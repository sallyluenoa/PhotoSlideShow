package org.fog_rock.photo_slideshow.app.menu.view

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.KeyEvent
import android.view.MenuItem
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentManager
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import org.fog_rock.photo_slideshow.R
import org.fog_rock.photo_slideshow.app.menu.contract.MenuContract
import org.fog_rock.photo_slideshow.app.menu.interactor.MenuInteractor
import org.fog_rock.photo_slideshow.app.menu.presenter.MenuPresenter
import org.fog_rock.photo_slideshow.app.menu.router.MenuRouter
import org.fog_rock.photo_slideshow.app.module.lib.impl.AppDatabaseImpl
import org.fog_rock.photo_slideshow.app.module.lib.impl.AppSettingsImpl
import org.fog_rock.photo_slideshow.app.module.lib.impl.GoogleWebApisImpl
import org.fog_rock.photo_slideshow.app.module.ui.AppDialogFragment
import org.fog_rock.photo_slideshow.app.module.ui.AppSimpleFragment
import org.fog_rock.photo_slideshow.app.module.ui.addFragment
import org.fog_rock.photo_slideshow.app.module.ui.replaceFragment
import org.fog_rock.photo_slideshow.core.extension.logI
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
        @StringRes private val titleResId: Int?,
        @StringRes private val messageResId: Int?,
        @StringRes private val positiveResId: Int?,
        @StringRes private val negativeResId: Int?,
        private val cancelable: Boolean
    ) {
        /**
         * 設定変更が適用されることの通知.
         */
        ALERT_CHANGED_SETTINGS(
            1000,
            R.string.alert_changed_settings_title,
            R.string.alert_changed_settings_message,
            R.string.ok,
            null,
            false
        ),

        /**
         * ユーザー切り替えの確認.
         */
        CONFIRM_CHANGE_USER(
            1010,
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
            1011,
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
            1100,
            R.string.failed_change_user_title,
            R.string.failed_change_user_message,
            R.string.ok,
            null,
            false
        ),

        /**
         * サインアウトに失敗.
         */
        FAILED_SIGN_OUT(
            1101,
            R.string.failed_sign_out_title,
            R.string.failed_sign_out_message,
            R.string.ok,
            null,
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
                if (titleResId != null) setTitle(titleResId)
                if (messageResId != null) setMessage(messageResId)
                if (positiveResId != null) setPositiveLabel(positiveResId)
                if (negativeResId != null) setNegativeLabel(negativeResId)
                setCancelable(cancelable)
            }.show(fragmentManager, code)
        }
    }

    private lateinit var binding: ActivityMenuBinding

    private var presenter: MenuContract.Presenter? = null

    private var orgNumberOfPhotos = -1
    private var orgTimeIntervalOfPhotos = -1
    private var orgServerUpdateTime = -1
    private var chgNumberOfPhotos = -1
    private var chgTimeIntervalOfPhotos = -1
    private var chgServerUpdateTime = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMenuBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)

        replaceFragment(AppSimpleFragment.newInstance(AppSimpleFragment.Layout.PROGRESS))

        presenter = MenuPresenter(
            MenuInteractor(
                AppSettingsImpl(this),
                AppDatabaseImpl(),
                GoogleWebApisImpl(this, GoogleSignInApiImpl(), GoogleOAuth2ApiImpl(), PhotosLibraryApiImpl())
            ),
            MenuRouter()
        )
        presenter?.create(this)
    }

    override fun onDestroy() {
        presenter?.destroy()
        presenter = null

        super.onDestroy()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean =
        when (item.itemId) {
            android.R.id.home -> {
                if (!showSettingsChangedDialog()) onBackPressed()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean =
        when (keyCode) {
            KeyEvent.KEYCODE_BACK -> {
                if (showSettingsChangedDialog()) true else super.onKeyDown(keyCode, event)
            }
            else -> super.onKeyDown(keyCode, event)
        }

    override fun onPreferenceStartFragment(caller: PreferenceFragmentCompat, pref: Preference): Boolean {
        val fragment = supportFragmentManager.fragmentFactory.instantiate(classLoader, pref.fragment).apply {
            setTargetFragment(caller, 0)
            arguments = caller.arguments
        }
        addFragment(fragment)
        return true
    }

    override fun onDialogResult(requestCode: Int, resultCode: Int, data: Intent) {
        logI("onDialogResult() requestCode: $requestCode, resultCode: $resultCode")

        when (DialogRequest.convertFromCode(requestCode)) {
            DialogRequest.ALERT_CHANGED_SETTINGS -> {
                finish()
            }
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

    override fun onCreateViewFragment(className: String) {
        when (className) {
            MenuFragment::class.java.simpleName -> binding.toolbar.setTitle(R.string.menu)
            SettingsFragment::class.java.simpleName -> binding.toolbar.setTitle(R.string.settings)
            else -> {}
        }
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

    override fun onChangedNumberOfPhotos(changedValue: Int) {
        logI("onChangedNumberOfPhotos: $chgNumberOfPhotos -> $changedValue")
        chgNumberOfPhotos = changedValue
    }

    override fun onChangedTimeIntervalOfPhotos(changedValue: Int) {
        logI("onChangedNumberOfPhotos: $chgTimeIntervalOfPhotos -> $changedValue")
        chgTimeIntervalOfPhotos = changedValue
    }

    override fun onChangedServerUpdateTime(changedValue: Int) {
        logI("onChangedNumberOfPhotos: $chgServerUpdateTime -> $changedValue")
        chgServerUpdateTime = changedValue
    }

    override fun getActivity(): Activity = this

    override fun onCreateResult(
        accountName: String, emailAddress: String,
        numberOfPhotos: Int, timeIntervalOfPhotos: Int, serverUpdateTime: Int
    ) {
        orgNumberOfPhotos = numberOfPhotos
        orgTimeIntervalOfPhotos = timeIntervalOfPhotos
        orgServerUpdateTime = serverUpdateTime
        chgNumberOfPhotos = orgNumberOfPhotos
        chgTimeIntervalOfPhotos = orgTimeIntervalOfPhotos
        chgServerUpdateTime = orgServerUpdateTime
        replaceFragment(MenuFragment.newInstance(this, accountName, emailAddress))
    }

    override fun onFailedChangeUser() {
        showDialogFragment(DialogRequest.FAILED_CHANGE_USER)
    }

    override fun onFailedSignOut() {
        showDialogFragment(DialogRequest.FAILED_SIGN_OUT)
    }

    /**
     * ダイアログを表示する.
     */
    private fun showDialogFragment(request: DialogRequest) {
        logI("Show dialog fragment: $request")
        request.show(this, supportFragmentManager)
    }

    private fun showSettingsChangedDialog(): Boolean {
        if (supportFragmentManager.backStackEntryCount > 0) {
            logI("Back stack fragments are remained.")
            return false
        }
        if (orgNumberOfPhotos == chgNumberOfPhotos
            && orgTimeIntervalOfPhotos == chgTimeIntervalOfPhotos
            && orgServerUpdateTime == chgServerUpdateTime) {
            logI("No changed settings.")
            return false
        }
        showDialogFragment(DialogRequest.ALERT_CHANGED_SETTINGS)
        return true
    }
}