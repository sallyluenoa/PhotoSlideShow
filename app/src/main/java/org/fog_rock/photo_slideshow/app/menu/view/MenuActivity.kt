package org.fog_rock.photo_slideshow.app.menu.view

import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import org.fog_rock.photo_slideshow.R
import org.fog_rock.photo_slideshow.core.extension.logI
import org.fog_rock.photo_slideshow.databinding.ActivityMenuBinding

class MenuActivity : AppCompatActivity(),
    MenuFragment.Callback, SettingsFragment.Callback,
    PreferenceFragmentCompat.OnPreferenceStartFragmentCallback {

    private lateinit var binding: ActivityMenuBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMenuBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)

        replaceFragment(MenuFragment.newInstance(
            this,"Example Account", "example@gmail.com"), false)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean =
        when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }

    override fun onPreferenceStartFragment(caller: PreferenceFragmentCompat, pref: Preference): Boolean {
        val fragment = supportFragmentManager.fragmentFactory.instantiate(classLoader, pref.fragment)
        fragment.setTargetFragment(caller, 0)
        fragment.arguments = caller.arguments
        replaceFragment(fragment, true)
        return true
    }

    override fun onCreateMenuFragment() {
        binding.toolbar.setTitle(R.string.menu)
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

    override fun onCreateSettingsFragment() {
        binding.toolbar.setTitle(R.string.settings)
    }

    /**
     * 新しいフラグメントに置換する.
     */
    private fun replaceFragment(fragment: Fragment, addStack: Boolean) {
        supportFragmentManager.beginTransaction().apply {
            replace(R.id.fragment_container, fragment)
            if (addStack) addToBackStack(null)
        }.commit()
    }
}