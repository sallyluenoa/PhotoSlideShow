package org.fog_rock.photo_slideshow.core.database.entity

import androidx.room.*
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
    override val createDateTimeMillis: Long,

    @ColumnInfo(name = "update_date")
    override val updateDateTimeMillis: Long,

    @ColumnInfo(name = "selected_album_id")
    val selectedAlbumId: Long,

    @ColumnInfo(name = "media_item_id")
    val mediaItemId: String,

    @ColumnInfo(name = "file_name")
    val fileName: String,

    @ColumnInfo(name="creation_time")
    val creationTime: Long,

    @ColumnInfo(name="contributor_name")
    val contributorName: String,

    @ColumnInfo(name="is_my_favorite")
    val isMyFavorite: Boolean

): BaseEntity {

    constructor(
        selectedAlbumId: Long, mediaItem: MediaItem
    ): this(
        0,
        System.currentTimeMillis(),
        System.currentTimeMillis(),
        selectedAlbumId,
        mediaItem.id,
        mediaItem.filename,
        if (mediaItem.hasMediaMetadata()) mediaItem.mediaMetadata.creationTime.seconds * 1000L else 0L,
        if (mediaItem.hasContributorInfo()) mediaItem.contributorInfo.displayName else "",
        false
    )

    fun copy(mediaItem: MediaItem): DisplayedPhoto = this.copy(
        updateDateTimeMillis = System.currentTimeMillis(),
        mediaItemId = mediaItem.id,
        fileName = mediaItem.filename,
        creationTime = if (mediaItem.hasMediaMetadata()) mediaItem.mediaMetadata.creationTime.seconds * 1000L else 0L,
        contributorName = if (mediaItem.hasContributorInfo()) mediaItem.contributorInfo.displayName else ""
    )
}