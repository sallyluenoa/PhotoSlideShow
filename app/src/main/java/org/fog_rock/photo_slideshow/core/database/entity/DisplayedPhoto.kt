package org.fog_rock.photo_slideshow.core.database.entity

import androidx.room.*
import com.google.gson.Gson
import com.google.photos.types.proto.MediaItem

@Entity(
    tableName = "displayed_photo",
    indices = [Index(
        value = ["selected_album_id", "media_item_id"],
        unique = true
    )],
    foreignKeys = [ForeignKey(
        entity = SelectedAlbum::class,
        parentColumns = ["id"],
        childColumns = ["selected_album_id"],
        onDelete = ForeignKey.CASCADE
    )]
)
data class DisplayedPhoto(

    @PrimaryKey(autoGenerate = true)
    override val id: Long,

    @ColumnInfo(name = "create_date")
    override val createTimeMillis: Long,

    @ColumnInfo(name = "update_date")
    override val updateTimeMillis: Long,

    @ColumnInfo(name = "selected_album_id")
    val selectedAlbumId: Long,

    @ColumnInfo(name = "media_item_id")
    val mediaItemId: String,

    @ColumnInfo(name = "media_item")
    val mediaItem: String,

    @ColumnInfo(name = "output_path")
    val outputPath: String,

    @ColumnInfo(name="is_my_favorite")
    val isMyFavorite: Boolean

): BaseEntity {

    constructor(
        selectedAlbumId: Long, mediaItem: MediaItem, outputPath: String
    ): this(
        0,
        System.currentTimeMillis(),
        System.currentTimeMillis(),
        selectedAlbumId,
        mediaItem.id,
        Gson().toJson(mediaItem),
        outputPath,
        false
    )

    fun mediaItem(): MediaItem = Gson().fromJson(mediaItem, MediaItem::class.java)

    fun copy(mediaItem: MediaItem, outputPath: String): DisplayedPhoto = this.copy(
        updateTimeMillis = System.currentTimeMillis(),
        mediaItemId = mediaItem.id,
        mediaItem = Gson().toJson(mediaItem),
        outputPath = outputPath
    )
}