package com.ae.apps.messagecounter.data.viewmodels

import android.arch.lifecycle.ViewModel
import android.content.SharedPreferences
import com.ae.apps.messagecounter.data.models.SentCountDetails
import com.ae.apps.messagecounter.data.repositories.*
import java.util.*


class CounterViewModel(private val counterRepository: CounterRepository,
                       private val preferences: SharedPreferences) : ViewModel() {

    companion object {
        fun newInstance(counterRepository: CounterRepository,
                        preferences: SharedPreferences) = CounterViewModel(counterRepository, preferences)
    }

    fun getSentCountData() : SentCountDetails{
        var limit:Int = getMessageLimitValue(preferences)
        var cycleStartDate = getCycleStartDate(preferences)
        val today = Calendar.getInstance().time

        var sentTodayCount = counterRepository.getCount(getIndexFromDate(today))
        if (sentTodayCount == -1) {
            sentTodayCount = 0
        }

        val sentCycleCount = counterRepository.getTotalCountSince(getIndexFromDate(cycleStartDate))

        val prevCycleStartDate = getPrevCycleStartDate(cycleStartDate)
        var cycle = getCycleSentCount(prevCycleStartDate)
        var lastCycleCount = counterRepository.getTotalCountBetween(cycle.startDateIndex, cycle.endDateIndex)

        val startWeekCount = counterRepository.getTotalCountSince(getIndexFromDate(getWeekStartDate()))

        return SentCountDetails(limit, sentTodayCount, sentCycleCount, startWeekCount, lastCycleCount)
    }
}