package com.ae.apps.messagecounter.data.viewmodels

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import android.content.Context
import android.net.Uri
import android.text.TextUtils
import android.util.Log
import com.ae.apps.common.managers.SMSManager
import com.ae.apps.common.utils.CommonUtils
import com.ae.apps.messagecounter.data.models.SentCountDetails
import com.ae.apps.messagecounter.data.preferences.PreferenceRepository
import com.ae.apps.messagecounter.data.repositories.*
import org.jetbrains.anko.doAsync
import java.util.*

/**
 * ViewModel for holding the data for SentCountFragment
 */
class CounterViewModel(private val counterRepository: CounterRepository,
                       private val preferenceRepository: PreferenceRepository) : ViewModel() {

    val TAG = "CounterViewModel"

    private var mSentCountDetails:MutableLiveData<SentCountDetails> = MutableLiveData()

    fun getSentCountDetails(): LiveData<SentCountDetails> = mSentCountDetails

    fun getSentCountData() {
        val limit: Int = preferenceRepository.getMessageLimitValue()
        val cycleStartDate = preferenceRepository.getCycleStartDate()
        val today = Calendar.getInstance().time

        doAsync {
            val prevCycleStartDate = getPrevCycleStartDate(cycleStartDate)
            val prevCycle = getCycleSentCount(prevCycleStartDate)
            var sentTodayCount = counterRepository.getCount(getIndexFromDate(today))
            if (sentTodayCount == -1) sentTodayCount = 0
            val sentCycleCount = counterRepository.getTotalCountSince(getIndexFromDate(cycleStartDate))
            val lastCycleCount = counterRepository.getTotalCountBetween(prevCycle.startDateIndex, prevCycle.endDateIndex)
            val startWeekCount = counterRepository.getTotalCountSince(getIndexFromDate(getWeekStartDate()))
            val startYearCount = counterRepository.getTotalCountSince(getIndexFromDate(getYearStartDate()))

            val sentCountDetails = SentCountDetails(limit, sentTodayCount, sentCycleCount,
                    startWeekCount, startYearCount, lastCycleCount,
                    getDurationDateString(cycleStartDate))
            mSentCountDetails.postValue(sentCountDetails)
        }
    }

    /**
     * Index messages that are in the sms database that have not been counted before
     *
     * @param context The context used for accessing the SMS API
     * @param messageId An optional messageId that represents a new SMS which shouldn't be counted
     *                  by this method
     */
    fun indexMessages(context: Context, messageId: String = "") {
        val defaultTimeStamp = getStartTimeStamp(CommonUtils.isFirstInstall(context))
        // Returns the last indexed message's time stamp from shared preference
        val lastMessageTimeStamp = preferenceRepository.getLastSentTimeStamp(defaultTimeStamp)

        // Need a valid reference time for indexing
        if(TextUtils.isEmpty(lastMessageTimeStamp)) {
            return
        }

        var newMessagesAdded = 0
        var lastIndexedTimeStamp = ""

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
                if (newMessagesCount > 0 && newMessagesCursor.moveToFirst()) {
                    Log.d(TAG, "Read from SMS Database")
                    do {
                        // Convert this row into a Message object and handle multipart messages
                        val message = getMessageFromCursor(newMessagesCursor)

                        messageSentDate.timeInMillis = java.lang.Long.parseLong(message.date)

                        // Skip the new message id which will be added by calling method
                        if (messageId != message.id) {
                            // Count this message against the date it was sent
                            val dateIndex = getIndexFromDate(messageSentDate.time)
                            lastIndexedTimeStamp = message.date

                            //if (!ignoreNumbersManager.checkIfNumberIgnored(message.getAddress())) {
                            counterRepository.addCount(dateIndex, message.messageCount)
                            newMessagesAdded += message.messageCount
                            //}
                        }
                    } while (newMessagesCursor.moveToNext())
                }
            } catch (e: Exception) {
                Log.e(TAG, e.message)
                Log.e(TAG, Log.getStackTraceString(e))
            } finally {
                newMessagesCursor?.close()
                preferenceRepository.setHistoricMessageIndexed()
            }

            // Update the model
            if(newMessagesAdded > 0) {
                preferenceRepository.setLastSentTimeStamp(lastIndexedTimeStamp)
                getSentCountData()
            }
        }
    }

    private fun getStartTimeStamp(indexAllMessages: Boolean): String {
        var defaultTimeStamp = ""
        if (indexAllMessages) {
            defaultTimeStamp = "0"
        }
        return defaultTimeStamp
    }
}