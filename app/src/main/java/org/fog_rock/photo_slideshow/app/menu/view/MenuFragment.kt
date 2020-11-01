package org.fog_rock.photo_slideshow.app.menu.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import org.fog_rock.photo_slideshow.BuildConfig
import org.fog_rock.photo_slideshow.R

class MenuFragment : PreferenceFragmentCompat() {

    companion object {

        private const val ARGS_ACCOUNT_NAME = "account_name"
        private const val ARGS_EMAIL_ADDRESS = "email_address"

        fun newInstance(
            rootKey: String? = null,
            accountName: String,
            emailAddress: String
        ): Fragment = MenuFragment().apply {
            arguments = Bundle().apply {
                if (!rootKey.isNullOrEmpty()) putString(ARG_PREFERENCE_ROOT, rootKey)
                putString(ARGS_ACCOUNT_NAME, accountName)
                putString(ARGS_EMAIL_ADDRESS, emailAddress)
            }
        }
    }

    /**
     * コールバック
     */
    interface Callback {
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

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.preferences_menu, rootKey)

        findPreference<Preference>(getString(R.string.pref_key_account_name))?.summary =
            arguments?.getString(ARGS_ACCOUNT_NAME)

        findPreference<Preference>(getString(R.string.pref_key_email_address))?.summary =
            arguments?.getString(ARGS_EMAIL_ADDRESS)

        findPreference<Preference>(getString(R.string.pref_key_app_version))?.summary =
            BuildConfig.VERSION_NAME

        findPreference<Preference>(getString(R.string.pref_key_license_info))?.setOnPreferenceClickListener {
            getActivityCallback()?.onClickedLicenseInfo()
            true
        }
        findPreference<Preference>(getString(R.string.pref_key_change_user))?.setOnPreferenceClickListener {
            getActivityCallback()?.onClickedChangeUser()
            true
        }
        findPreference<Preference>(getString(R.string.pref_key_sign_out))?.setOnPreferenceClickListener {
            getActivityCallback()?.onClickedSignOut()
            true
        }
    }

    private fun getActivityCallback(): Callback? {
        val activity = requireActivity()
        return if (activity is Callback) activity else null
    }
}