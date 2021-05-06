package org.fog_rock.photo_slideshow.app.menu.view

import android.app.Activity
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import org.fog_rock.photo_slideshow.R
import org.fog_rock.photo_slideshow.app.menu.contract.MenuContract
import org.fog_rock.photo_slideshow.app.menu.interactor.MenuInteractor
import org.fog_rock.photo_slideshow.app.menu.presenter.MenuPresenter
import org.fog_rock.photo_slideshow.app.menu.router.MenuRouter
import org.fog_rock.photo_slideshow.app.module.lib.impl.AppDatabaseImpl
import org.fog_rock.photo_slideshow.app.module.lib.impl.GoogleWebApisImpl
import org.fog_rock.photo_slideshow.app.module.ui.AppSimpleFragment
import org.fog_rock.photo_slideshow.core.webapi.entity.ApiResult
import org.fog_rock.photo_slideshow.core.webapi.impl.GoogleOAuth2ApiImpl
import org.fog_rock.photo_slideshow.core.webapi.impl.GoogleSignInApiImpl
import org.fog_rock.photo_slideshow.core.webapi.impl.PhotosLibraryApiImpl
import org.fog_rock.photo_slideshow.databinding.ActivityMenuBinding

class MenuActivity : AppCompatActivity(),
    PreferenceFragmentCompat.OnPreferenceStartFragmentCallback,
    MenuFragment.Callback, SettingsFragment.Callback,
    MenuContract.PresenterCallback {

    private lateinit var binding: ActivityMenuBinding

    private var presenter: MenuContract.Presenter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMenuBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)

        replaceFragment(
            AppSimpleFragment.newInstance(
                AppSimpleFragment.Layout.PROGRESS), false)

        presenter = MenuPresenter(
            MenuInteractor(
                AppDatabaseImpl(),
                GoogleWebApisImpl(this, GoogleSignInApiImpl(), GoogleOAuth2ApiImpl(), PhotosLibraryApiImpl())
            ),
            MenuRouter()
        )
        presenter?.create(this)
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
        presenter?.requestShowLicenses()
    }

    override fun onClickedChangeUser() {
        presenter?.requestChangeUser()
    }

    override fun onClickedSignOut() {
        presenter?.requestSignOut()
    }

    override fun onCreateSettingsFragment() {
        binding.toolbar.setTitle(R.string.settings)
    }

    override fun getActivity(): Activity = this

    override fun createLoadResult(accountName: String, emailAddress: String) {
        replaceFragment(MenuFragment.newInstance(this, accountName, emailAddress), false)
    }

    override fun requestChangeUserResult(result: ApiResult) {
//        TODO("Not yet implemented")
    }

    override fun requestSignOutResult(result: ApiResult) {
//        TODO("Not yet implemented")
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