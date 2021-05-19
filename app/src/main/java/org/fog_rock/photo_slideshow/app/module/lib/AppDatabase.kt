package org.fog_rock.photo_slideshow.app.module.lib

import com.google.photos.types.proto.Album
import com.google.photos.types.proto.MediaItem
import org.fog_rock.photo_slideshow.core.database.entity.DisplayedPhoto
import org.fog_rock.photo_slideshow.core.database.entity.UserInfo
import org.fog_rock.photo_slideshow.core.database.entity.UserInfoData
import org.fog_rock.photo_slideshow.core.webapi.entity.TokenInfo

/**
 * アプリのデータベースをハンドリングするためのインターフェース.
 */
interface AppDatabase {

    /**
     * フォト情報
     */
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

    /**
     * DBのユーザー情報を更新する.
     */
    suspend fun updateUserInfo(emailAddress: String, tokenInfo: TokenInfo)

    /**
     * DBからメールアドレスに紐づくユーザー情報を全削除する.
     */
    suspend fun deleteUserInfo(emailAddress: String)

    /**
     * DBのユーザー情報全般を画像情報リストで置き換える.
     */
    suspend fun replaceUserInfoData(userInfoData: UserInfoData, photosInfo: List<PhotoInfo>)

    /**
     * DBからユーザー情報をメールアドレスで検索する.
     */
    suspend fun findUserInfoByEmailAddress(emailAddress: String): UserInfo?

    /**
     * DBからユーザー情報全般をIDで検索する.
     */
    suspend fun findUserInfoDataById(id: Long): UserInfoData?

    /**
     * DBからユーザー情報全般をメールアドレスで検索する.
     */
    suspend fun findUserInfoDataByEmailAddress(emailAddress: String): UserInfoData?
}