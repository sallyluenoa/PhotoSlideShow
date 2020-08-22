package org.fog_rock.photo_slideshow.app.select.entity

import java.util.*

enum class SelectAlbumsResult {

    DECIDED_ALBUMS;

    fun key(): String = this.toString().toLowerCase(Locale.US)
}