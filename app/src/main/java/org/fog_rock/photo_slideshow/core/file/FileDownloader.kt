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
    writeTimeoutMilliSecs: Long
) {
    private val TAG = FileDownloader::class.java.simpleName

    private val client = OkHttpClient.Builder().apply {
        connectTimeout(connectionTimeoutMilliSecs, TimeUnit.MILLISECONDS)
        readTimeout(readTimeoutMilliSecs, TimeUnit.MILLISECONDS)
        writeTimeout(writeTimeoutMilliSecs, TimeUnit.MILLISECONDS)
    }.build()

    constructor(timeoutMilliSecs: Long): this(timeoutMilliSecs, timeoutMilliSecs, timeoutMilliSecs)

    /**
     * ファイルダウンロードを行う. UIスレッドから呼び出さないこと.
     * @param downloadUrl ダウンロードURL
     * @param outputFile 出力先ファイル
     * @return ダウンロード成功やすでに存在などによりファイルが存在する場合はtrue、それ以外はfalse
     */
    fun doDownload(downloadUrl: URL, outputFile: File): Boolean {
        Log.i(TAG, "Do download. DownloadURL: $downloadUrl, OutputFile: $outputFile")

        if (outputFile.exists()) {
            Log.i(TAG, "OutputFile is already existed.")
            return true
        }

        try {
            val request = Request.Builder().apply {
                url(downloadUrl)
            }.build()
            val response = client.newCall(request).execute()
            val body = response.body ?: run {
                Log.e(TAG, "Cannot get response body.")
                return false
            }
            return writeOutputFile(body, outputFile)
        } catch (e: IOException) {
            e.printStackTrace()
        } catch (e: IllegalStateException) {
            e.printStackTrace()
        }

        return false
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
            Log.i(TAG, "Succeeded to write output file.")
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