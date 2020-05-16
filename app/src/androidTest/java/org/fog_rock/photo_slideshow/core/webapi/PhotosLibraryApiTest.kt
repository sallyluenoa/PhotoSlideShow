package org.fog_rock.photo_slideshow.core.webapi

import android.util.Log
import androidx.test.platform.app.InstrumentationRegistry
import com.google.photos.types.proto.Album
import com.google.photos.types.proto.MediaItem
import kotlinx.coroutines.runBlocking
import org.fog_rock.photo_slideshow.core.webapi.client.PhotosLibraryClientHolder
import org.fog_rock.photo_slideshow.core.webapi.entity.TokenInfo
import org.fog_rock.photo_slideshow.core.webapi.impl.PhotosLibraryApiImpl
import org.fog_rock.photo_slideshow.test.R
import org.junit.Before
import org.junit.Test

/**
 * PhotosLibraryApiテスト
 * Googleアカウントでアプリ内にサインインして、アクセストークンを取得した状態でテストすること.
 */
class PhotosLibraryApiTest {

    companion object {
        private val TAG = PhotosLibraryApiTest::class.java.simpleName
    }

    private val testContext = InstrumentationRegistry.getInstrumentation().context

    private val tokenInfo = TokenInfo(
        testContext.getString(R.string.access_token),
        null,
        testContext.getString(R.string.expired_access_token_time_millis).toLong()
    )

    private var clientHolder = PhotosLibraryClientHolder(tokenInfo)

    private val photosApi: PhotosLibraryApi = PhotosLibraryApiImpl(clientHolder)

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
        val albumId = testContext.getString(R.string.album_id_1)
        val album = runBlocking {
            photosApi.requestAlbum(albumId)
        }
        showAlbum(album)
    }

    @Test
    fun requestMediaItem() {
        val mediaItemId = testContext.getString(R.string.album_id_2)
        val mediaItem = runBlocking {
            photosApi.requestMediaItem(mediaItemId)
        }
        showMediaItem(mediaItem)
    }

    @Test
    fun requestUpdateAlbums() {
        val albums = listOf(
            generateAlbum(testContext.getString(R.string.album_id_1)),
            generateAlbum(testContext.getString(R.string.album_id_2)),
            generateAlbum(testContext.getString(R.string.album_id_3))
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
            generateMediaItem(testContext.getString(R.string.media_item_id_1)),
            generateMediaItem(testContext.getString(R.string.media_item_id_2)),
            generateMediaItem(testContext.getString(R.string.media_item_id_3))
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
        val album = generateAlbum(testContext.getString(R.string.album_id_1))
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