package org.fog_rock.photo_slideshow.app.menu.view

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import org.fog_rock.photo_slideshow.R
import org.fog_rock.photo_slideshow.app.menu.contract.MenuContract
import org.fog_rock.photo_slideshow.app.menu.interactor.MenuInteractor
import org.fog_rock.photo_slideshow.app.menu.presenter.MenuPresenter
import org.fog_rock.photo_slideshow.app.menu.router.MenuRouter
import org.fog_rock.photo_slideshow.app.module.lib.impl.AppDatabaseImpl
import org.fog_rock.photo_slideshow.app.module.lib.impl.GoogleWebApisImpl
import org.fog_rock.photo_slideshow.app.module.ui.AppDialogFragment
import org.fog_rock.photo_slideshow.app.module.ui.AppSimpleFragment
import org.fog_rock.photo_slideshow.core.extension.logI
import org.fog_rock.photo_slideshow.core.webapi.entity.ApiResult
import org.fog_rock.photo_slideshow.core.webapi.impl.GoogleOAuth2ApiImpl
import org.fog_rock.photo_slideshow.core.webapi.impl.GoogleSignInApiImpl
import org.fog_rock.photo_slideshow.core.webapi.impl.PhotosLibraryApiImpl
import org.fog_rock.photo_slideshow.databinding.ActivityMenuBinding

class MenuActivity : AppCompatActivity(),
    PreferenceFragmentCompat.OnPreferenceStartFragmentCallback,
    AppDialogFragment.Callback, MenuFragment.Callback, SettingsFragment.Callback,
    MenuContract.PresenterCallback {

    /**
     * ダイアログを表示するときのリクエスト.
     */
    private enum class DialogRequest (
        val code: Int,
        @StringRes val titleResId: Int,
        @StringRes val messageResId: Int,
        @StringRes val positiveResId: Int,
        @StringRes val negativeResId: Int,
        val cancelable: Boolean
    ) {
        /**
         * ユーザー切り替えの確認.
         */
        CONFIRM_CHANGE_USER(
            1000,
            R.string.confirm_change_user_title,
            R.string.confirm_change_user_message,
            R.string.change_user,
            R.string.cancel,
            false
        ),

        /**
         * サインアウトの確認.
         */
        CONFIRM_SIGN_OUT(
            1001,
            R.string.confirm_sign_out_title,
            R.string.confirm_sign_out_message,
            R.string.sign_out,
            R.string.cancel,
            false
        ),

        /**
         * ユーザー切り替えに失敗.
         */
        FAILED_CHANGE_USER(
            1010,
            R.string.failed_change_user_title,
            R.string.failed_change_user_message,
            R.string.ok,
            R.string.empty,
            false
        ),

        /**
         * サインアウトに失敗.
         */
        FAILED_SIGN_OUT(
            1100,
            R.string.failed_sign_out_title,
            R.string.failed_sign_out_message,
            R.string.ok,
            R.string.empty,
            false
        ),
        ;

        companion object {
            /**
             * コードナンバーからリクエストへコンバートする.
             */
            fun convertFromCode(code: Int): DialogRequest =
                values().find { it.code == code } ?: throw IllegalArgumentException("Invalid code.")
        }

        /**
         * リクエストに応じたダイアログを表示する.
         */
        fun show(context: Context, fragmentManager: FragmentManager) {
            AppDialogFragment.Builder(context).apply {
                if (titleResId != R.string.empty) setTitle(titleResId)
                if (messageResId != R.string.empty) setMessage(messageResId)
                if (positiveResId != R.string.empty) setPositiveLabel(positiveResId)
                if (negativeResId != R.string.empty) setNegativeLabel(negativeResId)
                setCancelable(cancelable)
            }.show(fragmentManager, code)
        }
    }

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

    override fun onDialogResult(requestCode: Int, resultCode: Int, data: Intent) {
        logI("onDialogResult() requestCode: $requestCode, resultCode: $resultCode")

        when (DialogRequest.convertFromCode(requestCode)) {
            DialogRequest.CONFIRM_CHANGE_USER -> {
                if (resultCode == AppDialogFragment.BUTTON_POSITIVE) {
                    logI("Try to request change user.")
                    presenter?.requestChangeUser()
                }
            }
            DialogRequest.CONFIRM_SIGN_OUT -> {
                if (resultCode == AppDialogFragment.BUTTON_POSITIVE) {
                    logI("Try to request sign out.")
                    presenter?.requestSignOut()
                }
            }
            else -> {
                // Do nothing.
            }
        }
    }

    override fun onCreateMenuFragment() {
        binding.toolbar.setTitle(R.string.menu)
    }

    override fun onClickedLicenseInfo() {
        presenter?.requestShowLicenses()
    }

    override fun onClickedChangeUser() {
        showDialogFragment(DialogRequest.CONFIRM_CHANGE_USER)
    }

    override fun onClickedSignOut() {
        showDialogFragment(DialogRequest.CONFIRM_SIGN_OUT)
    }

    override fun onCreateSettingsFragment() {
        binding.toolbar.setTitle(R.string.settings)
    }

    override fun getActivity(): Activity = this

    override fun createLoadResult(accountName: String, emailAddress: String) {
        replaceFragment(MenuFragment.newInstance(this, accountName, emailAddress), false)
    }

    override fun requestChangeUserResult(result: ApiResult) {
        if (result == ApiResult.SUCCEEDED) {
            finishWithSignedOut()
        } else {
            showDialogFragment(DialogRequest.FAILED_CHANGE_USER)
        }
    }

    override fun requestSignOutResult(result: ApiResult) {
        if (result == ApiResult.SUCCEEDED) {
            finishWithSignedOut()
        } else {
            showDialogFragment(DialogRequest.FAILED_SIGN_OUT)
        }
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

    /**
     * ダイアログを表示する.
     */
    private fun showDialogFragment(request: DialogRequest) {
        request.show(this, supportFragmentManager)
    }

    private fun finishWithSignedOut() {
        val intent = Intent().apply {
        }
        setResult(RESULT_OK, intent)
        finish()
    }
}