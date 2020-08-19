package org.fog_rock.photo_slideshow.core.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.google.gson.Gson
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
    override val createTimeMillis: Long,

    @ColumnInfo(name = "update_date")
    override val updateTimeMillis: Long,

    @ColumnInfo(name = "email_address")
    val emailAddress: String,

    @ColumnInfo(name = "token_info")
    private val tokenInfo: String,

    @ColumnInfo(name = "update_photos_date")
    val updatePhotosTimeMillis: Long

): BaseEntity {

    constructor(
        emailAddress: String, tokenInfo: TokenInfo
    ): this(
        0,
        System.currentTimeMillis(),
        System.currentTimeMillis(),
        emailAddress,
        Gson().toJson(tokenInfo),
        0
    )

    fun tokenInfo(): TokenInfo = Gson().fromJson(tokenInfo, TokenInfo::class.javaObjectType)

    fun copy(tokenInfo: TokenInfo): UserInfo = this.copy(
        updateTimeMillis = System.currentTimeMillis(),
        tokenInfo = Gson().toJson(tokenInfo)
    )

    fun updatePhotos(): UserInfo = this.copy(
        updateTimeMillis = System.currentTimeMillis(),
        updatePhotosTimeMillis = System.currentTimeMillis()
    )

    /**
     * 写真更新が必要か.
     * @param intervalTimeMillis 写真更新をしなくてもよい間隔
     * @return 現在の時間が「最終更新時間 + 指定された間隔」より過ぎていれば true
     */
    fun isNeededUpdatePhotos(intervalTimeMillis: Long): Boolean =
        System.currentTimeMillis() > updatePhotosTimeMillis + intervalTimeMillis
}