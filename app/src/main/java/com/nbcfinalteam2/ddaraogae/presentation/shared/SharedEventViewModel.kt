package com.nbcfinalteam2.ddaraogae.presentation.shared

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

class SharedEventViewModel(): ViewModel() {

    private val _sharedEvent = MutableSharedFlow<SharedEvent>()
    private val sharedEvent = _sharedEvent.asSharedFlow()

    fun notifyDogListChanged() = viewModelScope.launch {
        _sharedEvent.emit(SharedEvent.DogRefreshment)
    }

}