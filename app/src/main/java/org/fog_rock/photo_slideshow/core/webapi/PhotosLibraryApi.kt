package org.fog_rock.photo_slideshow.core.webapi

import com.google.photos.types.proto.Album
import com.google.photos.types.proto.MediaItem

interface PhotosLibraryApi {

    /**
     * 共有アルバムリスト取得要求.
     * コルーチン内で呼び出すこと.
     */
    suspend fun requestSharedAlbums(): List<Album>

    /**
     * アルバム内のアイテムリスト取得要求.
     * コルーチン内で呼び出すこと.
     */
    suspend fun requestMediaItems(album: Album, maxSize: Int): List<MediaItem>
}