package com.ae.apps.messagecounter.fragments


import android.os.Bundle
import android.support.v7.preference.Preference
import android.support.v7.preference.PreferenceFragmentCompat
import com.ae.apps.messagecounter.R
import com.ae.apps.messagecounter.data.preferences.PREF_KEY_MESSAGE_LIMIT_VALUE


/**
 * Hosts the Preference fragment for the application
 *
 */
class SettingsFragment : PreferenceFragmentCompat() {

    companion object {
        fun newInstance() = SettingsFragment()
    }

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        addPreferencesFromResource(R.xml.preferences)

        // Make sure that only valid numeric value is entered for message limit
        val messageLimitPref = preferenceScreen.findPreference(PREF_KEY_MESSAGE_LIMIT_VALUE)
        messageLimitPref.onPreferenceChangeListener = Preference.OnPreferenceChangeListener { _: Preference, newValue: Any ->
            try {
                Integer.valueOf(newValue.toString())
                true
            } catch (e: NumberFormatException) {
                false
            }
        }
    }

}
