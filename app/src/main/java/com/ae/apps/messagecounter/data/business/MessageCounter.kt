package com.ae.apps.messagecounter.data.business

import android.content.Context
import android.net.Uri
import android.util.Log
import com.ae.apps.common.managers.SMSManager
import com.ae.apps.messagecounter.data.preferences.PreferenceRepository
import com.ae.apps.messagecounter.data.repositories.CounterRepository
import com.ae.apps.messagecounter.data.repositories.IgnoredNumbersRepository
import com.ae.apps.messagecounter.data.repositories.getIndexFromDate
import org.jetbrains.anko.doAsync
import java.util.*

class MessageCounter(private val counterRepository: CounterRepository,
                     private val ignoreNumbersRepository: IgnoredNumbersRepository,
                     private val preferenceRepository: PreferenceRepository) {

    companion object {
        fun newInstance(counterRepository: CounterRepository,
                        ignoreNumbersRepository: IgnoredNumbersRepository,
                        preferenceRepository: PreferenceRepository) = MessageCounter(counterRepository,
                ignoreNumbersRepository, preferenceRepository)
    }

    private val DEFAULT_INDEX_TIME_STAMP = "0"

    /**
     * Index messages that are in the sms database that have not been counted before
     *
     * @param context The context used for accessing the SMS API
     */
    fun indexMessages(context: Context, observer: MessageCounterObserver?) {
        var newMessagesAdded = 0
        var lastIndexedTimeStamp = ""
        var lastIndexedMessageId = ""
        val lastMessageTimeStamp = preferenceRepository.getLastSentTimeStamp(DEFAULT_INDEX_TIME_STAMP)

        doAsync {
            // Query the SMS Database to read messages
            val newMessagesCursor = context.contentResolver.query(
                    Uri.parse(SMSManager.SMS_URI_ALL),
                    SMS_TABLE_PROJECTION,
                    SELECT_SENT_MESSAGES_AFTER_DATE,
                    arrayOf(lastMessageTimeStamp),
                    SORT_BY_DATE)
            try {
                val messageSentDate = Calendar.getInstance()
                val newMessagesCount = newMessagesCursor?.count ?: 0
                Log.e("CounterViewModel", "Messages to be processed $newMessagesCount")
                if (newMessagesCount > 0 && newMessagesCursor.moveToFirst()) {
                    do {
                        // Convert this row into a Message object and handle multipart messages
                        val message = getMessageFromCursor(newMessagesCursor)

                        messageSentDate.timeInMillis = java.lang.Long.parseLong(message.date)

                        // Count this message against the date it was sent
                        val dateIndex = getIndexFromDate(messageSentDate.time)
                        lastIndexedTimeStamp = message.date
                        lastIndexedMessageId = message.id

                        // Only index if the number is not ignored explicitly
                        if (!ignoreNumbersRepository.checkIfNumberIsIgnored(message.address)) {
                            counterRepository.addCount(dateIndex, message.messageCount)
                            newMessagesAdded += message.messageCount
                        }
                    } while (newMessagesCursor.moveToNext())
                }
            } catch (e: Exception) {
                Log.e("CounterViewModel", Log.getStackTraceString(e))
            } finally {
                newMessagesCursor?.close()
                preferenceRepository.setHistoricMessageIndexed()
            }

            if (newMessagesAdded > 0) {
                preferenceRepository.setLastSentTimeStamp(lastIndexedTimeStamp)
                preferenceRepository.setLastSentMessageId(lastIndexedMessageId)
                observer?.onIndexCompleted()
            }
        }
    }

    interface MessageCounterObserver {
        fun onIndexCompleted()
    }
}