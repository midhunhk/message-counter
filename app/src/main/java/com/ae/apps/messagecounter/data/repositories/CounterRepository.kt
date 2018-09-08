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
package com.ae.apps.messagecounter.data.repositories

import com.ae.apps.messagecounter.data.dao.CounterDao
import com.ae.apps.messagecounter.data.models.Counter

/**
 * Repository that interacts with the Message Counter Database
 */
class CounterRepository private constructor(private val counterDao:CounterDao) {

    companion object {
        @Volatile private var instance: CounterRepository? = null
        fun getInstance(counterDao:CounterDao) =
                instance ?: synchronized(this) {
                    instance ?: CounterRepository(counterDao).also { instance = it }
                }
    }

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

}