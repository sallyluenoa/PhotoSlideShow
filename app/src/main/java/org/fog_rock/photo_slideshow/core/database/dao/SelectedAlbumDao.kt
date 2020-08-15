package org.fog_rock.photo_slideshow.core.database.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import org.fog_rock.photo_slideshow.core.database.entity.SelectedAlbum
import org.fog_rock.photo_slideshow.core.database.entity.SelectedData

@Dao
interface SelectedAlbumDao: BaseDao<SelectedAlbum> {

    @Query("select * from selected_album")
    override fun getAll(): List<SelectedAlbum>

    @Query("select * from selected_album where id = :id")
    override fun findById(id: Long): SelectedAlbum?

    @Query("select * from selected_album where user_info_id = :userInfoId and album_id = :albumId")
    fun findByUniqueKeys(userInfoId: Long, albumId: String): SelectedAlbum?

    @Transaction
    @Query("select * from selected_album where id = :id")
    fun findSelectedDataById(id: Long): SelectedData?

    @Transaction
    @Query("select * from selected_album where user_info_id = :userInfoId and album_id = :albumId")
    fun findSelectedDataByUniqueKeys(userInfoId: Long, albumId: String): SelectedData?
}