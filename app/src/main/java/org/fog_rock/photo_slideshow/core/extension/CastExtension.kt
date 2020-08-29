package org.fog_rock.photo_slideshow.core.extension

import android.content.Intent
import android.os.Bundle
import java.io.Serializable

@Suppress("UNCHECKED_CAST")
fun <ChildT> Any?.downCast(): ChildT? = this as ChildT

fun <SerializableT: Serializable> Intent.putArrayExtra(name: String, value: Array<SerializableT>): Intent {
    this.putExtra(name, value)
    return this
}

fun <SerializableT: Serializable> Intent.getArrayExtra(name: String): Array<SerializableT>? =
    this.getSerializableExtra(name).downCast<Array<SerializableT>>()

fun <SerializableT: Serializable> Intent.putArrayListExtra(name: String, value: ArrayList<SerializableT>): Intent {
    this.putExtra(name, value)
    return this
}

fun <SerializableT: Serializable> Intent.getArrayListExtra(name: String): ArrayList<SerializableT>? =
    this.getSerializableExtra(name).downCast<ArrayList<SerializableT>>()

fun <SerializableT: Serializable> Bundle.putArrayExtra(name: String, value: Array<SerializableT>): Bundle {
    this.putSerializable(name, value)
    return this
}

fun <SerializableT: Serializable> Bundle.getArrayExtra(name: String): Array<SerializableT>? =
    this.getSerializable(name).downCast<Array<SerializableT>>()

fun <SerializableT: Serializable> Bundle.putArrayListExtra(name: String, value: ArrayList<SerializableT>): Bundle {
    this.putSerializable(name, value)
    return this
}

fun <SerializableT: Serializable> Bundle.getArrayListExtra(name: String): ArrayList<SerializableT>? =
    this.getSerializable(name).downCast<ArrayList<SerializableT>>()
