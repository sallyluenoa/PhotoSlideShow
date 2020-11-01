package org.fog_rock.photo_slideshow.app.menu.view

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import org.fog_rock.photo_slideshow.R
import org.fog_rock.photo_slideshow.app.module.ui.AppSimpleFragment
import org.fog_rock.photo_slideshow.core.extension.logI

class MenuActivity : AppCompatActivity(), MenuFragment.Callback, PreferenceFragmentCompat.OnPreferenceStartFragmentCallback {

    private val fragmentManager = supportFragmentManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_menu)
        replaceFragment(AppSimpleFragment.newInstance(AppSimpleFragment.Layout.PROGRESS))
    }

    override fun onPreferenceStartFragment(caller: PreferenceFragmentCompat, pref: Preference): Boolean {
        val fragment = supportFragmentManager.fragmentFactory.instantiate(classLoader, pref.fragment)
        fragment.setTargetFragment(caller, 0)
        fragment.arguments = caller.arguments
        replaceFragment(fragment)
        return true
    }

    override fun onClickedLicenseInfo() {
//        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        logI("onClickedLicenseInfo")
    }

    override fun onClickedChangeUser() {
//        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        logI("onClickedChangeUser")
    }

    override fun onClickedSignOut() {
 //       TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        logI("onClickedSignOut")
    }

    /**
     * 新しいフラグメントに置換する.
     */
    private fun replaceFragment(fragment: Fragment) {
        fragmentManager.beginTransaction().apply {
            replace(R.id.fragment_container, fragment)
            addToBackStack(null)
        }.commit()
    }
}