package com.nbcfinalteam2.ddaraogae.presentation.ui.mypage

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nbcfinalteam2.ddaraogae.domain.usecase.DeleteAccountUseCase
import com.nbcfinalteam2.ddaraogae.domain.usecase.SignOutUseCase
import com.nbcfinalteam2.ddaraogae.presentation.model.DefaultEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MyPageViewModel @Inject constructor(
    private val deleteAccountUseCase: DeleteAccountUseCase,
    private val signOutUseCase: SignOutUseCase
) : ViewModel(){

    private val _restartEvent = MutableSharedFlow<DefaultEvent>()
    val restartEvent: SharedFlow<DefaultEvent> = _restartEvent.asSharedFlow()

    fun logOut() = viewModelScope.launch{
        runCatching {
            signOutUseCase()
        }.onSuccess {
            _restartEvent.emit(DefaultEvent.Success)
        }

    }
    fun deleteUser() = viewModelScope.launch {
        runCatching {
            deleteAccountUseCase()
        }.onSuccess {
            _restartEvent.emit(DefaultEvent.Success)
        }
    }
}