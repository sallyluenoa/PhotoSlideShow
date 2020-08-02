package org.fog_rock.photo_slideshow.core.database.entity

import androidx.room.*
import com.google.gson.Gson
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
    override val createTimeMillis: Long,

    @ColumnInfo(name = "update_date")
    override val updateTimeMillis: Long,

    @ColumnInfo(name = "user_info_id")
    val userInfoId: Long,

    @ColumnInfo(name = "album_id")
    val albumId: String,

    @ColumnInfo(name = "album")
    private val album: String

): BaseEntity {

    constructor(
        userInfoId: Long, album: Album
    ): this(
        0,
        System.currentTimeMillis(),
        System.currentTimeMillis(),
        userInfoId,
        album.id,
        Gson().toJson(album)
    )

    fun album(): Album = Gson().fromJson(album, Album::class.java)

    fun copy(album: Album): SelectedAlbum = this.copy(
        updateTimeMillis = System.currentTimeMillis(),
        albumId = album.id,
        album = Gson().toJson(album)
    )
}