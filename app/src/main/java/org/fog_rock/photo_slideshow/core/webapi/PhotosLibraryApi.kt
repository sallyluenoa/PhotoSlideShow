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

    fun getSharedAlbumList(): List<Album> =
        try {
            val pagedResponse = clientHolder.client.listSharedAlbums()
            val sharedAlbumsResponse = pagedResponse.page.response
            sharedAlbumsResponse.sharedAlbumsList
        } catch (e: ApiException) {
            Log.e(TAG, "Failed to get album response.")
            e.printStackTrace()
            listOf<Album>()
        }

    fun getMediaItemList(album: Album, maxListSize: Int): List<MediaItem> =
        try {
            val request = SearchMediaItemsRequest.newBuilder().apply {
                albumId = album.id
            }.build()
            val pagedResponse = clientHolder.client.searchMediaItems(request)
            val mediaItemsResponse = pagedResponse.page.response

            val count = mediaItemsResponse.mediaItemsCount
            Log.d(TAG, "mediaItem count: $count")
            val randomNumList = generateRandomNumberList(max(count - maxListSize, 0), count)
            val mediaItemList = mutableListOf<MediaItem>()
            randomNumList.forEach {
                val item = mediaItemsResponse.getMediaItems(it)
                Log.d(TAG, "$it: ${item.id}")
                mediaItemList.add(item)
            }
            mediaItemList.toList()
        } catch (e: ApiException) {
            Log.e(TAG, "Failed to get mediaItem response.")
            e.printStackTrace()
            listOf<MediaItem>()
        }

    private fun generateRandomNumberList(start: Int, end: Int): List<Int> {
        val list = mutableListOf<Int>()
        for (i in start until end) list.add(i)
        list.shuffle()
        return list.toList()
    }
}