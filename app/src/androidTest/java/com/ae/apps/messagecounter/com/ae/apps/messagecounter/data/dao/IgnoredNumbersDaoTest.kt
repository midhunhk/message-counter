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
package com.ae.apps.messagecounter.com.ae.apps.messagecounter.data.dao

import android.arch.persistence.room.Room
import android.support.test.InstrumentationRegistry
import android.support.test.runner.AndroidJUnit4
import com.ae.apps.messagecounter.data.AppDatabase
import com.ae.apps.messagecounter.data.dao.IgnoredNumbersDao
import com.ae.apps.messagecounter.utils.MockIgnoredNumbers
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
open class IgnoredNumbersDaoTest {

    private lateinit var appDatabase: AppDatabase
    private lateinit var ignoredNumbersDao: IgnoredNumbersDao

    @Before
    fun setUp() {
        appDatabase = Room.inMemoryDatabaseBuilder(InstrumentationRegistry.getContext(),
                AppDatabase::class.java)
                .allowMainThreadQueries()
                .build()
        ignoredNumbersDao = appDatabase.ignoredNumbersDao()
    }

    @After
    fun tearDown() {
        appDatabase.close()
    }

    @Test
    fun test_insertIgnoreNumber(){
        val ignoredNumber = MockIgnoredNumbers.getIgnoredNumber()
        ignoredNumbersDao.insertIgnoreNumber(ignoredNumber)
        val actualResult = ignoredNumbersDao.checkIfNumberIsIgnored(ignoredNumber.ignoreNumber)
        Assert.assertEquals(1, actualResult)
    }

    @Test
    fun test_checkIfNumberIsIgnored_shouldReturnFalseWhenNumberNotIgnored(){
        val actualResult = ignoredNumbersDao.checkIfNumberIsIgnored("+12345667")
        Assert.assertNotEquals(1, actualResult)
    }

    @Test
    fun test_getAllIgnoredNumbers(){
        val list = MockIgnoredNumbers.getIgnoredNumbers(5)
        list.forEach {
            ignoredNumbersDao.insertIgnoreNumber(it)
        }
        val allIgnoredNumbers = ignoredNumbersDao.getAllIgnoredNumbers()
        Assert.assertNotNull(allIgnoredNumbers)
        Assert.assertEquals(5, allIgnoredNumbers.size)
    }

}
