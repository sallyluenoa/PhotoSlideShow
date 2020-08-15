package org.fog_rock.photo_slideshow.core.database.entity

import androidx.room.Embedded
import androidx.room.Relation

data class UserInfoData(

    @Embedded
    var userInfo: UserInfo,

    @Relation(
        entity = SelectedAlbum::class,
        parentColumn = "id",
        entityColumn = "user_info_id"
    )
    var dataList: List<SelectedData>
)