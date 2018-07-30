package com.ae.apps.messagecounter.data.viewmodels

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import android.content.Context
import com.ae.apps.messagecounter.data.business.MessageCounter
import com.ae.apps.messagecounter.data.models.SentCountDetails
import com.ae.apps.messagecounter.data.preferences.PreferenceRepository
import com.ae.apps.messagecounter.data.repositories.*
import org.jetbrains.anko.doAsync
import java.util.*

/**
 * ViewModel for holding the data for SentCountFragment
 */
class CounterViewModel(private val counterRepository: CounterRepository,
                       private val ignoreNumbersRepository: IgnoredNumbersRepository,
                       private val preferenceRepository: PreferenceRepository) : ViewModel(), MessageCounter.MessageCounterObserver {

    private var mSentCountDetails: MutableLiveData<SentCountDetails> = MutableLiveData()

    fun getSentCountDetails(): LiveData<SentCountDetails> = mSentCountDetails

    fun readSentCountDataFromRepository() {
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
                    getDurationDateString(cycleStartDate),
                    getDurationDateString(prevCycleStartDate))
            mSentCountDetails.postValue(sentCountDetails)
        }
    }

    /**
     * Index messages that are in the sms database that have not been counted before
     *
     * @param context The context used for accessing the SMS API
     */
    fun indexMessages(context: Context) {
        val messageCounter = MessageCounter.newInstance(counterRepository, ignoreNumbersRepository, preferenceRepository)
        messageCounter.indexMessages(context, this)
    }

    override fun onIndexCompleted() {
        readSentCountDataFromRepository()
    }

}