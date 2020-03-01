package org.fog_rock.photo_slideshow.core.file

import android.content.Context
import android.util.Log
import java.io.IOException
import java.io.InputStream

/**
 * ファイル読み込み専用クラス.
 */
class FileReader(private val context: Context) {

    private val TAG = FileReader::class.java.simpleName

    /**
     * assets 以下に設置されているファイルの読み込みを行う.
     * @param fileName ファイル名
     * @return ファイルの読み込みに成功した場合はファイル内の文字列、失敗した場合はNULL
     */
    fun readAssetsFile(fileName: String): String? {
        var inputStream: InputStream? = null
        val assetManager = context.resources.assets
        return try {
            inputStream = assetManager.open(fileName)
            inputStream.bufferedReader().use { it.readText() }
        } catch (e : IOException) {
            Log.e(TAG, "Failed to open or read InputStream.")
            e.printStackTrace()
            null
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close()
                } catch (e : IOException) {
                    Log.e(TAG, "Failed to close InputStream.")
                    e.printStackTrace()
                }
            }
        }
    }
}