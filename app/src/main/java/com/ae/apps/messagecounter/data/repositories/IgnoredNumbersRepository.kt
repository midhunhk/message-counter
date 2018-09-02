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

import com.ae.apps.messagecounter.data.dao.IgnoredNumbersDao
import com.ae.apps.messagecounter.data.models.IgnoredNumber

class IgnoredNumbersRepository private constructor(private val dao: IgnoredNumbersDao) {

    companion object {
        @Volatile
        private var instance: IgnoredNumbersRepository? = null

        fun getInstance(dao: IgnoredNumbersDao) =
                instance ?: synchronized(this) {
                    instance ?: IgnoredNumbersRepository(dao).also { instance = it }
                }
    }

    fun getAllIgnoredNumbers() = dao.getAllIgnoredNumbers()

    fun checkIfNumberIsIgnored(numberToCheck: String): Boolean {
        return dao.checkIfNumberIsIgnored(numberToCheck) == 1
    }

    fun checkIfNumberIsNotIgnored(numberToCheck:String): Boolean{
        return !checkIfNumberIsIgnored(numberToCheck)
    }

    fun ignoreNumber(numberToIgnore:IgnoredNumber) {
        dao.insertIgnoreNumber(numberToIgnore)
    }

    fun unIgnoreNumber(ignoredNumber:IgnoredNumber) = dao.deleteIgnoredNumber(ignoredNumber)
}