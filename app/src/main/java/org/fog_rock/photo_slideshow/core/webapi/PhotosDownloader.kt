package org.fog_rock.photo_slideshow.core.webapi

import android.content.Context
import android.util.Log
import com.google.photos.types.proto.MediaItem
import com.google.photos.types.proto.MediaMetadata
import org.fog_rock.photo_slideshow.core.file.FileDownloader
import org.fog_rock.photo_slideshow.core.math.SizeCalculator
import java.io.File
import java.net.MalformedURLException
import java.net.URL

class PhotosDownloader(
    private val context: Context,
    private val aspectWidth: Long,
    private val aspectHeight: Long,
    private val fileDownloader: FileDownloader
) {

    private val TAG = PhotosDownloader::class.java.simpleName

    private val calculator = SizeCalculator()

    constructor(
        context: Context, aspectWidth: Long, aspectHeight: Long,
        connectionTimeoutMilliSecs: Long, readTimeoutMilliSecs: Long, writeTimeoutMilliSecs: Long
    ): this(
        context, aspectWidth, aspectHeight,
        FileDownloader(
            connectionTimeoutMilliSecs,
            readTimeoutMilliSecs,
            writeTimeoutMilliSecs
        )
    )

    constructor(
        context: Context, aspectWidth: Long, aspectHeight: Long, timeoutMilliSecs: Long
    ): this(
        context, aspectWidth, aspectHeight,
        FileDownloader(timeoutMilliSecs)
    )

    /**
     * メディアアイテムリストのURLから写真のダウンロードを行う.
     * UIスレッドからは呼び出さないこと.
     */
    fun doDownloads(mediaItems: List<MediaItem>): List<String> {
        val outputFiles = mutableListOf<String>()

        mediaItems.forEach {
            val outputFile = doDownload(it) ?: run {
                return@forEach
            }
            outputFiles.add(outputFile)
        }
        return outputFiles.toList()
    }

    /**
     * メディアアイテムのURLから写真のダウンロードを行う.
     * UIスレッドからは呼び出さないこと.
     */
    fun doDownload(mediaItem: MediaItem): String? {
        val metadata = getPhotoMetaData(mediaItem) ?: run {
            return null
        }
        val downloadUrl = getDownloadUrl(mediaItem.baseUrl, metadata) ?: run {
            return null
        }
        val outputFile = getOutputFile(mediaItem.filename)
        return if (fileDownloader.doDownload(downloadUrl, outputFile)) outputFile.path else null
    }

    private fun getPhotoMetaData(mediaItem: MediaItem): MediaMetadata? {
        if (!mediaItem.hasMediaMetadata()) {
            Log.i(TAG, "MediaItem does not have MetaData.")
            return null
        }
        val metadata = mediaItem.mediaMetadata
        if (metadata.metadataCase != MediaMetadata.MetadataCase.PHOTO) {
            Log.i(TAG, "MetaData is not case of photo.")
            return null
        }
        return metadata
    }

    private fun getDownloadUrl(baseUrl: String, metadata: MediaMetadata): URL? {
        val scale = calculator.estimateEffectiveScale(metadata.width, metadata.height, aspectWidth, aspectHeight)
        val width = (metadata.width * scale).toLong()
        val height = (metadata.height * scale).toLong()

        return try {
            URL("$baseUrl=w$width-h$height")
        } catch (e: MalformedURLException) {
            Log.e(TAG, "Invalid url format.")
            e.printStackTrace()
            null
        }
    }

    private fun getOutputFile(fileName: String) = File(context.filesDir, fileName)

}