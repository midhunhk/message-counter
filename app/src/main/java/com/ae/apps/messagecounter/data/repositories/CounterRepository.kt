package com.ae.apps.messagecounter.data.repositories

import com.ae.apps.messagecounter.data.dao.CounterDao
import com.ae.apps.messagecounter.data.models.Counter

class CounterRepository private constructor(
        private val counterDao:CounterDao
) {
    fun addCount(dateIndex: String, sentCount:Int){
        runOnIoThread {
            val counter = Counter(dateIndex, sentCount)
            counterDao.insertOrUpdate(counter)
        }
    }

    fun getCount(dateIndex: String) =
            counterDao.getCount(dateIndex)

    fun getTotalCountSince(dateIndex: String) =
            counterDao.getTotalCountSince(dateIndex)

    fun getTotalCountBetween(startIndex: String, endIndex: String) =
            counterDao.getTotalCountBetween(startIndex, endIndex)

    companion object {
        @Volatile private var instance: CounterRepository? = null
        fun getInstance(counterDao:CounterDao) =
            instance ?: synchronized(this) {
                instance ?: CounterRepository(counterDao).also { instance = it }
            }
    }
}