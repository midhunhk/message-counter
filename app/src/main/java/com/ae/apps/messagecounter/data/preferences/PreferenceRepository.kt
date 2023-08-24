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
package com.ae.apps.messagecounter.data.preferences

import android.content.SharedPreferences
import com.ae.apps.messagecounter.data.business.DEFAULT_INDEX_TIME_STAMP
import java.util.*

class PreferenceRepository private constructor(private val preferences: SharedPreferences) {

    companion object {
        fun newInstance(preferences: SharedPreferences) = PreferenceRepository(preferences)
    }

    fun getMessageLimitValue(): Int {
        val rawVal = preferences.getString(PREF_KEY_MESSAGE_LIMIT_VALUE, DEFAULT_MESSAGE_LIMIT.toString())
        val limit: Int = try {
            Integer.valueOf(rawVal!!)
        } catch (e: NumberFormatException) {
            DEFAULT_MESSAGE_LIMIT
        }

        return limit
    }

    fun getCycleStartDate(): Date {
        val cycleStart = Integer.valueOf(preferences.getString(PREF_KEY_CYCLE_START_DATE, DEFAULT_CYCLE_START_DATE)!!)
        val calendar = Calendar.getInstance()
        if (calendar.get(Calendar.DATE) < cycleStart) {
            calendar.add(Calendar.MONTH, -1)
        }
        calendar.set(Calendar.DATE, cycleStart)
        return calendar.time
    }

    fun historicMessagesIndexed() = preferences.getBoolean(PREF_KEY_HISTORIC_DATA_INDEXED, false)

    fun setHistoricMessageIndexed() = preferences.edit()
            .putBoolean(PREF_KEY_HISTORIC_DATA_INDEXED, true)
            .apply()

    fun getLastSentTimeStamp() = preferences.getString(PREF_KEY_LAST_SENT_TIME_STAMP, DEFAULT_INDEX_TIME_STAMP)!!

    fun getLastSentMessageId() = preferences.getString(PREF_KEY_LAST_SENT_MESSAGE_ID, "0")!!

    fun setLastSentTimeStamp(timeStamp: String) = preferences.edit()
            .putString(PREF_KEY_LAST_SENT_TIME_STAMP, timeStamp)
            .apply()

    fun messageLimitNotificationEnabled() = preferences.getBoolean(PREF_KEY_ENABLE_NOTIFICATION, false)

    fun setLastSentMessageId(messageId: String) = preferences.edit()
            .putString(PREF_KEY_LAST_SENT_MESSAGE_ID, messageId)
            .apply()

    fun backgroundServiceEnabled() = preferences.getBoolean(PREF_KEY_ENABLE_BACKGROUND_SERVICE, true)

    fun hasDonated() = preferences.getBoolean(PREF_KEY_DONATIONS_MADE, false)

    fun saveDonationsMade() = preferences.edit()
            .putBoolean(PREF_KEY_DONATIONS_MADE, true)
            .apply()

    fun hasRuntimePermissions() = preferences.getBoolean(PREF_KEY_VALID_RUN_TIME_PERMISSIONS, false)

    fun saveRuntimePermissions(value: Boolean) = preferences.edit()
            .putBoolean(PREF_KEY_VALID_RUN_TIME_PERMISSIONS, value)
            .apply()

    fun getSettingsHintReviewed() = preferences.getBoolean(PREF_KEY_SETTINGS_HINT_REVIEWED, false)

    fun setSettingsHintReviewed() = preferences.edit()
            .putBoolean(PREF_KEY_SETTINGS_HINT_REVIEWED, true)
            .apply()

    fun isIndexInProcess() = preferences.getBoolean(PREF_KEY_INDEX_IN_PROCESS, false)

    fun setIndexInProcess(indexStatus:Boolean) = preferences.edit()
            .putBoolean(PREF_KEY_INDEX_IN_PROCESS, indexStatus)
            .apply()

    fun getLastBackupTime() = preferences.getLong(PREF_KEY_LAST_BACKUP_TIME, 0)

    fun setLastBackupTime(backupTime:Long) = preferences.edit()
            .putLong(PREF_KEY_LAST_BACKUP_TIME, backupTime)
            .apply()
}