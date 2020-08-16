package org.fog_rock.photo_slideshow.app.module.entity

import com.google.photos.types.proto.Album
import com.google.photos.types.proto.MediaItem
import org.fog_rock.photo_slideshow.core.database.entity.DisplayedPhoto

data class PhotoInfo(
    val album: Album,
    val mediaDetails: List<MediaDetail>
) {

    data class MediaDetail(
        val mediaItem: MediaItem,
        val outputPath: String
    )

    fun displayedPhotos(selectedAlbumId: Long): List<DisplayedPhoto> {
        val displayedPhotos = mutableListOf<DisplayedPhoto>()
        mediaDetails.forEach {
            displayedPhotos.add(DisplayedPhoto(selectedAlbumId, it.mediaItem, it.outputPath))
        }
        return displayedPhotos.toList()
    }
}