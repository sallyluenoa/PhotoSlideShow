package org.fog_rock.photo_slideshow.app.module.ui.extension

import androidx.annotation.IdRes
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import org.fog_rock.photo_slideshow.R

fun FragmentActivity.addFragment(fragment: Fragment, @IdRes resId: Int = R.id.fragment_container) {
    replaceFragment(fragment, resId, true)
}

fun FragmentActivity.replaceFragment(fragment: Fragment, @IdRes resId: Int = R.id.fragment_container) {
    replaceFragment(fragment, resId, false)
}

private fun FragmentActivity.replaceFragment(fragment: Fragment, @IdRes resId: Int, addStack: Boolean) {
    supportFragmentManager.beginTransaction().apply {
        replace(resId, fragment)
        if (addStack) addToBackStack(null)
    }.commit()
}