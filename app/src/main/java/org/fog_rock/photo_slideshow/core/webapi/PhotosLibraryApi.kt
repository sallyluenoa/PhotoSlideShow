package org.fog_rock.photo_slideshow.core.webapi

import android.content.Context
import android.util.Log
import com.google.android.gms.common.api.ApiException
import com.google.photos.library.v1.proto.SearchMediaItemsRequest
import com.google.photos.types.proto.Album
import com.google.photos.types.proto.MediaItem
import kotlin.math.max

/**
 * Google Photos に関連するAPI
 */
class PhotosLibraryApi(
    private val context: Context,
    accessToken: String) {

    private val TAG = PhotosLibraryApi::class.java.simpleName

    private val clientHolder = PhotosLibraryClientHolder(accessToken)

    fun getSharedAlbums(): List<Album> =
        try {
            val pagedResponse = clientHolder.client.listSharedAlbums()
            val sharedAlbumsResponse = pagedResponse.page.response
            sharedAlbumsResponse.sharedAlbumsList
        } catch (e: ApiException) {
            Log.e(TAG, "Failed to get album response.")
            e.printStackTrace()
            listOf<Album>()
        }

    fun getMediaItems(album: Album, maxSize: Int): List<MediaItem> =
        try {
            val request = SearchMediaItemsRequest.newBuilder().apply {
                albumId = album.id
                pageSize = 100
            }.build()
            val pagedResponse = clientHolder.client.searchMediaItems(request)
            val mediaItemsResponse = pagedResponse.page.response

            val count = mediaItemsResponse.mediaItemsCount
            Log.d(TAG, "mediaItem count: $count")
            val randNumbers = generateRandomNumbers(max(count - maxSize, 0), count)
            val mediaItems = mutableListOf<MediaItem>()
            randNumbers.forEach {
                val item = mediaItemsResponse.getMediaItems(it)
                Log.d(TAG, "$it: ${item.id}")
                mediaItems.add(item)
            }
            mediaItems.toList()
        } catch (e: ApiException) {
            Log.e(TAG, "Failed to get mediaItem response.")
            e.printStackTrace()
            listOf<MediaItem>()
        }

    private fun generateRandomNumbers(start: Int, end: Int): List<Int> {
        val numbers = mutableListOf<Int>()
        for (i in start until end) numbers.add(i)
        numbers.shuffle()
        return numbers.toList()
    }
}