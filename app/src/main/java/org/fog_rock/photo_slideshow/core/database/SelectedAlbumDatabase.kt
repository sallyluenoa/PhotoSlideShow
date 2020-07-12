package org.fog_rock.photo_slideshow.core.database

import com.google.photos.types.proto.Album
import org.fog_rock.photo_slideshow.core.database.entity.SelectedAlbumWithCoveredPhoto
import org.fog_rock.photo_slideshow.core.database.entity.SelectedAlbumWithDisplayedPhotos

interface SelectedAlbumDatabase {

    suspend fun update(userInfoId: Long, album: Album)

    suspend fun delete(userInfoId: Long, albumId: String)

    suspend fun findWithCoveredPhoto(albumId: String): SelectedAlbumWithCoveredPhoto?

    suspend fun findWithDisplayedPhotos(albumId: String): SelectedAlbumWithDisplayedPhotos?
}