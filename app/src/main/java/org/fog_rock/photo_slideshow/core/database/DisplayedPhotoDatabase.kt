package org.fog_rock.photo_slideshow.core.database

import com.google.photos.types.proto.MediaItem

interface DisplayedPhotoDatabase {

    suspend fun update(selectedAlbumId: Long, mediaItem: MediaItem)

    suspend fun update(selectedAlbumId: Long, mediaItems: List<MediaItem>)
}