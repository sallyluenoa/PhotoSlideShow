package org.fog_rock.photo_slideshow.core.database.impl

import androidx.room.*
import org.fog_rock.photo_slideshow.core.database.entity.UserInfo

/**
 * データベース (Dao)
 * https://developer.android.com/training/data-storage/room?hl=ja
 */
@Dao
interface UserInfoDao {

    @Insert
    fun insert(userInfo: UserInfo)

    @Update
    fun update(userInfo: UserInfo)

    @Delete
    fun delete(userInfo: UserInfo)

    @Query("select * from users_info")
    fun getAll(): List<UserInfo>

    @Query("select * from users_info where email_address = :email")
    fun find(email: String): List<UserInfo>
}