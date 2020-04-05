package org.fog_rock.photo_slideshow.core.file

import com.google.photos.types.proto.MediaItem

/**
 * 複数の写真をダウンロードするためのインターフェース.
 */
interface PhotosDownloader {

    /**
     * メディアアイテムリストの情報を元に、写真のダウンロードを行う.
     * コルーチン内で呼び出すこと.
     */
    suspend fun doDownloads(mediaItems: List<MediaItem>): List<String>
}