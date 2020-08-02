package org.fog_rock.photo_slideshow.core.database.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import org.fog_rock.photo_slideshow.core.database.entity.UserInfo
import org.fog_rock.photo_slideshow.core.database.entity.UserInfoWithSelectedAlbums

/**
 * データベース (Dao)
 * https://developer.android.com/training/data-storage/room?hl=ja
 */
@Dao
interface UserInfoDao: BaseDao<UserInfo> {

    @Query("select * from users_info")
    override fun getAll(): List<UserInfo>

    @Query("select * from users_info where id = :id")
    override fun findById(id: Long): UserInfo?

    @Query("select * from users_info where email_address = :emailAddress")
    fun findByEmailAddress(emailAddress: String): UserInfo?

    @Transaction
    @Query("select * from users_info where id = :id")
    fun findWithSelectedAlbums(id: Long): UserInfoWithSelectedAlbums?

    @Transaction
    @Query("select * from users_info where email_address = :emailAddress")
    fun findWithSelectedAlbums(emailAddress: String): UserInfoWithSelectedAlbums?
}