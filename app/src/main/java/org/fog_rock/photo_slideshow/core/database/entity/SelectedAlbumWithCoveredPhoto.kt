package org.fog_rock.photo_slideshow.core.database.entity

import androidx.room.Embedded
import androidx.room.Relation

data class SelectedAlbumWithCoveredPhoto(

    @Embedded
    val selectedAlbum: SelectedAlbum,

    @Relation(
        parentColumn = "covered_media_item_id",
        entityColumn = "media_item_id"
    )
    val coveredDisplayedPhoto: DisplayedPhoto
)