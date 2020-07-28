package org.fog_rock.photo_slideshow.core.database.impl

import com.google.photos.types.proto.Album
import org.fog_rock.photo_slideshow.core.database.SelectedAlbumDatabase
import org.fog_rock.photo_slideshow.core.database.dao.SelectedAlbumDao
import org.fog_rock.photo_slideshow.core.database.entity.SelectedAlbum
import org.fog_rock.photo_slideshow.core.database.entity.SelectedAlbumWithCoveredPhoto
import org.fog_rock.photo_slideshow.core.database.entity.SelectedAlbumWithDisplayedPhotos
import org.fog_rock.photo_slideshow.core.database.room.SingletonRoomObject

class SelectedAlbumDatabaseImpl: SelectedAlbumDatabase {

    override suspend fun update(userInfoId: Long, album: Album) {
        val selectedAlbum = dao().findByUserInfoIdAndAlbumId(userInfoId, album.id)
        if (selectedAlbum != null) {
            dao().update(selectedAlbum.copy(album))
        } else {
            dao().insert(SelectedAlbum(userInfoId, album))
        }
    }

    override suspend fun update(userInfoId: Long, albums: List<Album>) {
        albums.forEach { update(userInfoId, it) }
    }

    override suspend fun delete(userInfoId: Long, albumId: String) {
        val selectedAlbum = dao().findByUserInfoIdAndAlbumId(userInfoId, albumId)
        if (selectedAlbum != null) {
            dao().delete(selectedAlbum)
        }
    }

    override suspend fun findWithCoveredPhoto(albumId: String): SelectedAlbumWithCoveredPhoto? =
        dao().findWithCoveredPhoto(albumId)

    override suspend fun findWithDisplayedPhotos(albumId: String): SelectedAlbumWithDisplayedPhotos? =
        dao().findWithDisplayedPhotos(albumId)

    private fun dao(): SelectedAlbumDao = SingletonRoomObject.selectedAlbumDao()
}