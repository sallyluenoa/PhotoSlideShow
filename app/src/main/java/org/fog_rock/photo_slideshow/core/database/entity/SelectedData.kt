package org.fog_rock.photo_slideshow.core.database.entity

import androidx.room.Embedded
import androidx.room.Relation

data class SelectedData(

    @Embedded
    val selectedAlbum: SelectedAlbum,

    @Relation(
        parentColumn = "id",
        entityColumn = "selected_album_id"
    )
    val displayedPhotos: List<DisplayedPhoto>
)