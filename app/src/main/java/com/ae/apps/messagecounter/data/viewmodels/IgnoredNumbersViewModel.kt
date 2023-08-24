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

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.ae.apps.messagecounter.data.models.IgnoredNumber
import com.ae.apps.messagecounter.data.repositories.IgnoredNumbersRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.jetbrains.anko.doAsync

class IgnoredNumbersViewModel(private val repository: IgnoredNumbersRepository) : ViewModel() {

    private var ignoredNumbers: MutableLiveData<List<IgnoredNumber>> = MutableLiveData()

    fun getIgnoredNumbers() = ignoredNumbers

    init {
        doAsync{
            readAllIgnoredNumbers()
        }
    }

    private fun readAllIgnoredNumbers(){
        getIgnoredNumbers().postValue(repository.getAllIgnoredNumbers())
    }

    fun ignoreNumber(ignoredContact: IgnoredNumber) {
        doAsync{
            if (!repository.checkIfNumberIsIgnored(ignoredContact.ignoreNumber)) {
                repository.ignoreNumber(ignoredContact)
                readAllIgnoredNumbers()
            }
        }
    }

    fun unIgnoreNumber(ignoredNumber:IgnoredNumber){
        doAsync {
            repository.unIgnoreNumber(ignoredNumber)
            readAllIgnoredNumbers()
        }
    }

}