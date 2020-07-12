package org.fog_rock.photo_slideshow.core.database.entity

import androidx.room.*
import com.google.photos.types.proto.Album

@Entity(
    tableName = "selected_album",
    indices = [Index(
        value = ["user_info_id", "album_id"],
        unique = true
    )],
    foreignKeys = [ForeignKey(
        entity = UserInfo::class,
        parentColumns = ["id"],
        childColumns = ["user_info_id"],
        onDelete = ForeignKey.CASCADE
    )]
)
data class SelectedAlbum(

    @PrimaryKey(autoGenerate = true)
    override val id: Long,

    @ColumnInfo(name = "create_date")
    override val createDateTimeMillis: Long,

    @ColumnInfo(name = "update_date")
    override val updateDateTimeMillis: Long,

    @ColumnInfo(name = "user_info_id")
    val userInfoId: Long,

    @ColumnInfo(name = "album_id")
    val albumId: String,

    @ColumnInfo(name = "album_title")
    val albumTitle: String,

    @ColumnInfo(name = "covered_media_item_id")
    val coveredMediaItemId: String

): BaseEntity {

    constructor(
        userInfoId: Long, album: Album
    ): this(
        0,
        System.currentTimeMillis(),
        System.currentTimeMillis(),
        userInfoId,
        album.id,
        album.title,
        album.coverPhotoMediaItemId
    )

    fun copy(album: Album): SelectedAlbum = this.copy(
        updateDateTimeMillis = System.currentTimeMillis(),
        albumId = album.id,
        albumTitle = album.title,
        coveredMediaItemId = album.coverPhotoMediaItemId
    )
}