package org.fog_rock.photo_slideshow.core.file.impl

import android.content.Context
import android.util.Log
import org.fog_rock.photo_slideshow.core.file.FileReader
import java.io.FileNotFoundException
import java.io.IOException
import java.io.InputStream

/**
 * ファイル読み込み専用クラス.
 */
class FileReaderImpl(
    private val context: Context
): FileReader {

    private val TAG = FileReaderImpl::class.java.simpleName

    override fun readAssetsFile(fileName: String): String? {
        var inputStream: InputStream? = null
        val assetManager = context.resources.assets

        try {
            inputStream = assetManager.open(fileName)
            return inputStream.bufferedReader().use { it.readText() }
        } catch (e : IOException) {
            Log.e(TAG, "Failed to open or read InputStream.")
            e.printStackTrace()
        } catch (e : FileNotFoundException) {
            Log.e(TAG, "Not found in assets path.")
            e.printStackTrace()
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
        return null
    }
}