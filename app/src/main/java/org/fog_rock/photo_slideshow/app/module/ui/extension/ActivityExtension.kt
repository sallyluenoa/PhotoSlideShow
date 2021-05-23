package org.fog_rock.photo_slideshow.app.module.ui.extension

import android.app.Activity
import android.content.Context
import android.content.Intent

inline fun <reified ActivityT: Activity> Context.newIntent(): Intent =
    Intent(this, ActivityT::class.java)

inline fun <reified ActivityT: Activity> Context.startActivity() {
    startActivity(newIntent<ActivityT>())
}

inline fun <reified ActivityT: Activity> Context.startActivityAndFinishAll() {
    startActivity(newIntent<ActivityT>().apply {
        flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
    })
}

inline fun <reified ActivityT: Activity> Activity.startActivityForResult(requestCode: Int) {
    startActivityForResult(newIntent<ActivityT>(), requestCode)
}