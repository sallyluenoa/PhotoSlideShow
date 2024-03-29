package org.fog_rock.photo_slideshow.app.module.lib.impl

import com.google.photos.types.proto.MediaItem
import com.google.photos.types.proto.MediaMetadata
import org.fog_rock.photo_slideshow.app.module.lib.PhotosDownloader
import org.fog_rock.photo_slideshow.core.extension.logE
import org.fog_rock.photo_slideshow.core.extension.logI
import org.fog_rock.photo_slideshow.core.file.FileDownloader
import org.fog_rock.photo_slideshow.core.math.SizeCalculator
import java.io.File
import java.net.MalformedURLException
import java.net.URL

class PhotosDownloaderImpl(
    private val fileDownloader: FileDownloader,
    private val sizeCalculator: SizeCalculator,
    private val aspectWidth: Long,
    private val aspectHeight: Long
): PhotosDownloader {

    override suspend fun requestDownloads(mediaItems: List<MediaItem>, outputDir: File): List<String> {
        val outputFiles = mutableListOf<String>()
        if (!isValidOutputDir(outputDir)) {
            logE("Invalid output dir.")
            return outputFiles
        }
        mediaItems.forEach {
            val outputFile = doDownload(it, outputDir)
            if (outputFile != null) outputFiles.add(outputFile)
        }
        return outputFiles.toList()
    }

    /**
     * メディアアイテムリストの情報を元に、写真のダウンロードを行う.
     */
    private suspend fun doDownload(mediaItem: MediaItem, outputDir: File): String? {
        val metadata = getPhotoMetaData(mediaItem) ?: return null
        val downloadUrl = getDownloadUrl(mediaItem.baseUrl, metadata) ?: return null
        val outputFile = File(outputDir, mediaItem.filename)
        return if (fileDownloader.requestDownload(downloadUrl, outputFile)) outputFile.path else null
    }

    /**
     * 写真のメタデータを取得.
     */
    private fun getPhotoMetaData(mediaItem: MediaItem): MediaMetadata? {
        if (!mediaItem.hasMediaMetadata()) {
            logI("MediaItem does not have MetaData.")
            return null
        }
        val metadata = mediaItem.mediaMetadata
        if (metadata.metadataCase != MediaMetadata.MetadataCase.PHOTO) {
            logI("MetaData is not case of photo.")
            return null
        }
        return metadata
    }

    /**
     * ダウンロードURLを取得.
     */
    private fun getDownloadUrl(baseUrl: String, metadata: MediaMetadata): URL? {
        val scale = sizeCalculator.estimateEffectiveScale(
            metadata.width, metadata.height, aspectWidth, aspectHeight)
        val width = (metadata.width * scale).toLong()
        val height = (metadata.height * scale).toLong()

        return try {
            URL("$baseUrl=w$width-h$height")
        } catch (e: MalformedURLException) {
            logE("Invalid url format.")
            e.printStackTrace()
            null
        }
    }

    /**
     * 出力先ディレクトリが存在するか確認.
     */
    private fun isValidOutputDir(outputDir: File): Boolean {
        if (!outputDir.exists()) {
            logE("Output dir path does not exist.")
            return false
        }
        if (!outputDir.isDirectory) {
            logE("Output dir path is not directory.")
            return false
        }
        return true
    }

}