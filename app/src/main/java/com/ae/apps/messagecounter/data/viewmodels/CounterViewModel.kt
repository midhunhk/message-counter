package com.ae.apps.messagecounter.data.viewmodels

import android.arch.lifecycle.ViewModel
import android.content.SharedPreferences
import com.ae.apps.messagecounter.data.repositories.*
import java.util.*


class CounterViewModel(private val counterRepository: CounterRepository,
                       private val preferences: SharedPreferences) : ViewModel() {

    companion object {
        fun newInstance(counterRepository: CounterRepository,
                        preferences: SharedPreferences) = CounterViewModel(counterRepository, preferences)
    }

    fun getSentCountData(){
        var limit:Int = getMessageLimitValue(preferences)
        var cycleStartDate = getCycleStartDate(preferences)
        val today = Calendar.getInstance().time

        val sentTodayCount = counterRepository.getCount(getIndexFromDate(today))

        val sentCycleCount = counterRepository.getTotalCountSince(getIndexFromDate(cycleStartDate))

        val prevCycleStartDate = getPrevCycleStartDate(cycleStartDate)
        var cycle = getCycleSentCount(prevCycleStartDate)
        var lastCycleCount = counterRepository.getTotalCountBetween(cycle.startDateIndex, cycle.endDateIndex)

                .knjuygt6r5r5rt67u88uy7y666666666666666666666666666666666666666666666666666666666666
    }
}