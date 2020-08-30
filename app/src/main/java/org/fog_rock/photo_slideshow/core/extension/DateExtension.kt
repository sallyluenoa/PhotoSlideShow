package org.fog_rock.photo_slideshow.core.extension

import java.text.SimpleDateFormat
import java.util.*

private const val ISO_8601_FORMAT = "yyyy-MM-dd'T'HH:mm'Z'"

fun Long.toDateString(
    format: String = ISO_8601_FORMAT,
    timeZone: TimeZone = TimeZone.getDefault()
): String {
    val df = SimpleDateFormat(format, Locale.US)
    df.timeZone = timeZone
    return df.format(Date(this))
}