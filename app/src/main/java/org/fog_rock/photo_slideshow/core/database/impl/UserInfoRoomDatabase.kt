package org.fog_rock.photo_slideshow.core.database.impl

import androidx.room.Database
import androidx.room.RoomDatabase
import org.fog_rock.photo_slideshow.core.database.entity.UserInfo

/**
 * データベース (Room)
 * https://developer.android.com/training/data-storage/room?hl=ja
 */
@Database(entities = [UserInfo::class], version = 1)
abstract class UserInfoRoomDatabase: RoomDatabase() {
    abstract fun dao(): UserInfoDao
}