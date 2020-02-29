package org.fog_rock.photo_slideshow.core.entity

import java.io.Serializable

data class AlbumData(
    val id: String,
    val title: String,
    val productUrl: String,
    val mediaItemCount: Long
) : Serializable
