package org.fog_rock.photo_slideshow.core.database.impl

import com.google.photos.types.proto.MediaItem
import org.fog_rock.photo_slideshow.core.database.DisplayedPhotoDatabase
import org.fog_rock.photo_slideshow.core.database.dao.DisplayedPhotoDao
import org.fog_rock.photo_slideshow.core.database.entity.DisplayedPhoto
import org.fog_rock.photo_slideshow.core.database.room.SingletonRoomObject

class DisplayedPhotoDatabaseImpl: DisplayedPhotoDatabase {

    override suspend fun update(selectedAlbumId: Long, mediaItem: MediaItem) {
        val displayPhoto = dao().findBySelectedAlbumIdAndMediaItemId(selectedAlbumId, mediaItem.id)
        if (displayPhoto != null) {
            dao().update(displayPhoto.copy(mediaItem))
        } else {
            dao().insert(DisplayedPhoto(selectedAlbumId, mediaItem))
        }
    }

    private fun dao(): DisplayedPhotoDao = SingletonRoomObject.displayedPhotoDao()
}