package org.fog_rock.photo_slideshow.core.extension

import java.text.SimpleDateFormat
import java.util.*

const val ONE_SECOND_MILLIS = 1000L
const val ONE_MINUTE_MILLIS = 60 * ONE_SECOND_MILLIS
const val ONE_HOUR_MILLIS = 60 * ONE_MINUTE_MILLIS
const val ONE_DAY_MILLIS = 24 * ONE_HOUR_MILLIS

private const val ISO_8601_FORMAT = "yyyy-MM-dd'T'HH:mm'Z'"

fun Long.toDateString(
    format: String = ISO_8601_FORMAT,
    timeZone: TimeZone = TimeZone.getDefault()
): String {
    val df = SimpleDateFormat(format, Locale.US)
    df.timeZone = timeZone
    return df.format(Date(this))
}