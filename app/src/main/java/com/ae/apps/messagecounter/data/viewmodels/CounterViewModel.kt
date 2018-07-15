package com.ae.apps.messagecounter.data.viewmodels

import android.arch.lifecycle.ViewModel
import android.content.Context
import android.net.Uri
import android.util.Log
import android.widget.Toast
import com.ae.apps.common.managers.SMSManager
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

    fun getSentCountData(): SentCountDetails {
        val limit: Int = preferenceRepository.getMessageLimitValue()
        val cycleStartDate = preferenceRepository.getCycleStartDate()
        val today = Calendar.getInstance().time

        var sentTodayCount = counterRepository.getCount(getIndexFromDate(today))
        if (sentTodayCount == -1) sentTodayCount = 0

        val sentCycleCount = counterRepository.getTotalCountSince(getIndexFromDate(cycleStartDate))

        val prevCycleStartDate = getPrevCycleStartDate(cycleStartDate)
        val cycle = getCycleSentCount(prevCycleStartDate)
        val lastCycleCount = counterRepository.getTotalCountBetween(cycle.startDateIndex, cycle.endDateIndex)

        val startWeekCount = counterRepository.getTotalCountSince(getIndexFromDate(getWeekStartDate()))
        val startYearCount = counterRepository.getTotalCountSince(getIndexFromDate(getYearStartDate()))

        return SentCountDetails(limit, sentTodayCount, sentCycleCount, startWeekCount, startYearCount, lastCycleCount)
    }

    fun checkForUnloggedMessages(context: Context, messageId: String, indexAllMessages: Boolean) {
        val lastMessageTimeStamp = preferenceRepository.getLastSentTimeStamp(getStartTimeStamp(indexAllMessages))
        var newMessagesAdded = 0

        doAsync {
            // Query the SMS Database to read messages
            val newMessagesCursor = context.contentResolver.query(
                    Uri.parse(SMSManager.SMS_URI_ALL),
                    SMS_TABLE_PROJECTION,
                    "person is null and $COLUMN_NAME_DATE> ? ",
                    arrayOf(lastMessageTimeStamp), null)
            val newMessagesCount = newMessagesCursor?.count ?: 0
            val messageSentDate = Calendar.getInstance()
            try {
                if (newMessagesCount > 0 && newMessagesCursor.moveToFirst()) {
                    Log.d(TAG, "Open MessageCounterDatabase")
                    do {
                        val message = getMessageFromCursor(newMessagesCursor)

                        messageSentDate.timeInMillis = java.lang.Long.parseLong(message.date)

                        // Parse and add to message counter database.
                        // Skip the new message id which will be added by calling method
                        if (messageId != message.id && null == message.protocol) {
                            // Count this message against the date it was sent
                            val dateIndex = getIndexFromDate(messageSentDate.time)

                            //if (!ignoreNumbersManager.checkIfNumberIgnored(message.getAddress())) {
                            counterRepository.addCount(dateIndex, message.messageCount)
                            newMessagesAdded += message.messageCount
                            //}
                        }
                    } while (newMessagesCursor.moveToNext())
                }
            } catch (e: Exception) {
                Toast.makeText(context, "Exception " + e.message, Toast.LENGTH_SHORT).show()
                Log.e(TAG, e.message)
                Log.e(TAG, Log.getStackTraceString(e))
            } finally {
                newMessagesCursor?.close()
                // preferenceRepository.setHistoricMessageIndexed()
            }

            runOnIoThread {
                Toast.makeText(context, "Historical indexing ", Toast.LENGTH_SHORT).show()
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