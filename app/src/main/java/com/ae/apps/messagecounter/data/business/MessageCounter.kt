package com.ae.apps.messagecounter.data.business

import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.preference.PreferenceManager
import android.util.Log
import com.ae.apps.common.managers.SMSManager
import com.ae.apps.messagecounter.data.AppDatabase
import com.ae.apps.messagecounter.data.preferences.PreferenceRepository
import com.ae.apps.messagecounter.data.repositories.CounterRepository
import com.ae.apps.messagecounter.data.repositories.IgnoredNumbersRepository
import com.ae.apps.messagecounter.data.repositories.getIndexFromDate
import org.jetbrains.anko.doAsync
import java.util.*

/**
 * The logic for counting sent messages from SMS API and to update the database
 * for MessageCounter
 */
class MessageCounter(private val counterRepository: CounterRepository,
                     private val ignoreNumbersRepository: IgnoredNumbersRepository,
                     private val preferenceRepository: PreferenceRepository) {

    companion object {
        fun newInstance(counterRepository: CounterRepository,
                        ignoreNumbersRepository: IgnoredNumbersRepository,
                        preferenceRepository: PreferenceRepository) = MessageCounter(counterRepository,
                ignoreNumbersRepository, preferenceRepository)

        fun newInstance(context: Context): MessageCounter{
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
    fun indexMessages(context: Context, observer: MessageCounterObserver?) {
        var newMessagesAdded = 0
        var lastIndexedTimeStamp = ""
        var lastIndexedMessageId = ""

        doAsync {
            val newMessagesCursor = getNewMessagesCursor(context)
            try {
                Log.d("CounterViewModel", "Messages to be processed $newMessagesCursor.count")
                if (newMessagesCursor.count > 0 && newMessagesCursor.moveToFirst()) {
                    // Since this method would be invoked multiple times when SMS database changes,
                    // updating the lastSentTimeStamp to prevent duplicate reads
                    preferenceRepository.setLastSentTimeStamp( System.currentTimeMillis().toString() )
                    val messageSentDate = Calendar.getInstance()
                    do {
                        // Convert this row into a Message object and handle multipart messages
                        val message = getMessageFromCursor(newMessagesCursor)

                        messageSentDate.timeInMillis = java.lang.Long.parseLong(message.date)
                        lastIndexedTimeStamp = message.date
                        lastIndexedMessageId = message.id

                        if (ignoreNumbersRepository.checkIfNumberIsNotIgnored(message.address)) {
                            counterRepository.addCount(getIndexFromDate(messageSentDate.time),
                                    message.messageCount)
                            newMessagesAdded += message.messageCount
                        }
                    } while (newMessagesCursor.moveToNext())
                }
            } catch (e: Exception) {
                Log.e("CounterViewModel", Log.getStackTraceString(e))
            } finally {
                newMessagesCursor.close()
                preferenceRepository.setHistoricMessageIndexed()
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
     * @param context The context required fir accessing the SMS API
     */
    fun checkIfUnIndexedMessagesExist(context: Context): Boolean {
        val newMessagesCursor = getNewMessagesCursor(context)
        val newMessagesExists = newMessagesCursor.count > 0 && newMessagesCursor.moveToFirst()
        newMessagesCursor.close()
        return newMessagesExists
    }

    private fun getNewMessagesCursor(context: Context): Cursor {
        val lastMessageTimeStamp = preferenceRepository.getLastSentTimeStamp()
        return context.contentResolver.query(
                Uri.parse(SMSManager.SMS_URI_ALL),
                SMS_TABLE_PROJECTION,
                SELECT_SENT_MESSAGES_AFTER_DATE,
                arrayOf(lastMessageTimeStamp),
                SORT_BY_DATE)
    }

    fun checkIfMessageLimitCrossed():Boolean{
        if(preferenceRepository.messageLimitNotificationEnabled()){
            val startIndex = getIndexFromDate(preferenceRepository.getCycleStartDate())
            val currentLimit = preferenceRepository.getMessageLimitValue()

            val currentCount = counterRepository.getTotalCountSince(startIndex)
            if(currentCount >= currentLimit){
                return true
            }
        }
        return false
    }

    /**
     * An interface that represents observers who are interested in the completion of indexing
     */
    interface MessageCounterObserver {
        fun onIndexCompleted()
    }
}