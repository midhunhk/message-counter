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
package com.ae.apps.messagecounter.data.business

import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.preference.PreferenceManager
import android.util.Log
import com.ae.apps.common.managers.SMSManager
import com.ae.apps.messagecounter.data.AppDatabase
import com.ae.apps.messagecounter.data.models.SentCountDetails
import com.ae.apps.messagecounter.data.preferences.PreferenceRepository
import com.ae.apps.messagecounter.data.repositories.CounterRepository
import com.ae.apps.messagecounter.data.repositories.IgnoredNumbersRepository
import com.ae.apps.messagecounter.data.repositories.getDurationDateString
import com.ae.apps.messagecounter.data.repositories.getIndexFromDate
import org.jetbrains.anko.doAsync
import java.lang.Long
import java.util.*

/**
 * The logic for counting sent messages from SMS API and to update the database
 * for MessageCounter
 */
class MessageCounter(private val counterRepository: CounterRepository,
                     private val ignoreNumbersRepository: IgnoredNumbersRepository,
                     private val preferenceRepository: PreferenceRepository) {

    companion object {
        private const val TAG = "MessageCounter"
        private const val TIME_DELTA = 500
        fun newInstance(counterRepository: CounterRepository,
                        ignoreNumbersRepository: IgnoredNumbersRepository,
                        preferenceRepository: PreferenceRepository) = MessageCounter(counterRepository,
                ignoreNumbersRepository, preferenceRepository)

        fun newInstance(context: Context): MessageCounter {
            val preferenceRepository = PreferenceRepository.newInstance(PreferenceManager.getDefaultSharedPreferences(context))
            val counterRepository = CounterRepository.getInstance(AppDatabase.getInstance(context).counterDao())
            val ignoreNumbersRepository = IgnoredNumbersRepository.getInstance(AppDatabase.getInstance(context).ignoredNumbersDao())
            return MessageCounter(counterRepository, ignoreNumbersRepository, preferenceRepository)
        }
    }

    /**
     * Index messages that are in the sms database that have not been counted before
     *
     * @param context The context used for accessing the SMS API
     */
    @Synchronized
    fun indexMessages(context: Context, observer: MessageCounterObserver?) {
        // Prevent multiple indexing process to run at the same time
        if (!preferenceRepository.isIndexInProcess()) {
            doMessageIndex(context, observer)
        } else {
            Log.d(TAG, "Message Indexing in Process")
        }
    }

    private fun doMessageIndex(context: Context, observer: MessageCounterObserver?) {
        var newMessagesAdded = 0
        var lastIndexedTimeStamp = ""
        var lastIndexedMessageId = ""

        doAsync {
            val newMessagesCursor = getNewMessagesCursor(context)
            try {
                if (checkIfRowsPresent(newMessagesCursor)) {
                    Log.d(TAG, "Messages to be processed")
                    preferenceRepository.setIndexInProcess(true)
                    // Since this method would be invoked multiple times when SMS database changes,
                    // updating the lastSentTimeStamp to prevent duplicate reads
                    preferenceRepository.setLastSentTimeStamp(getDeltaTimeStamp())
                    val messageSentDate = Calendar.getInstance()
                    do {
                        // Convert this row into a Message object and handle multipart messages
                        val message = getMessageFromCursor(newMessagesCursor!!)

                        messageSentDate.timeInMillis = Long.parseLong(message.date)
                        lastIndexedTimeStamp = message.date
                        lastIndexedMessageId = message.id

                        if (ignoreNumbersRepository.checkIfNumberIsNotIgnored(message.address)) {
                            counterRepository.addCount(getIndexFromDate(messageSentDate.time),
                                    message.messageCount)
                            newMessagesAdded += message.messageCount
                        }
                    } while (newMessagesCursor!!.moveToNext())
                }
            } catch (e: Exception) {
                Log.e(TAG, Log.getStackTraceString(e))
            } finally {
                newMessagesCursor?.close()
                preferenceRepository.setHistoricMessageIndexed()
                preferenceRepository.setIndexInProcess(false)
            }

            if (newMessagesAdded > 0) {
                preferenceRepository.setLastSentTimeStamp(lastIndexedTimeStamp)
                preferenceRepository.setLastSentMessageId(lastIndexedMessageId)
                observer?.onIndexCompleted()
            }
        }
    }

    /**
     * Checks if there are any sent messages that are yet to be indexed
     *
     * @param context The context required for accessing the SMS API
     */
    fun checkIfUnIndexedMessagesExist(context: Context): Boolean {
        val newMessagesCursor = getNewMessagesCursor(context)
        val newMessagesExists = checkIfRowsPresent(newMessagesCursor)
        newMessagesCursor?.close()
        return newMessagesExists
    }

    private fun checkIfRowsPresent(cursor: Cursor?) = null != cursor &&
            cursor.count > 0 && cursor.moveToFirst()

    private fun getNewMessagesCursor(context: Context): Cursor? {
        val lastMessageTimeStamp = preferenceRepository.getLastSentTimeStamp()
        val lastSentMessageId = preferenceRepository.getLastSentMessageId()
        return context.contentResolver.query(
                Uri.parse(SMSManager.SMS_URI_ALL),
                SMS_TABLE_PROJECTION,
                SELECT_SENT_MESSAGES_AFTER_DATE + SELECT_SENT_MESSAGES_AFTER_LAST,
                arrayOf(lastMessageTimeStamp, lastSentMessageId),
                SORT_BY_DATE)
    }

    private fun getDeltaTimeStamp() = (System.currentTimeMillis() + TIME_DELTA).toString()

    fun checkIfMessageLimitCrossed(): Boolean {
        if (preferenceRepository.messageLimitNotificationEnabled()) {
            val startIndex = getIndexFromDate(preferenceRepository.getCycleStartDate())
            val currentLimit = preferenceRepository.getMessageLimitValue()

            val currentCount = counterRepository.getTotalCountSince(startIndex)
            if (currentCount >= currentLimit) {
                return true
            }
        }
        return false
    }

    /**
     * Reads the data from Repositories to be consumed by any widgets
     * Make sure to call this from another thread since db access on main thread
     * will not work
     */
    fun getSentCountDetailsForWidget(): SentCountDetails {
        val limit: Int = preferenceRepository.getMessageLimitValue()
        val cycleStartDate = preferenceRepository.getCycleStartDate()
        val today = Calendar.getInstance().time
        var sentTodayCount = counterRepository.getCount(getIndexFromDate(today))
        if (sentTodayCount == -1) sentTodayCount = 0
        val sentCycleCount = counterRepository.getTotalCountSince(getIndexFromDate(cycleStartDate))

        return SentCountDetails(limit, sentTodayCount, sentCycleCount,
                0, 0, 0,
                getDurationDateString(cycleStartDate), "")
    }

    /**
     * An interface that represents observers who are interested in the completion of indexing
     */
    interface MessageCounterObserver {
        fun onIndexCompleted()
    }

}