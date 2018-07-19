package com.ae.apps.messagecounter.com.ae.apps.messagecounter.data.dao

import android.arch.persistence.room.Room
import android.support.test.InstrumentationRegistry
import android.support.test.runner.AndroidJUnit4
import android.test.MoreAsserts
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