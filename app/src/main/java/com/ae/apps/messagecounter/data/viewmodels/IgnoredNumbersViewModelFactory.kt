package com.ae.apps.messagecounter.data.viewmodels

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import com.ae.apps.messagecounter.data.repositories.IgnoredNumbersRepository

@Suppress("UNCHECKED_CAST")
class IgnoredNumbersViewModelFactory(private val repository: IgnoredNumbersRepository)
    : ViewModelProvider.NewInstanceFactory() {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return IgnoredNumbersViewModel(repository) as T
    }
}