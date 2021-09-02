package org.fog_rock.photo_slideshow.app.module.lib.impl

import android.content.Context
import android.content.SharedPreferences
import androidx.preference.PreferenceManager
import org.fog_rock.photo_slideshow.R
import org.fog_rock.photo_slideshow.app.module.lib.AppSettings

class AppSettingsImpl(private val context: Context): AppSettings {

    override fun getNumberOfPhotos(): Int =
        (getDefaultSharedPreferences()
            .getString(context.getString(R.string.pref_key_number_of_photos), null)
            ?: context.getString(R.string.default_value_number_of_photos))
            .toInt()

    override fun getTimeIntervalOfPhotos(): Int =
        (getDefaultSharedPreferences()
            .getString(context.getString(R.string.pref_key_time_interval_of_photos), null)
            ?: context.getString(R.string.default_value_time_interval_of_photos))
            .toInt()

    override fun getServerUpdateTime(): Int =
        (getDefaultSharedPreferences()
            .getString(context.getString(R.string.pref_key_server_update_time), null)
            ?: context.getString(R.string.default_value_server_update_time))
            .toInt()

    private fun getDefaultSharedPreferences(): SharedPreferences =
        PreferenceManager.getDefaultSharedPreferences(context)
}