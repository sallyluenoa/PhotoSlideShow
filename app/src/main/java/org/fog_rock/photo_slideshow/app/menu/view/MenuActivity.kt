package org.fog_rock.photo_slideshow.app.menu.view

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import org.fog_rock.photo_slideshow.R
import org.fog_rock.photo_slideshow.app.module.ui.AppSimpleFragment

class MenuActivity : AppCompatActivity(), MenuFragment.Callback {

    private val fragmentManager = supportFragmentManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_menu)
        replaceFragment(AppSimpleFragment.newInstance(AppSimpleFragment.Layout.PROGRESS))
    }

    override fun onClickedSettings() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onClickedLicenseInfo() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onClickedChangeUser() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onClickedSignOut() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    /**
     * 新しいフラグメントに置換する.
     */
    private fun replaceFragment(fragment: Fragment) {
        fragmentManager.beginTransaction().apply {
            replace(R.id.fragment_container, fragment)
        }.commit()
    }
}