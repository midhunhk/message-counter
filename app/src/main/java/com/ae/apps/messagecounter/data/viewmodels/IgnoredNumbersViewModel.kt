package com.ae.apps.messagecounter.data.viewmodels

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import com.ae.apps.messagecounter.data.models.IgnoredNumber
import com.ae.apps.messagecounter.data.repositories.IgnoredNumbersRepository
import org.jetbrains.anko.doAsync

class IgnoredNumbersViewModel(private val repository: IgnoredNumbersRepository) : ViewModel() {

    private var mIgnoredNumbers: MutableLiveData<List<IgnoredNumber>> = MutableLiveData()

    fun getIgnoredNumbers() = mIgnoredNumbers

    init {
        doAsync {
            mIgnoredNumbers.postValue(repository.getAllIgnoredNumbers())
        }
    }

    fun ignoreNumber(number: String, label: String) {
        doAsync {
            if (!repository.checkIfNumberIsIgnored(number)) {
                repository.ignoreNumber(IgnoredNumber(label, number))
            }
        }
    }

    fun unIgnoreNumber(ignoredNumber:IgnoredNumber){
        doAsync {
            repository.unIgnoreNumber(ignoredNumber)
        }
    }

}