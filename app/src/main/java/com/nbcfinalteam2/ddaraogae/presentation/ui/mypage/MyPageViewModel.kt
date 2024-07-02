package com.nbcfinalteam2.ddaraogae.presentation.ui.mypage

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nbcfinalteam2.ddaraogae.R
import com.nbcfinalteam2.ddaraogae.domain.usecase.DeleteAccountUseCase
import com.nbcfinalteam2.ddaraogae.domain.usecase.IsGoogleUserUseCase
import com.nbcfinalteam2.ddaraogae.domain.usecase.SignOutUseCase
import com.nbcfinalteam2.ddaraogae.presentation.model.DefaultEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MyPageViewModel @Inject constructor(
    private val deleteAccountUseCase: DeleteAccountUseCase,
    private val signOutUseCase: SignOutUseCase,
    private val isGoogleUserUseCase: IsGoogleUserUseCase
) : ViewModel(){

    private val _restartEvent = MutableSharedFlow<DefaultEvent>()
    val restartEvent: SharedFlow<DefaultEvent> = _restartEvent.asSharedFlow()

    private val _mypageEvent = MutableSharedFlow<DefaultEvent>()
    val mypageEvent: SharedFlow<DefaultEvent> = _mypageEvent.asSharedFlow()

    private val _mypageUiState = MutableStateFlow(MypageUiState.init())
    val mypageUiState: StateFlow<MypageUiState> = _mypageUiState.asStateFlow()

    private val _isGoogleLogin = MutableSharedFlow<Boolean>()
    val isGoogleLogin:SharedFlow<Boolean> = _isGoogleLogin.asSharedFlow()

    fun logOut() = viewModelScope.launch{
        _mypageUiState.update {
            it.copy(isLoading = true)
        }
        runCatching {
            signOutUseCase()
        }.onSuccess {
            _mypageEvent.emit(DefaultEvent.Success)
            _mypageUiState.update {
                it.copy(isLoading = false)
            }
            _restartEvent.emit(DefaultEvent.Success)
        }.onFailure {
            _mypageEvent.emit(DefaultEvent.Failure(R.string.msg_sign_out_fail))
            _mypageUiState.update {
                it.copy(isLoading = false)
            }
        }

    }
    fun isGoogleUser() = viewModelScope.launch{
        var isGoogleUser = false
        runCatching {
            isGoogleUser = isGoogleUserUseCase()
        }.onSuccess {
            _mypageEvent.emit(DefaultEvent.Success)
            _isGoogleLogin.emit(isGoogleUser)
        }.onFailure {
            _mypageEvent.emit(DefaultEvent.Failure(R.string.msg_delete_user_fail))
        }
    }
    fun deleteUser(credential : String) = viewModelScope.launch {
        _mypageUiState.update {
            it.copy(isLoading = true)
        }
        runCatching {
            deleteAccountUseCase(credential)
        }.onSuccess {
            _mypageEvent.emit(DefaultEvent.Success)
            _mypageUiState.update {
                it.copy(isLoading = false)
            }
            _restartEvent.emit(DefaultEvent.Success)
        }.onFailure {
            _mypageEvent.emit(DefaultEvent.Failure(R.string.msg_delete_user_fail))
            _mypageUiState.update {
                it.copy(isLoading = false)
            }
        }
    }
}