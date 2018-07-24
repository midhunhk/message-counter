package com.ae.apps.messagecounter.data.repositories

import android.telephony.SmsMessage
import com.ae.apps.messagecounter.data.models.Cycle
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.Executors

private val IO_EXECUTOR = Executors.newSingleThreadExecutor()
private val DATE_INDEX_FORMAT = SimpleDateFormat("yyMMdd", Locale.getDefault())
private val DATE_DISPLAY_FORMAT = SimpleDateFormat("dd MMM", Locale.getDefault())

/**
 * Utility method to run blocks on a dedicated background thread, used for io/database work.
 */
fun runOnIoThread(f: () -> Unit) {
    IO_EXECUTOR.execute(f)
}

/**
 * Returns the index from the a given date
 *
 * @param date date
 * @return index
 */
fun getIndexFromDate(date: Date): String {
    return DATE_INDEX_FORMAT.format(date)
}

fun getDisplayDateString(cycleStartDate: Date): CharSequence {
    return DATE_DISPLAY_FORMAT.format(cycleStartDate)
}

/**
 * Calculates the number of messages in a multi part message based on the messageBody
 * @param messageBody the message body
 * @return message count
 */
fun getMessageCount(messageBody: String): Int {
    return SmsMessage.calculateLength(messageBody, false)[0]
}

/**
 * Returns the cycle end date. By default, cycle duration will be a month
 *
 * @param startDate start date
 * @return end date
 */
fun getCycleEndDate(startDate: Date): Date {
    val calendar = Calendar.getInstance()
    calendar.time = startDate
    calendar.add(Calendar.MONTH, +1)
    calendar.add(Calendar.DATE, -1)
    return calendar.time
}

/**
 * Returns the previous cycle start date
 *
 * @param startDate start date
 * @return previous cycle start date
 */
fun getPrevCycleStartDate(startDate: Date): Date {
    val calendar = Calendar.getInstance()
    calendar.time = startDate
    calendar.add(Calendar.MONTH, -1)
    return calendar.time
}

/**
 * Returns the start of the week
 *
 * @return
 */
fun getWeekStartDate(): Date {
    val calendar = Calendar.getInstance()
    calendar.set(Calendar.DAY_OF_WEEK, calendar.firstDayOfWeek)
    return calendar.time
}

/**
 * Returns the start of the year
 */
fun getYearStartDate(): Date {
    val calendar = Calendar.getInstance()
    calendar.set(Calendar.DAY_OF_YEAR, 1)
    return calendar.time
}

/**
 * Get count of messages sent in a cycle
 *
 * @param cycleStartDate cycle start date
 * @return
 */
fun getCycleSentCount(cycleStartDate: Date): Cycle {
    val cycleEndDate = getCycleEndDate(cycleStartDate)
    val cycleStartIndex = getIndexFromDate(cycleStartDate)
    val cycleEndIndex = getIndexFromDate(cycleEndDate)
    return Cycle(cycleStartIndex, cycleEndIndex)
}

/**
 * Returns the dates in a startDate - endDate format
 *
 * @param startDate start date
 * @return duration date string
 */
fun getDurationDateString(startDate: Date): String {
    val endDate = getCycleEndDate(startDate)
    val startDateString = getDisplayDateString(startDate)
    val endDateString = getDisplayDateString(endDate)
    return  "$startDateString - $endDateString"
}