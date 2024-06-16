package com.nbcfinalteam2.ddaraogae.presentation.shared

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SharedEventViewModel @Inject constructor() : ViewModel() {
    private val _dogRefreshEvent = MutableSharedFlow<SharedEvent>(replay = 1)
    val dogRefreshEvent: SharedFlow<SharedEvent> = _dogRefreshEvent

    fun notifyDogRefreshEvent() = viewModelScope.launch {
        _dogRefreshEvent.emit(SharedEvent.Occur)
    }
}


