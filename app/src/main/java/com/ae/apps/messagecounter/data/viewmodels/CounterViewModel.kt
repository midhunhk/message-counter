package com.ae.apps.messagecounter.data.viewmodels

import android.arch.lifecycle.ViewModel
import android.content.SharedPreferences
import com.ae.apps.messagecounter.data.repositories.CounterRepository

class CounterViewModel internal constructor(counterRepository: CounterRepository,
                                            preferences: SharedPreferences):
        ViewModel()  {

    companion object {
        fun newInstance(counterRepository: CounterRepository,
                        preferences: SharedPreferences) = CounterViewModel(counterRepository, preferences)
    }

    fun getSentCountData(){

    }
}