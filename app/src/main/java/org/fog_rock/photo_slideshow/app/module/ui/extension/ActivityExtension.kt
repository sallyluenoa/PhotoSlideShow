package org.fog_rock.photo_slideshow.app.module.ui.extension

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import org.fog_rock.photo_slideshow.R

fun FragmentActivity.addFragment(fragment: Fragment) {
    replaceFragment(fragment, true)
}

fun FragmentActivity.replaceFragment(fragment: Fragment) {
    replaceFragment(fragment, false)
}

private fun FragmentActivity.replaceFragment(fragment: Fragment, addStack: Boolean) {
    supportFragmentManager.beginTransaction().apply {
        replace(R.id.fragment_container, fragment)
        if (addStack) addToBackStack(null)
    }.commit()
}