package org.fog_rock.photo_slideshow.core.database.dao

import androidx.room.*
import org.fog_rock.photo_slideshow.core.database.entity.BaseEntity

@Dao
interface BaseDao<EntityT: BaseEntity> {

    @Insert
    suspend fun insert(entity: EntityT): Long

    @Insert
    suspend fun insert(entities: List<EntityT>): List<Long>

    @Update
    suspend fun update(entity: EntityT)

    @Update
    suspend fun update(entities: List<EntityT>)

    @Delete
    suspend fun delete(entity: EntityT)

    @Delete
    suspend fun delete(entities: List<EntityT>)

    suspend fun getAll(): List<EntityT>

    suspend fun findById(id: Long): EntityT?
}