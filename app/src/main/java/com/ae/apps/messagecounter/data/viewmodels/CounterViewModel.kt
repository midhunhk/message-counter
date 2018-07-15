package com.ae.apps.messagecounter.data.viewmodels

import android.arch.lifecycle.ViewModel
import android.content.SharedPreferences
import com.ae.apps.messagecounter.data.models.SentCountDetails
import com.ae.apps.messagecounter.data.repositories.*
import java.util.*

/**
 * ViewModel for holding the data for SentCountFragment
 */
class CounterViewModel(private val counterRepository: CounterRepository,
                       private val preferences: SharedPreferences) : ViewModel() {

    fun getSentCountData() : SentCountDetails{
        val limit:Int = getMessageLimitValue(preferences)
        val cycleStartDate = getCycleStartDate(preferences)
        val today = Calendar.getInstance().time

        var sentTodayCount = counterRepository.getCount(getIndexFromDate(today))
        if (sentTodayCount == -1) sentTodayCount = 0

        val sentCycleCount = counterRepository.getTotalCountSince(getIndexFromDate(cycleStartDate))

        val prevCycleStartDate = getPrevCycleStartDate(cycleStartDate)
        val cycle = getCycleSentCount(prevCycleStartDate)
        val lastCycleCount = counterRepository.getTotalCountBetween(cycle.startDateIndex, cycle.endDateIndex)

        val startWeekCount = counterRepository.getTotalCountSince(getIndexFromDate(getWeekStartDate()))

        return SentCountDetails(limit, sentTodayCount, sentCycleCount, startWeekCount, lastCycleCount)
    }
}