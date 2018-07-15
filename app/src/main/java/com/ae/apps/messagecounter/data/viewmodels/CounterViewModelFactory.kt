package com.ae.apps.messagecounter.data.viewmodels

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import android.content.SharedPreferences
import com.ae.apps.messagecounter.data.repositories.CounterRepository

@Suppress("UNCHECKED_CAST")
class CounterViewModelFactory(private val repository: CounterRepository, private val sharedPreferences: SharedPreferences): ViewModelProvider.NewInstanceFactory() {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return CounterViewModel(repository, sharedPreferences) as T
    }
}