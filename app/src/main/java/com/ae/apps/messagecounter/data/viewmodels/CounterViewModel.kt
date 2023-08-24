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
package com.ae.apps.messagecounter.data.viewmodels

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.ae.apps.messagecounter.data.business.MessageCounter
import com.ae.apps.messagecounter.data.models.SentCountDetails
import com.ae.apps.messagecounter.data.preferences.PreferenceRepository
import com.ae.apps.messagecounter.data.repositories.*
import kotlinx.coroutines.DelicateCoroutinesApi
import org.jetbrains.anko.doAsync
import java.util.*

/**
 * ViewModel for holding the data for SentCountFragment
 */
class CounterViewModel(
    private val counterRepository: CounterRepository,
    private val ignoreNumbersRepository: IgnoredNumbersRepository,
    private val preferenceRepository: PreferenceRepository
) : ViewModel(), MessageCounter.MessageCounterObserver {

    private var sentCountDetails1: MutableLiveData<SentCountDetails> = MutableLiveData()

    fun getSentCountDetails(): LiveData<SentCountDetails> = sentCountDetails1

    @OptIn(DelicateCoroutinesApi::class)
    fun readSentCountDataFromRepository() {
        val limit: Int = preferenceRepository.getMessageLimitValue()
        val cycleStartDate = preferenceRepository.getCycleStartDate()
        val today = Calendar.getInstance().time

        doAsync {
            val prevCycleStartDate = getPrevCycleStartDate(cycleStartDate)
            val prevCycle = getCycleSentCount(prevCycleStartDate)
            var sentTodayCount = counterRepository.getCount(getIndexFromDate(today))
            if (sentTodayCount == -1) sentTodayCount = 0
            val sentCycleCount =
                counterRepository.getTotalCountSince(getIndexFromDate(cycleStartDate))
            val lastCycleCount = counterRepository.getTotalCountBetween(
                prevCycle.startDateIndex,
                prevCycle.endDateIndex
            )
            val startWeekCount =
                counterRepository.getTotalCountSince(getIndexFromDate(getWeekStartDate()))
            val startYearCount =
                counterRepository.getTotalCountSince(getIndexFromDate(getYearStartDate()))

            val sentCountDetails = SentCountDetails(
                limit, sentTodayCount, sentCycleCount,
                startWeekCount, startYearCount, lastCycleCount,
                getDurationDateString(cycleStartDate),
                getDurationDateString(prevCycleStartDate)
            )
            sentCountDetails1.postValue(sentCountDetails)
        }
    }

    /**
     * Index messages that are in the sms database that have not been counted before
     *
     * @param context The context used for accessing the SMS API
     */
    fun indexMessages(context: Context) {
        val messageCounter = MessageCounter.newInstance(
            counterRepository,
            ignoreNumbersRepository,
            preferenceRepository
        )
        messageCounter.indexMessages(context, this)
    }

    override fun onIndexCompleted() {
        readSentCountDataFromRepository()
    }

}