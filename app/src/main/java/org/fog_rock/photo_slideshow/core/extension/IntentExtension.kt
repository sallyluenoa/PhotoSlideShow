package org.fog_rock.photo_slideshow.core.extension

import android.content.Intent
import java.io.Serializable

fun <SerializableT: Serializable> Intent.putArrayListExtra(
    name: String, value: ArrayList<SerializableT>): Intent {
    this.putExtra(name, value)
    return this
}

@Suppress("UNCHECKED_CAST")
fun <SerializableT: Serializable> Intent.getArrayListExtra(
    name: String): ArrayList<SerializableT>? =
    this.getSerializableExtra(name) as? ArrayList<SerializableT>