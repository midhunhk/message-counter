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
        val limit: Int
        limit = try {
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

    fun getLastSentMessageId() = preferences.getString(PREF_KEY_LAST_SENT_MESSAGE_ID, "")!!

    fun setLastSentTimeStamp(timeStamp: String) = preferences.edit()
            .putString(PREF_KEY_LAST_SENT_TIME_STAMP, timeStamp)
            .apply()

    fun messageLimitNotificationEnabled() = preferences.getBoolean(PREF_KEY_ENABLE_NOTIFICATION, false)

    fun setLastSentMessageId(messageId: String) = preferences.edit()
            .putString(PREF_KEY_LAST_SENT_MESSAGE_ID, messageId)
            .apply()

    fun backgroundServiceEnabled() = preferences.getBoolean(PREF_KEY_ENABLE_BACKGROUND_SERVICE, false)

    fun hasDonated() = preferences.getBoolean(PREF_KEY_DONATIONS_MADE, false)

    fun saveDonationsMade() = preferences.edit()
            .putBoolean(PREF_KEY_DONATIONS_MADE, true)
            .apply()
}