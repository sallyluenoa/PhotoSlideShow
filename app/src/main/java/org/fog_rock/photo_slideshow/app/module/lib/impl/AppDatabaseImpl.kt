package org.fog_rock.photo_slideshow.app.module.lib.impl

import org.fog_rock.photo_slideshow.app.module.lib.AppDatabase
import org.fog_rock.photo_slideshow.core.database.dao.DisplayedPhotoDao
import org.fog_rock.photo_slideshow.core.database.dao.SelectedAlbumDao
import org.fog_rock.photo_slideshow.core.database.dao.UserInfoDao
import org.fog_rock.photo_slideshow.core.database.entity.SelectedAlbum
import org.fog_rock.photo_slideshow.core.database.entity.UserInfo
import org.fog_rock.photo_slideshow.core.database.entity.UserInfoData
import org.fog_rock.photo_slideshow.core.database.room.SingletonRoomObject
import org.fog_rock.photo_slideshow.core.webapi.entity.TokenInfo

class AppDatabaseImpl(): AppDatabase {

    override suspend fun updateUserInfo(emailAddress: String, tokenInfo: TokenInfo) {
        val userInfo = userInfoDao().findByEmailAddress(emailAddress)
        if (userInfo != null) {
            userInfoDao().update(userInfo.copy(tokenInfo))
        } else {
            userInfoDao().insert(UserInfo(emailAddress, tokenInfo))
        }
    }

    override suspend fun deleteUserInfo(userInfo: UserInfo) {
        userInfoDao().delete(userInfo)
    }

    override suspend fun replaceUserInfoData(userInfoData: UserInfoData, photosInfo: List<AppDatabase.PhotoInfo>) {
        userInfoData.dataList.forEach { selectedData ->
            val photoInfo = photosInfo.find { it.album.id == selectedData.selectedAlbum.albumId }
            if (photoInfo != null) {
                // DB更新
                // selectedAlbumを更新.
                selectedAlbumDao().update(selectedData.selectedAlbum.copy(photoInfo.album))
                // 古いdisplayedPhotosを削除.
                displayedPhotoDao().delete(selectedData.displayedPhotos)
                // 新しいdisplayedPhotosを追加.
                displayedPhotoDao().insert(photoInfo.displayedPhotos(selectedData.selectedAlbum.id))
            } else {
                // DB削除
                // selectedAlbumを消せばdisplayedPhotosも削除される.
                selectedAlbumDao().delete(selectedData.selectedAlbum)
            }
        }

        photosInfo.forEach { photoInfo->
            if (userInfoData.dataList.find { it.selectedAlbum.albumId == photoInfo.album.id } == null) {
                // DB追加
                // selectedAlbumを追加.
                val id = selectedAlbumDao().insert(SelectedAlbum(userInfoData.userInfo.id, photoInfo.album))
                // displayedPhotosを追加.
                displayedPhotoDao().insert(photoInfo.displayedPhotos(id))
            }
        }

        // 最後にUserInfoの最終更新時間を更新する.
        userInfoDao().update(userInfoData.userInfo.updatePhotos())
    }

    override suspend fun findUserInfoByEmailAddress(emailAddress: String): UserInfo? =
        userInfoDao().findByEmailAddress(emailAddress)

    override suspend fun findUserInfoDataById(id: Long): UserInfoData? =
        userInfoDao().findUserInfoDataById(id)

    override suspend fun findUserInfoDataByEmailAddress(emailAddress: String): UserInfoData? =
        userInfoDao().findUserInfoDataByEmailAddress(emailAddress)

    private fun userInfoDao(): UserInfoDao = SingletonRoomObject.userInfoDao()

    private fun selectedAlbumDao(): SelectedAlbumDao = SingletonRoomObject.selectedAlbumDao()

    private fun displayedPhotoDao(): DisplayedPhotoDao = SingletonRoomObject.displayedPhotoDao()
}