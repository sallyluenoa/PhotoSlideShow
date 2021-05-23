package org.fog_rock.photo_slideshow.core.extension

import android.content.Intent
import android.os.Bundle
import java.io.Serializable

inline fun <reified ChildT> Any?.downCast(): ChildT? = if (this is ChildT) this else null

fun <SerialT: Serializable> Intent.putArrayExtra(name: String, value: Array<SerialT>): Intent =
    this.apply { putExtra(name, value) }

inline fun <reified SerialT: Serializable> Intent.getArrayExtra(name: String): Array<SerialT>? =
    this.getSerializableExtra(name).downCast<Array<SerialT>>()

inline fun <reified SerialT: Serializable> Intent.putListExtra(name: String, value: List<SerialT>): Intent =
    putArrayExtra(name, value.toTypedArray())

inline fun <reified SerialT: Serializable> Intent.getListExtra(name: String): List<SerialT>? =
    getArrayExtra<SerialT>(name)?.toList()

fun <SerialT: Serializable> Bundle.putArrayExtra(name: String, value: Array<SerialT>): Bundle =
    this.apply { putSerializable(name, value) }

inline fun <reified SerialT: Serializable> Bundle.getArrayExtra(name: String): Array<SerialT>? =
    this.getSerializable(name).downCast<Array<SerialT>>()

inline fun <reified SerialT: Serializable> Bundle.putListExtra(name: String, value: List<SerialT>): Bundle =
    putArrayExtra(name, value.toTypedArray())

inline fun <reified SerialT: Serializable> Bundle.getListExtra(name: String): List<SerialT>? =
    getArrayExtra<SerialT>(name)?.toList()
