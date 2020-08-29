package org.fog_rock.photo_slideshow.app.module.lib

import com.google.photos.types.proto.Album
import com.google.photos.types.proto.MediaItem
import org.fog_rock.photo_slideshow.core.database.entity.DisplayedPhoto
import org.fog_rock.photo_slideshow.core.database.entity.UserInfo
import org.fog_rock.photo_slideshow.core.database.entity.UserInfoData
import org.fog_rock.photo_slideshow.core.webapi.entity.TokenInfo

interface AppDatabase {

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

    suspend fun updateUserInfo(emailAddress: String, tokenInfo: TokenInfo)

    suspend fun deleteUserInfo(userInfo: UserInfo)

    suspend fun replaceUserInfoData(userInfoData: UserInfoData, photosInfo: List<PhotoInfo>)

    suspend fun findUserInfoByEmailAddress(emailAddress: String): UserInfo?

    suspend fun findUserInfoDataById(id: Long): UserInfoData?

    suspend fun findUserInfoDataByEmailAddress(emailAddress: String): UserInfoData?
}