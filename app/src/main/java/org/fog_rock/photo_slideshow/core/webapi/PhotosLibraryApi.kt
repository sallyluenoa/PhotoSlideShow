package org.fog_rock.photo_slideshow.core.webapi

import com.google.photos.types.proto.Album
import com.google.photos.types.proto.MediaItem

/**
 * Google Photos に関連するAPI
 * https://developers.google.com/photos/
 * https://google.github.io/java-photoslibrary/1.4.0/
 */
interface PhotosLibraryApi {

    /**
     * ClientHolderの更新.
     */
    fun updateClientHolder(clientHolder: PhotosLibraryClientHolder)

    /**
     * ClientHolderの有効期限が切れていないか確認する.
     */
    fun isAvailableClientHolder(): Boolean

    /**
     * アルバム取得要求.
     * コルーチン内で呼び出すこと.
     * @param albumId アルバムID
     */
    suspend fun requestAlbum(albumId: String): Album

    /**
     * メディアアイテム取得要求.
     * コルーチン内で呼び出すこと.
     * @param mediaItemId メディアアイテムID
     */
    suspend fun requestMediaItem(mediaItemId: String): MediaItem

    /**
     * アルバムリストの更新要求.
     * コルーチン内で呼び出すこと.
     */
    suspend fun requestUpdateAlbums(albums: List<Album>): List<Album>

    /**
     * メディアアイテムリスト更新要求.
     * コルーチン内で呼び出すこと.
     */
    suspend fun requestUpdateMediaItems(mediaItems: List<MediaItem>): List<MediaItem>

    /**
     * 共有アルバムリスト取得要求.
     * コルーチン内で呼び出すこと.
     */
    suspend fun requestSharedAlbums(): List<Album>

    /**
     * アルバム内のアイテムリスト取得要求.
     * コルーチン内で呼び出すこと.
     */
    suspend fun requestMediaItems(album: Album): List<MediaItem>
}