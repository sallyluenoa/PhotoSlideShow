package org.fog_rock.photo_slideshow.core.webapi

import android.util.Log
import com.google.photos.types.proto.Album
import com.google.photos.types.proto.MediaItem
import kotlinx.coroutines.runBlocking
import org.fog_rock.photo_slideshow.core.webapi.impl.PhotosLibraryApiImpl
import org.junit.Before
import org.junit.Test

/**
 * PhotosLibraryApiテスト
 * Googleアカウントでアプリ内にサインインして、アクセストークンを取得した状態でテストすること.
 */
class PhotosLibraryApiTest {

    companion object {
        /**
         * それぞれ必要な情報を適宜更新すること.
         */
        private const val ACCESS_TOKEN = ""
        private const val EXPIRED_ACCESS_TOKEN_TIME_MILLIS = 0L

        private const val ALBUM_ID1 = ""
        private const val ALBUM_ID2 = ""
        private const val ALBUM_ID3 = ""

        private const val MEDIA_ITEM_ID1 = ""
        private const val MEDIA_ITEM_ID2 = ""
        private const val MEDIA_ITEM_ID3 = ""
    }

    private val TAG = PhotosLibraryApiTest::class.java.simpleName

    private val tokenInfo = GoogleOAuth2Api.TokenInfo(ACCESS_TOKEN, null, EXPIRED_ACCESS_TOKEN_TIME_MILLIS)

    private var clientHolder = PhotosLibraryClientHolder(tokenInfo)

    private val photosApi = PhotosLibraryApiImpl(clientHolder)

    /**
     * テスト前にアクセストークンが有効か確認する.
     */
    @Before
    fun configClientHolder() {
        assert(!photosApi.isAvailableClientHolder()) {
            Log.e(TAG, "ClientHolder is not available. AccessToken should be updated.")
        }
    }

    @Test
    fun requestAlbum() {
        val albumId = ALBUM_ID1
        val album = runBlocking {
            photosApi.requestAlbum(albumId)
        }
        showAlbum(album)
    }

    @Test
    fun requestMediaItem() {
        val mediaItemId = MEDIA_ITEM_ID1
        val mediaItem = runBlocking {
            photosApi.requestMediaItem(mediaItemId)
        }
        showMediaItem(mediaItem)
    }

    @Test
    fun requestUpdateAlbums() {
        val albums = listOf(
            generateAlbum(ALBUM_ID1),
            generateAlbum(ALBUM_ID2),
            generateAlbum(ALBUM_ID3)
        )
        val newAlbums = runBlocking {
            photosApi.requestUpdateAlbums(albums)
        }
        newAlbums.forEach {
            showAlbum(it)
        }
    }

    @Test
    fun requestUpdateMediaItems() {
        val mediaItems = listOf(
            generateMediaItem(MEDIA_ITEM_ID1),
            generateMediaItem(MEDIA_ITEM_ID2),
            generateMediaItem(MEDIA_ITEM_ID3)
        )
        val newMediaItems = runBlocking {
            photosApi.requestUpdateMediaItems(mediaItems)
        }
        newMediaItems.forEach {
            showMediaItem(it)
        }
    }

    @Test
    fun requestSharedAlbums() {
        val albums = runBlocking {
            photosApi.requestSharedAlbums()
        }
        Log.i(TAG, "[Albums Result] Count: ${albums.size}")
        albums.forEach {
            showAlbum(it)
        }
    }

    @Test
    fun requestMediaItems() {
        val album = generateAlbum(ALBUM_ID3)
        val mediaItems = runBlocking {
            photosApi.requestMediaItems(album)
        }
        Log.i(TAG, "[MediaItems Result] Count: ${mediaItems.size}")
        mediaItems.forEach {
            showMediaItem(it)
        }
    }

    private fun generateAlbum(albumId: String): Album =
        Album.newBuilder().apply { id = albumId }.build()

    private fun generateMediaItem(mediaItemId: String): MediaItem =
        MediaItem.newBuilder().apply { id = mediaItemId }.build()

    private fun showAlbum(album: Album) {
        Log.i(TAG, "ID: ${album.id}, Title: ${album.title}, Count: ${album.mediaItemsCount}")
    }

    private fun showMediaItem(mediaItem: MediaItem) {
        Log.i(TAG, "ID: ${mediaItem.id}, FileName: ${mediaItem.filename}, BaseUrl: ${mediaItem.baseUrl}")
    }
}