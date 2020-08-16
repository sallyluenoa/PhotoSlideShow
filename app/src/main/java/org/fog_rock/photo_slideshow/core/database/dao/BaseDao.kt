package org.fog_rock.photo_slideshow.core.database.dao

import androidx.room.*
import org.fog_rock.photo_slideshow.core.database.entity.BaseEntity

@Dao
interface BaseDao<EntityT: BaseEntity> {

    @Insert
    fun insert(entity: EntityT): Long

    @Insert
    fun insert(entities: List<EntityT>): List<Long>

    @Update
    fun update(entity: EntityT)

    @Update
    fun update(entities: List<EntityT>)

    @Delete
    fun delete(entity: EntityT)

    @Delete
    fun delete(entities: List<EntityT>)

    fun getAll(): List<EntityT>

    fun findById(id: Long): EntityT?
}