package org.fog_rock.photo_slideshow.core.database.entity

import androidx.room.Embedded
import androidx.room.Relation

data class UserInfoWithSelectedAlbums(

    @Embedded
    val userInfo: UserInfo,

    @Relation(
        parentColumn = "id",
        entityColumn = "user_info_id"
    )
    val selectedAlbums: List<SelectedAlbum>
)