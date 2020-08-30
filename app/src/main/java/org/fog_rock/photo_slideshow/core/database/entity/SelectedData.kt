package org.fog_rock.photo_slideshow.core.database.entity

import androidx.room.Embedded
import androidx.room.Relation

/**
 * 選択アルバムと表示フォトリストの組み合わせ
 * データベース (Entity)
 * https://developer.android.com/training/data-storage/room/relationships?hl=ja
 */
data class SelectedData(

    @Embedded
    val selectedAlbum: SelectedAlbum,

    @Relation(
        parentColumn = "id",
        entityColumn = "selected_album_id"
    )
    val displayedPhotos: List<DisplayedPhoto>
)