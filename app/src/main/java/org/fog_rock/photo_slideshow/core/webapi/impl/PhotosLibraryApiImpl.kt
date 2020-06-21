package org.fog_rock.photo_slideshow.core.webapi.impl

import com.google.android.gms.common.api.ApiException
import com.google.photos.library.v1.proto.SearchMediaItemsRequest
import com.google.photos.types.proto.Album
import com.google.photos.types.proto.MediaItem
import org.fog_rock.photo_slideshow.core.extension.logE
import org.fog_rock.photo_slideshow.core.extension.logI
import org.fog_rock.photo_slideshow.core.webapi.PhotosLibraryApi
import org.fog_rock.photo_slideshow.core.webapi.client.PhotosLibraryClientHolder

class PhotosLibraryApiImpl(
    private var clientHolder: PhotosLibraryClientHolder
): PhotosLibraryApi {

    override fun updateClientHolder(clientHolder: PhotosLibraryClientHolder) {
        this.clientHolder = clientHolder
    }

    override fun isAvailableClientHolder(): Boolean = clientHolder.isAvailable()

    override suspend fun requestAlbum(albumId: String): Album =
        try {
            clientHolder.client.getAlbum(albumId)
        } catch (e: ApiException) {
            logE("Failed to get album.")
            e.printStackTrace()
            Album.newBuilder().apply { id = albumId }.build()
        }

    override suspend fun requestMediaItem(mediaItemId: String): MediaItem =
        try {
            clientHolder.client.getMediaItem(mediaItemId)
        } catch (e: ApiException) {
            logE("Failed to get mediaItem.")
            e.printStackTrace()
            MediaItem.newBuilder().apply { id = mediaItemId }.build()
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

    override suspend fun requestSharedAlbums(): List<Album> =
        try {
            val response = clientHolder.client.listSharedAlbums()
            val albums = emptyList<Album>()
            for (page in response.iteratePages()) {
                logI("Load ListSharedAlbumsPage. PageCount: ${page.pageElementCount}");
                albums.plus(page.response.sharedAlbumsList)
            }
            albums
        } catch (e: ApiException) {
            logE("Failed to get listSharedAlbums.")
            e.printStackTrace()
            emptyList()
        }

    override suspend fun requestMediaItems(album: Album): List<MediaItem> =
        try {
            val request = SearchMediaItemsRequest.newBuilder().apply {
                albumId = album.id
                pageSize = 100
            }.build()
            val response = clientHolder.client.searchMediaItems(request)
            val mediaItems = emptyList<MediaItem>()
            logI("Total mediaItem count: ${album.mediaItemsCount}")
            for (page in response.iteratePages()) {
                logI("Load SearchMediaItemsPage. PageCount: ${page.pageElementCount}")
                mediaItems.plus(page.response.mediaItemsList)
            }
            mediaItems
        } catch (e: ApiException) {
            logE("Failed to get searchMediaItems.")
            e.printStackTrace()
            emptyList()
        }
}