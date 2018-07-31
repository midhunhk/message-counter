package com.ae.apps.messagecounter.com.ae.apps.messagecounter.data.preferences

import android.content.SharedPreferences
import android.support.test.runner.AndroidJUnit4
import com.ae.apps.messagecounter.data.preferences.*
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito

@RunWith(AndroidJUnit4::class)
class PreferenceRepositoryTest {

    val sharedPreferences:SharedPreferences = Mockito.mock(SharedPreferences::class.java)

    var preferenceRepository: PreferenceRepository = PreferenceRepository.newInstance(sharedPreferences)

    val MESSAGE_LIMIT_VALUE = 100

    @Before
    fun init() {
        Mockito.`when`(sharedPreferences.getString(PREF_KEY_MESSAGE_LIMIT_VALUE, DEFAULT_MESSAGE_LIMIT.toString()))
                .thenReturn(MESSAGE_LIMIT_VALUE.toString())
        Mockito.`when`(sharedPreferences.getString(PREF_KEY_CYCLE_START_DATE, DEFAULT_CYCLE_START_DATE))
                .thenReturn(DEFAULT_CYCLE_START_DATE)
    }

    @Test
    fun getMessageLimitValueTest() {
        val result = preferenceRepository.getMessageLimitValue()
        Assert.assertEquals(MESSAGE_LIMIT_VALUE, result)
    }

}