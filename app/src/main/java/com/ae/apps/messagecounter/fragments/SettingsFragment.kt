package com.ae.apps.messagecounter.fragments


import android.os.Bundle
import android.support.v7.preference.PreferenceFragmentCompat
import com.ae.apps.messagecounter.R


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
    }

}
