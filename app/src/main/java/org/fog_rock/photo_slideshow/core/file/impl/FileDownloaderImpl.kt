package org.fog_rock.photo_slideshow.core.file.impl

import okhttp3.Request
import okhttp3.Response
import okhttp3.ResponseBody
import org.fog_rock.photo_slideshow.core.extension.logE
import org.fog_rock.photo_slideshow.core.extension.logI
import org.fog_rock.photo_slideshow.core.file.FileDownloader
import org.fog_rock.photo_slideshow.core.webapi.holder.SingletonWebHolder
import java.io.*
import java.net.URL

class FileDownloaderImpl(): FileDownloader {

    override suspend fun requestDownload(downloadUrl: URL, outputFile: File): Boolean {
        if (outputFile.exists()) {
            logI("OutputFile is already existed: $outputFile")
            return true
        }

        logI("Do download. DownloadURL: $downloadUrl, OutputFile: $outputFile")
        return doDownload(downloadUrl, outputFile)
    }

    /**
     * ファイルダウンロードを行う.
     */
    private fun doDownload(downloadUrl: URL, outputFile: File): Boolean =
        try {
            val request = Request.Builder().apply {
                url(downloadUrl)
            }.build()
            val response = SingletonWebHolder.okHttpClient.newCall(request).execute()
            writeOutputFile(response, outputFile)
        } catch (e: IOException) {
            logE("Failed network connection.")
            e.printStackTrace()
            false
        } catch (e: IllegalStateException) {
            logE("Request call has already been executed.")
            e.printStackTrace()
            false
        } catch (e: IllegalArgumentException ) {
            logE("Url scheme must be 'http' or 'https'.")
            e.printStackTrace()
            false
        }

    /**
     * レスポンスファイルに書き込む.
     * @param body レスポンスボディ
     * @param outputFile 出力先ファイル
     */
    private fun writeOutputFile(response: Response, outputFile: File): Boolean {
        if (!response.isSuccessful) {
            logE("Failed network connection. Code: ${response.code}")
            return false
        }
        val body = response.body ?: run {
            logE("Response body is null.")
            return false
        }
        return writeOutputFile(body, outputFile)
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
            logI("Succeeded to write output file.")
            return true
        } catch (e: IOException) {
            logE("Failed to write OutputStream.")
            e.printStackTrace()
        } catch (e: FileNotFoundException) {
            logE("Failed to open OutputStream.")
            e.printStackTrace()
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close()
                } catch (e: IOException) {
                    logE("Failed to close InputStream.")
                    e.printStackTrace()
                }
            }
            if (outputStream != null) {
                try {
                    outputStream.flush()
                    outputStream.close()
                } catch (e: IOException) {
                    logE("Failed to close OutputStream.")
                    e.printStackTrace()
                }
            }
        }
        return false
    }
}