package com.ae.apps.messagecounter.com.ae.apps.messagecounter.data.dao

import android.arch.persistence.room.Room
import android.support.test.InstrumentationRegistry
import android.support.test.runner.AndroidJUnit4
import com.ae.apps.messagecounter.data.AppDatabase
import com.ae.apps.messagecounter.data.dao.CounterDao
import com.ae.apps.messagecounter.utils.MockCounters
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
open class CounterDaoTest {

    private lateinit var appDatabase:AppDatabase
    private lateinit var counterDao:CounterDao

    @Before
    fun setUp(){
        appDatabase = Room.inMemoryDatabaseBuilder(InstrumentationRegistry.getContext(),
                AppDatabase::class.java)
                .allowMainThreadQueries()
                .build()
        counterDao = appDatabase.counterDao()
    }

    @After
    fun tearDown(){
        appDatabase.close()
    }

    @Test
    fun insertCounterSavesData(){
        val counter = MockCounters.getSimpleCounter()
        counterDao.insert(counter)

        val actualCount = counterDao.getCount(counter.dateIndex)
        Assert.assertEquals(counter.sentCount, actualCount)
    }

    //@Test
    fun shouldIncrementCountForSameDateIndex(){
        val messageCount1 = MockCounters.getCounterWithCount(1)
        val messageCount2 = MockCounters.getCounterWithCount(3)

        counterDao.insert(messageCount1)
        counterDao.insert(messageCount2)

        val actualCount = counterDao.getCount(messageCount1.dateIndex)
        Assert.assertEquals(4, actualCount)
    }

    @Test
    fun onGetCount_shouldGetNo(){
        val emptyCount = counterDao.getCount("20180101")
        Assert.assertEquals(0, emptyCount)
    }

    //@Test
    fun onGetTotalCountSince(){
        val list = MockCounters.getCounters(2)
        val expectedCount = MockCounters.getSentCount(list)
        list.forEach {
            counterDao.insert(it)
        }

        val actualCount = counterDao.getTotalCountSince(list[0].dateIndex)
        Assert.assertEquals(expectedCount, actualCount)
    }
}
