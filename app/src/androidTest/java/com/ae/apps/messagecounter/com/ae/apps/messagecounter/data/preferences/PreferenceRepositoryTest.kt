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