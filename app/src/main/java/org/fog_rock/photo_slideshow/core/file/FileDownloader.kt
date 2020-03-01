package org.fog_rock.photo_slideshow.core.file

import android.util.Log
import okhttp3.*
import java.io.*
import java.net.URL
import java.util.concurrent.TimeUnit

/**
 * ファイルダウンロード専用クラス.
 */
class FileDownloader(
    connectionTimeoutMilliSecs: Long,
    readTimeoutMilliSecs: Long,
    writeTimeoutMilliSecs: Long,
    private val callback: Callback
) {
    private val TAG = FileDownloader::class.java.simpleName

    interface Callback {
        /**
         * ダウンロード結果を返す.
         * @param resultOutputFile ダウンロードに成功した場合は出力先ファイル、失敗した場合はNULL
         * @see doDownload
         */
        fun downloadResult(resultOutputFile: File?)
    }

    private val client = OkHttpClient.Builder().apply {
        connectTimeout(connectionTimeoutMilliSecs, TimeUnit.MILLISECONDS)
        readTimeout(readTimeoutMilliSecs, TimeUnit.MILLISECONDS)
        writeTimeout(writeTimeoutMilliSecs, TimeUnit.MILLISECONDS)
    }.build()

    /**
     * ファイルダウンロードを行う.
     * @param downloadUrl ダウンロードURL
     * @param outputFile 出力先ファイル
     * @see Callback.downloadResult
     */
    fun doDownload(downloadUrl: URL, outputFile: File) {
        Log.i(TAG, "Do download. DownloadURL: $downloadUrl, OutputFile: $outputFile")

        if (outputFile.exists()) {
            Log.i(TAG, "OutputFile is already existed.")
            callback.downloadResult(outputFile)
            return
        }

        val request = Request.Builder().apply {
            url(downloadUrl)
        }.build()

        client.newCall(request).enqueue(object : okhttp3.Callback {
            override fun onResponse(call: Call, response: Response) {
                Log.i(TAG, "Succeeded to get http response.")
                val body = response.body ?: run {
                    Log.e(TAG, "Cannot get response body.")
                    callback.downloadResult(null)
                    return
                }
                if (writeOutputFile(body, outputFile)) {
                    Log.i(TAG, "Succeeded to write output file.")
                    callback.downloadResult(outputFile)
                } else {
                    Log.e(TAG, "Failed to write output file.")
                    callback.downloadResult(null)
                }
            }

            override fun onFailure(call: Call, e: IOException) {
                Log.e(TAG, "Failed to get http response.")
                callback.downloadResult(null)
            }
        })
    }

    /**
     * レスポンスボディからファイルに書き込む.
     * @param body レスポンスボディ
     * @param outputFile 出力先ファイル
     */
    private fun writeOutputFile(body: ResponseBody, outputFile: File): Boolean {
        var inputStream: InputStream? = null
        var outputStream: OutputStream? = null

        try {
            inputStream = body.byteStream()
            outputStream = FileOutputStream(outputFile)
            inputStream.use { it.copyTo(outputStream) }
            return true
        } catch (e: IOException) {
            Log.e(TAG, "Failed to write OutputStream.")
            e.printStackTrace()
        } catch (e: FileNotFoundException) {
            Log.e(TAG, "Failed to open OutputStream.")
            e.printStackTrace()
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close()
                } catch (e: IOException) {
                    Log.e(TAG, "Failed to close InputStream.")
                    e.printStackTrace()
                }
            }
            if (outputStream != null) {
                try {
                    outputStream.flush()
                    outputStream.close()
                } catch (e: IOException) {
                    Log.e(TAG, "Failed to close OutputStream.")
                    e.printStackTrace()
                }
            }
        }
        return false
    }
}