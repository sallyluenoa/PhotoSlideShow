package org.fog_rock.photo_slideshow.app.module.lib

import com.google.photos.types.proto.MediaItem
import java.io.File

/**
 * 複数の写真をダウンロードするためのインターフェース.
 */
interface PhotosDownloader {

    /**
     * メディアアイテムリストの情報を元に、写真のダウンロードを行う.
     * コルーチン内で呼び出すこと.
     */
    suspend fun requestDownloads(mediaItems: List<MediaItem>, outputDir: File): List<String>
}