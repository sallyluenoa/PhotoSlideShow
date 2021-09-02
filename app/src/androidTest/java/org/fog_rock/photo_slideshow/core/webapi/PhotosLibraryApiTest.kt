package org.fog_rock.photo_slideshow.core.webapi

import com.google.photos.types.proto.Album
import com.google.photos.types.proto.MediaItem
import kotlinx.coroutines.runBlocking
import org.fog_rock.photo_slideshow.core.extension.logE
import org.fog_rock.photo_slideshow.core.extension.logI
import org.fog_rock.photo_slideshow.core.webapi.holder.SingletonWebHolder
import org.fog_rock.photo_slideshow.core.webapi.impl.PhotosLibraryApiImpl
import org.fog_rock.photo_slideshow.test.AndroidTestModuleGenerator
import org.junit.Before
import org.junit.Test

/**
 * PhotosLibraryApiテスト
 * Googleアカウントでアプリ内にサインインして、アクセストークンを取得した状態でテストすること.
 */
class PhotosLibraryApiTest {

    private val photosLibraryApi: PhotosLibraryApi = PhotosLibraryApiImpl()

    /**
     * テスト前にアクセストークンが有効か確認する.
     */
    @Before
    fun updatePhotosLibraryClient() {
        val tokenInfo = AndroidTestModuleGenerator.tokenInfo()
        assert(!tokenInfo.isAvailableAccessToken()) {
            logE("ClientHolder is not available. AccessToken should be updated.")
        }
        SingletonWebHolder.updatePhotosLibraryClient(tokenInfo)
    }

    @Test
    fun requestAlbum() {
        val album = runBlocking {
            photosLibraryApi.requestAlbum(AndroidTestModuleGenerator.albumId())
        }
        showAlbum(album)
    }

    @Test
    fun requestMediaItem() {
        val mediaItem = runBlocking {
            photosLibraryApi.requestMediaItem(AndroidTestModuleGenerator.mediaItemId())
        }
        showMediaItem(mediaItem)
    }

    @Test
    fun requestUpdateAlbums() {
        val newAlbums = runBlocking {
            val albums = listOf(
                AndroidTestModuleGenerator.album(1),
                AndroidTestModuleGenerator.album(2),
                AndroidTestModuleGenerator.album(3)
            )
            photosLibraryApi.requestUpdateAlbums(albums)
        }
        newAlbums.forEach { showAlbum(it) }
    }

    @Test
    fun requestUpdateMediaItems() {
        val newMediaItems = runBlocking {
            val mediaItems = listOf(
                AndroidTestModuleGenerator.mediaItem(1),
                AndroidTestModuleGenerator.mediaItem(2),
                AndroidTestModuleGenerator.mediaItem(3)
            )
            photosLibraryApi.requestUpdateMediaItems(mediaItems)
        }
        newMediaItems.forEach { showMediaItem(it) }
    }

    @Test
    fun requestSharedAlbums() {
        val albums = runBlocking {
            photosLibraryApi.requestSharedAlbums()
        }
        logI("[Albums Result] Count: ${albums.size}")
        albums.forEach { showAlbum(it) }
    }

    @Test
    fun requestMediaItems() {
        val mediaItems = runBlocking {
            photosLibraryApi.requestMediaItems(AndroidTestModuleGenerator.album())
        }
        logI("[MediaItems Result] Count: ${mediaItems.size}")
        mediaItems.forEach {
            showMediaItem(it)
        }
    }

    private fun showAlbum(album: Album) {
        logI("ID: ${album.id}, Title: ${album.title}, Count: ${album.mediaItemsCount}")
    }

    private fun showMediaItem(mediaItem: MediaItem) {
        logI("ID: ${mediaItem.id}, FileName: ${mediaItem.filename}, BaseUrl: ${mediaItem.baseUrl}")
    }
}