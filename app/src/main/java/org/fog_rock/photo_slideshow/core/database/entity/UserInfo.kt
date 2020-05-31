package org.fog_rock.photo_slideshow.core.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import org.fog_rock.photo_slideshow.core.webapi.entity.TokenInfo

/**
 * ユーザー情報
 * データベース (Entity)
 * https://developer.android.com/training/data-storage/room?hl=ja
 */
@Entity(tableName = "users_info")
data class UserInfo(

    @PrimaryKey(autoGenerate = true)
    val id: Long,

    @ColumnInfo(name = "email_address")
    val emailAddress: String,

    @ColumnInfo(name = "access_token")
    val accessToken: String,

    @ColumnInfo(name = "refresh_token")
    val refreshToken: String,

    @ColumnInfo(name = "expired_access_token_time_millis")
    val expiredAccessTokenTimeMillis: Long,

    @ColumnInfo(name = "update_date_time_millis")
    val updateDateTimeMillis: Long

) {

    private constructor(id: Long, emailAddress: String, tokenInfo: TokenInfo): this (
        id, emailAddress,
        tokenInfo.accessToken, tokenInfo.refreshToken, tokenInfo.expiredAccessTokenTimeMillis,
        System.currentTimeMillis()
    )

    companion object {

        /**
         * ユーザー情報を新規作成する.
         */
        fun newUserInfo(emailAddress: String, tokenInfo: TokenInfo) =
            UserInfo(0, emailAddress, tokenInfo)

        /**
         * ユーザー情報をマージして更新する.
         */
        fun mergeUserInfo(userInfo: UserInfo, tokenInfo: TokenInfo) =
            UserInfo(userInfo.id, userInfo.emailAddress, tokenInfo)
    }
}