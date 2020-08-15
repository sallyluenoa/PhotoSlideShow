package org.fog_rock.photo_slideshow.app.module

import com.google.photos.types.proto.Album
import com.google.photos.types.proto.MediaItem
import org.fog_rock.photo_slideshow.core.database.entity.*
import org.fog_rock.photo_slideshow.core.database.room.SingletonRoomObject
import org.fog_rock.photo_slideshow.core.webapi.entity.TokenInfo

class AppDatabase {

    fun updateUserInfo(emailAddress: String, tokenInfo: TokenInfo) {
        val userInfo = userInfoDao().findByEmailAddress(emailAddress)
        if (userInfo != null) {
            userInfoDao().update(userInfo.copy(tokenInfo))
        } else {
            userInfoDao().insert(UserInfo(emailAddress, tokenInfo))
        }
    }

    fun updateSelectedAlbum(userInfoId: Long, album: Album) {
        val selectedAlbum = selectedAlbumDao().findByUniqueKeys(userInfoId, album.id)
        if (selectedAlbum != null) {
            selectedAlbumDao().update(selectedAlbum.copy(album))
        } else {
            selectedAlbumDao().insert(SelectedAlbum(userInfoId, album))
        }
    }

    fun updateDisplayedPhoto(selectedAlbumId: Long, mediaItem: MediaItem) {
        val displayedPhoto = displayedPhotoDao().findByUniqueKeys(selectedAlbumId, mediaItem.id)
        if (displayedPhoto != null) {
            displayedPhotoDao().update(displayedPhoto.copy(mediaItem))
        } else {
            displayedPhotoDao().insert(DisplayedPhoto(selectedAlbumId, mediaItem))
        }
    }

    fun updateSelectedAlbums(userInfoId: Long, albums: List<Album>) {
        albums.forEach { updateSelectedAlbum(userInfoId, it) }
    }

    fun updateDisplayedPhotos(selectedAlbumId: Long, mediaItems: List<MediaItem>) {
        mediaItems.forEach { updateDisplayedPhoto(selectedAlbumId, it) }
    }

    fun findUserInfoByEmailAddress(emailAddress: String): UserInfo? =
        userInfoDao().findByEmailAddress(emailAddress)

    fun findUserInfoWithAllById(id: Long): UserInfoData? =
        userInfoDao().findWithAllById(id)

    fun findUserInfoWithAllByEmailAddress(emailAddress: String): UserInfoData? =
        userInfoDao().findWithAllByEmailAddress(emailAddress)

    private fun userInfoDao() = SingletonRoomObject.userInfoDao()

    private fun selectedAlbumDao() = SingletonRoomObject.selectedAlbumDao()

    private fun displayedPhotoDao() = SingletonRoomObject.displayedPhotoDao()
}