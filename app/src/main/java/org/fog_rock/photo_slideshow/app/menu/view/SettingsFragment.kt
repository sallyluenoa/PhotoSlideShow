package org.fog_rock.photo_slideshow.app.menu.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.preference.ListPreference
import androidx.preference.PreferenceFragmentCompat
import org.fog_rock.photo_slideshow.R

class SettingsFragment : PreferenceFragmentCompat() {

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.preferences_settings, rootKey)

        val numberListPreference = findPreference<ListPreference>(getString(R.string.pref_key_number_of_photos))
        numberListPreference?.setOnPreferenceChangeListener { preference, newValue ->
            if (preference is ListPreference) {
                preference.summary = getString(R.string.number_of_photos_summary, preference.entry)
                true
            } else false
        }

        val intervalListPreference = findPreference<ListPreference>(getString(R.string.pref_key_time_interval_of_photos))
        intervalListPreference?.setOnPreferenceChangeListener { preference, newValue ->
            if (preference is ListPreference) {
                preference.summary = getString(R.string.time_interval_of_photos_summary, preference.entry)
                true
            } else false
        }

        val updateTimeListPreference = findPreference<ListPreference>(getString(R.string.pref_key_server_update_time))
        updateTimeListPreference?.setOnPreferenceChangeListener { preference, newValue ->
            if (preference is ListPreference) {
                preference.summary = getString(R.string.server_update_time_summary, preference.entry)
                true
            } else false
        }
    }
}