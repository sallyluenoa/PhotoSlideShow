package org.fog_rock.photo_slideshow.core.file.impl

import android.util.Log
import com.google.photos.types.proto.MediaItem
import com.google.photos.types.proto.MediaMetadata
import org.fog_rock.photo_slideshow.core.file.FileDownloader
import org.fog_rock.photo_slideshow.core.file.PhotosDownloader
import org.fog_rock.photo_slideshow.core.math.SizeCalculator
import java.io.File
import java.net.MalformedURLException
import java.net.URL

class PhotosDownloaderImpl(
    private val fileDownloader: FileDownloader,
    private val outputDir: File,
    private val aspectWidth: Long,
    private val aspectHeight: Long
): PhotosDownloader {

    private val TAG = PhotosDownloaderImpl::class.java.simpleName

    private val calculator = SizeCalculator()

    override suspend fun doDownloads(mediaItems: List<MediaItem>): List<String> {
        val outputFiles = mutableListOf<String>()

        mediaItems.forEach {
            val outputFile = doDownload(it)
            if (outputFile != null) outputFiles.add(outputFile)
        }
        return outputFiles.toList()
    }

    /**
     * メディアアイテムリストの情報を元に、写真のダウンロードを行う.
     */
    private suspend fun doDownload(mediaItem: MediaItem): String? {
        val metadata = getPhotoMetaData(mediaItem) ?: return null
        val downloadUrl = getDownloadUrl(mediaItem.baseUrl, metadata) ?: return null
        val outputFile = getOutputFile(mediaItem.filename) ?: return null
        return if (fileDownloader.requestDownload(downloadUrl, outputFile)) outputFile.path else null
    }

    /**
     * 写真のメタデータを取得.
     */
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

    /**
     * ダウンロードURLを取得.
     */
    private fun getDownloadUrl(baseUrl: String, metadata: MediaMetadata): URL? {
        val scale = calculator.estimateEffectiveScale(
            metadata.width, metadata.height, aspectWidth, aspectHeight)
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

    /**
     * 出力先ファイルを取得.
     */
    private fun getOutputFile(fileName: String): File? {
        if (!outputDir.exists()) {
            Log.e(TAG, "Output dir path does not exist.")
            return null
        }
        if (!outputDir.isDirectory) {
            Log.e(TAG, "Output dir path is not directory.")
            return null
        }
        return File(outputDir, fileName)
    }

}