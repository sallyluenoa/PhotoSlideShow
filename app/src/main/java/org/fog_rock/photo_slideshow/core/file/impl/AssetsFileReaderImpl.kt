package org.fog_rock.photo_slideshow.core.file.impl

import android.content.Context
import org.fog_rock.photo_slideshow.core.extension.logE
import org.fog_rock.photo_slideshow.core.extension.logI
import org.fog_rock.photo_slideshow.core.file.AssetsFileReader
import java.io.FileNotFoundException
import java.io.IOException
import java.io.InputStream

/**
 * ファイル読み込み専用クラス.
 */
class AssetsFileReaderImpl(
    private val context: Context
): AssetsFileReader {

    override fun read(fileName: String): String? {
        logI("Read assets file: $fileName")

        var inputStream: InputStream? = null
        val assetManager = context.resources.assets

        try {
            inputStream = assetManager.open(fileName)
            return inputStream.bufferedReader().use { it.readText() }
        } catch (e : IOException) {
            logE("Failed to open or read InputStream.")
            e.printStackTrace()
        } catch (e : FileNotFoundException) {
            logE("Not found in assets path.")
            e.printStackTrace()
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close()
                } catch (e : IOException) {
                    logE("Failed to close InputStream.")
                    e.printStackTrace()
                }
            }
        }
        return null
    }
}