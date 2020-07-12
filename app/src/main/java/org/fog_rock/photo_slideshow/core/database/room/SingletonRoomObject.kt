package org.fog_rock.photo_slideshow.core.database.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import org.fog_rock.photo_slideshow.core.database.dao.DisplayedPhotoDao
import org.fog_rock.photo_slideshow.core.database.dao.SelectedAlbumDao
import org.fog_rock.photo_slideshow.core.database.dao.UserInfoDao
import org.fog_rock.photo_slideshow.core.database.entity.DisplayedPhoto
import org.fog_rock.photo_slideshow.core.database.entity.SelectedAlbum
import org.fog_rock.photo_slideshow.core.database.entity.UserInfo

object SingletonRoomObject {

    /**
     * データベース (Room)
     * https://developer.android.com/training/data-storage/room?hl=ja
     */
    @Database(
        entities = [
            UserInfo::class,
            SelectedAlbum::class,
            DisplayedPhoto::class
        ],
        version = 1,
        exportSchema = false
    )
    abstract class SingletonRoomDatabase: RoomDatabase() {
        abstract fun userInfoDao(): UserInfoDao
        abstract fun selectedAlbumDao(): SelectedAlbumDao
        abstract fun displayedPhotoDao(): DisplayedPhotoDao
    }

    private lateinit var database: SingletonRoomDatabase

    fun setup(context: Context) {
        database = Room.databaseBuilder(
            context,
            SingletonRoomDatabase::class.java,
            "database"
        ).build()
    }

    fun userInfoDao() = database.userInfoDao()

    fun selectedAlbumDao() = database.selectedAlbumDao()

    fun displayedPhotoDao() = database.displayedPhotoDao()
}