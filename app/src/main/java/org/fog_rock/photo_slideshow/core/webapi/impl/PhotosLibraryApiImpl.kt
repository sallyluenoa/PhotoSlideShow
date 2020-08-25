package org.fog_rock.photo_slideshow.core.webapi.impl

import com.google.android.gms.common.api.ApiException
import com.google.photos.library.v1.PhotosLibraryClient
import com.google.photos.library.v1.proto.SearchMediaItemsRequest
import com.google.photos.types.proto.Album
import com.google.photos.types.proto.MediaItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.fog_rock.photo_slideshow.core.extension.logE
import org.fog_rock.photo_slideshow.core.extension.logI
import org.fog_rock.photo_slideshow.core.webapi.PhotosLibraryApi
import org.fog_rock.photo_slideshow.core.webapi.entity.TokenInfo
import org.fog_rock.photo_slideshow.core.webapi.holder.SingletonWebHolder

class PhotosLibraryApiImpl(): PhotosLibraryApi {

    override suspend fun requestAlbum(albumId: String): Album = withContext(Dispatchers.IO) {
        try {
            photosLibraryClient().getAlbum(albumId)
        } catch (e: ApiException) {
            logE("Failed to get album.")
            e.printStackTrace()
            Album.newBuilder().apply { id = albumId }.build()
        }
    }

    override suspend fun requestMediaItem(mediaItemId: String): MediaItem = withContext(Dispatchers.IO) {
        try {
            photosLibraryClient().getMediaItem(mediaItemId)
        } catch (e: ApiException) {
            logE("Failed to get mediaItem.")
            e.printStackTrace()
            MediaItem.newBuilder().apply { id = mediaItemId }.build()
        }
    }

    override suspend fun requestUpdateAlbums(albums: List<Album>): List<Album> {
        val newAlbums = emptyList<Album>().toMutableList()
        albums.forEach {
            newAlbums.add(requestAlbum(it.id))
        }
        return newAlbums.toList()
    }

    override suspend fun requestUpdateMediaItems(mediaItems: List<MediaItem>): List<MediaItem> {
        val newMediaItems = emptyList<MediaItem>().toMutableList()
        mediaItems.forEach {
            newMediaItems.add(requestMediaItem(it.id))
        }
        return newMediaItems.toList()
    }

    override suspend fun requestSharedAlbums(): List<Album> = withContext(Dispatchers.IO) {
        try {
            val response = photosLibraryClient().listSharedAlbums()
            val albums = mutableListOf<Album>()
            for (page in response.iteratePages()) {
                logI("Load ListSharedAlbumsPage. PageCount: ${page.pageElementCount}")
                albums.addAll(page.response.sharedAlbumsList)
            }
            albums.toList()
        } catch (e: ApiException) {
            logE("Failed to get listSharedAlbums.")
            e.printStackTrace()
            emptyList<Album>()
        }
    }

    override suspend fun requestMediaItems(album: Album): List<MediaItem> = withContext(Dispatchers.IO) {
        try {
            val request = SearchMediaItemsRequest.newBuilder().apply {
                albumId = album.id
                pageSize = 100
            }.build()
            val response = photosLibraryClient().searchMediaItems(request)
            val mediaItems = mutableListOf<MediaItem>()
            logI("Total mediaItem count: ${album.mediaItemsCount}")
            for (page in response.iteratePages()) {
                logI("Load SearchMediaItemsPage. PageCount: ${page.pageElementCount}")
                mediaItems.addAll(page.response.mediaItemsList)
            }
            mediaItems.toList()
        } catch (e: ApiException) {
            logE("Failed to get searchMediaItems.")
            e.printStackTrace()
            emptyList<MediaItem>()
        }
    }

    override fun updatePhotosLibraryClient(tokenInfo: TokenInfo?) =
        SingletonWebHolder.updatePhotosLibraryClient(tokenInfo)

    override fun currentTokenInfo(): TokenInfo = SingletonWebHolder.tokenInfo

    private fun photosLibraryClient(): PhotosLibraryClient =
        SingletonWebHolder.photosLibraryClient ?:
        throw NullPointerException("PhotosLibraryClient is null.")
}