package org.fog_rock.photo_slideshow.core.database.dao

import androidx.room.Dao
import androidx.room.Query
import org.fog_rock.photo_slideshow.core.database.entity.DisplayedPhoto

@Dao
interface DisplayedPhotoDao: BaseDao<DisplayedPhoto> {

    @Query("select * from displayed_photo")
    override suspend fun getAll(): List<DisplayedPhoto>

    @Query("select * from displayed_photo where id = :id")
    override suspend fun findById(id: Long): DisplayedPhoto?

    @Query("select * from displayed_photo where selected_album_id = :selectedAlbumId and media_item_id = :mediaItemId")
    suspend fun findByUniqueKeys(selectedAlbumId: Long, mediaItemId: String): DisplayedPhoto?
}