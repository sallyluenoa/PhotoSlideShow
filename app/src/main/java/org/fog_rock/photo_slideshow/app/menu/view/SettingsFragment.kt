package org.fog_rock.photo_slideshow.app.menu.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.preference.ListPreference
import androidx.preference.PreferenceFragmentCompat
import org.fog_rock.photo_slideshow.R
import org.fog_rock.photo_slideshow.core.extension.tag

class SettingsFragment : PreferenceFragmentCompat() {

    /**
     * コールバック
     */
    interface Callback: FragmentCallback {
        /**
         * 写真の表示枚数が変更されたときのイベント.
         */
        fun onChangedNumberOfPhotos(changedValue: Int)

        /**
         * 写真の表示時間が変更されたときのイベント.
         */
        fun onChangedTimeIntervalOfPhotos(changedValue: Int)

        /**
         * サーバーの更新時間が変更されたときのイベント.
         */
        fun onChangedServerUpdateTime(changedValue: Int)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        getActivityCallback()?.onCreateViewFragment(tag())
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.preferences_settings, rootKey)

        findPreference<ListPreference>(getString(R.string.pref_key_number_of_photos))?.apply {
            setSummaryProvider {
                if (it is ListPreference) getString(R.string.number_of_photos_summary, it.entry) else ""
            }
            setOnPreferenceChangeListener { _, newValue ->
                getActivityCallback()?.onChangedNumberOfPhotos(newValue.toString().toInt())
                true
            }
        }

        findPreference<ListPreference>(getString(R.string.pref_key_time_interval_of_photos))?.apply {
            setSummaryProvider {
                if (it is ListPreference) getString(R.string.time_interval_of_photos_summary, it.entry) else ""
            }
            setOnPreferenceChangeListener { _, newValue ->
                getActivityCallback()?.onChangedTimeIntervalOfPhotos(newValue.toString().toInt())
                true
            }
        }

        findPreference<ListPreference>(getString(R.string.pref_key_server_update_time))?.apply {
            setSummaryProvider {
                if (it is ListPreference) getString(R.string.server_update_time_summary, it.entry) else ""
            }
            setOnPreferenceChangeListener { _, newValue ->
                getActivityCallback()?.onChangedServerUpdateTime(newValue.toString().toInt())
                true
            }
        }
    }

    private fun getActivityCallback(): Callback? {
        val activity = requireActivity()
        return if (activity is Callback) activity else null
    }
}