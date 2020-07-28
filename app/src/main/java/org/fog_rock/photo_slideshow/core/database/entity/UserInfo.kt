package org.fog_rock.photo_slideshow.core.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import org.fog_rock.photo_slideshow.core.webapi.entity.TokenInfo

/**
 * ユーザー情報
 * データベース (Entity)
 * https://developer.android.com/training/data-storage/room?hl=ja
 */
@Entity(
    tableName = "users_info",
    indices = [Index(
        value = ["email_address"],
        unique = true
    )]
)
data class UserInfo(

    @PrimaryKey(autoGenerate = true)
    override val id: Long,

    @ColumnInfo(name = "create_date")
    override val createDateTimeMillis: Long,

    @ColumnInfo(name = "update_date")
    override val updateDateTimeMillis: Long,

    @ColumnInfo(name = "email_address")
    val emailAddress: String,

    @ColumnInfo(name = "access_token")
    val accessToken: String,

    @ColumnInfo(name = "refresh_token")
    val refreshToken: String,

    @ColumnInfo(name = "expired_access_token")
    val expiredAccessTokenTimeMillis: Long,

    @ColumnInfo(name = "update_photos")
    val updatePhotosTimeMillis: Long

): BaseEntity {

    constructor(
        emailAddress: String, tokenInfo: TokenInfo
    ): this(
        0,
        System.currentTimeMillis(),
        System.currentTimeMillis(),
        emailAddress,
        tokenInfo.accessToken,
        tokenInfo.refreshToken,
        tokenInfo.expiredAccessTokenTimeMillis,
        0
    )

    fun copy(tokenInfo: TokenInfo): UserInfo = this.copy(
        updateDateTimeMillis = System.currentTimeMillis(),
        accessToken = tokenInfo.accessToken,
        refreshToken = tokenInfo.refreshToken,
        expiredAccessTokenTimeMillis = tokenInfo.expiredAccessTokenTimeMillis
    )

    fun copy(updatePhotosTimeMillis: Long): UserInfo = this.copy(
        updateDateTimeMillis = System.currentTimeMillis(),
        updatePhotosTimeMillis = updatePhotosTimeMillis
    )

    /**
     * 写真更新が必要か.
     * @param intervalTimeMillis 写真更新をしなくてもよい間隔
     * @return 現在の時間が「最終更新時間 + 指定された間隔」より過ぎていれば true
     */
    fun isNeededUpdatePhotos(intervalTimeMillis: Long): Boolean =
        System.currentTimeMillis() > updatePhotosTimeMillis + intervalTimeMillis

    /**
     * アクセストークンが有効か.
     * @param intervalTimeMillis 有効期限までのバッファー間隔
     * @return 現在の時間が「アクセストークン有効期限 - 指定された間隔」を過ぎていなければ true
     */
    fun isAvailableAccessToken(intervalTimeMillis: Long): Boolean =
        System.currentTimeMillis() < expiredAccessTokenTimeMillis - intervalTimeMillis
}