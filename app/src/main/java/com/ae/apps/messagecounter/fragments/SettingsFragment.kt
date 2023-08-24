/*
 * Copyright 2018 Midhun Harikumar
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.ae.apps.messagecounter.fragments

import android.os.Build
import android.os.Bundle
import android.preference.Preference
import android.preference.PreferenceFragment
import com.ae.apps.messagecounter.R
import com.ae.apps.messagecounter.data.preferences.PREF_KEY_MESSAGE_LIMIT_VALUE

/**
 * Hosts the Preference fragment for the application
 *
 */
class SettingsFragment : PreferenceFragment() {

    companion object {
        fun newInstance() = SettingsFragment()
    }

    fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        // We have one less preference for Android 7 and up
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            addPreferencesFromResource(R.xml.preferences_n_up)
        } else {
            addPreferencesFromResource(R.xml.preferences)
        }

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
