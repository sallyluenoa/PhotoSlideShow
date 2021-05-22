package org.fog_rock.photo_slideshow.app.menu.view

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import org.fog_rock.photo_slideshow.BuildConfig
import org.fog_rock.photo_slideshow.R
import org.fog_rock.photo_slideshow.app.module.ui.extension.FragmentCallback
import org.fog_rock.photo_slideshow.app.module.ui.extension.getActivityCallback
import org.fog_rock.photo_slideshow.core.extension.tag

class MenuFragment : PreferenceFragmentCompat() {

    companion object {

        private const val ARGS_ACCOUNT_NAME = "account_name"
        private const val ARGS_EMAIL_ADDRESS = "email_address"

        fun newInstance(
            context: Context,
            accountName: String,
            emailAddress: String
        ): Fragment = MenuFragment().apply {
            arguments = Bundle().apply {
                putString(ARG_PREFERENCE_ROOT, context.getString(R.string.pref_key_root))
                putString(ARGS_ACCOUNT_NAME, accountName)
                putString(ARGS_EMAIL_ADDRESS, emailAddress)
            }
        }
    }

    /**
     * コールバック
     */
    interface Callback: FragmentCallback {
        /**
         * ライセンス情報がタップされたときのイベント.
         */
        fun onClickedLicenseInfo()

        /**
         * ユーザー切り替えがタップされたときのイベント.
         */
        fun onClickedChangeUser()

        /**
         * サインアウトがタップされたときのイベント.
         */
        fun onClickedSignOut()
    }

    private val callback: Callback? by lazy {
        getActivityCallback()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        callback?.onCreateViewFragment(tag())
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.preferences_menu, rootKey)

        findPreference<Preference>(getString(R.string.pref_key_account_name))?.summary =
            arguments?.getString(ARGS_ACCOUNT_NAME)

        findPreference<Preference>(getString(R.string.pref_key_email_address))?.summary =
            arguments?.getString(ARGS_EMAIL_ADDRESS)

        findPreference<Preference>(getString(R.string.pref_key_app_version))?.summary =
            BuildConfig.VERSION_NAME

        findPreference<Preference>(getString(R.string.pref_key_license_info))?.setOnPreferenceClickListener {
            callback?.onClickedLicenseInfo()
            true
        }
        findPreference<Preference>(getString(R.string.pref_key_change_user))?.setOnPreferenceClickListener {
            callback?.onClickedChangeUser()
            true
        }
        findPreference<Preference>(getString(R.string.pref_key_sign_out))?.setOnPreferenceClickListener {
            callback?.onClickedSignOut()
            true
        }
    }
}