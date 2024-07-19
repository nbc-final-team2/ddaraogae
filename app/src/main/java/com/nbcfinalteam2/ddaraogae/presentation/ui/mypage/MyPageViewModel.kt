package com.nbcfinalteam2.ddaraogae.presentation.ui.mypage

import android.net.ConnectivityManager
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.nbcfinalteam2.ddaraogae.R
import com.nbcfinalteam2.ddaraogae.domain.usecase.DeleteAccountUseCase
import com.nbcfinalteam2.ddaraogae.domain.usecase.GetCurrentUserUseCase
import com.nbcfinalteam2.ddaraogae.domain.usecase.IsGoogleUserUseCase
import com.nbcfinalteam2.ddaraogae.domain.usecase.SignInWithGoogleUseCase
import com.nbcfinalteam2.ddaraogae.domain.usecase.SignOutUseCase
import com.nbcfinalteam2.ddaraogae.presentation.alarm_core.AlarmController
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
import java.io.IOException
import javax.inject.Inject

@HiltViewModel
class MyPageViewModel @Inject constructor(
    private val deleteAccountUseCase: DeleteAccountUseCase,
    private val signOutUseCase: SignOutUseCase,
    private val isGoogleUserUseCase: IsGoogleUserUseCase,
    private val signInWithGoogleUseCase: SignInWithGoogleUseCase,
    private val connectivityManager: ConnectivityManager,
    private val alarmController: AlarmController,
    private val getCurrentUserUseCase: GetCurrentUserUseCase
) : ViewModel(){

    private val _restartEvent = MutableSharedFlow<DefaultEvent>()
    val restartEvent: SharedFlow<DefaultEvent> = _restartEvent.asSharedFlow()

    private val _mypageEvent = MutableSharedFlow<DefaultEvent>()
    val mypageEvent: SharedFlow<DefaultEvent> = _mypageEvent.asSharedFlow()

    private val _mypageUiState = MutableStateFlow(MypageUiState.init())
    val mypageUiState: StateFlow<MypageUiState> = _mypageUiState.asStateFlow()

    private val _isGoogleLogin = MutableSharedFlow<Boolean>()
    val isGoogleLogin:SharedFlow<Boolean> = _isGoogleLogin.asSharedFlow()

    private val _isGoogleLoginSuccess = MutableSharedFlow<Boolean>()
    val isGoogleLoginSuccess:SharedFlow<Boolean> = _isGoogleLoginSuccess.asSharedFlow()

    fun logOut() = viewModelScope.launch{
        _mypageUiState.update {
            it.copy(isLoading = true)
        }
        runCatching {
            alarmController.unsetAllAlarms(getCurrentUserUseCase()?.uid!!)
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
        if(!_mypageUiState.value.isLoading){
            _mypageUiState.update {
            it.copy(isLoading = true)
            }
        }
        runCatching {
            val uid = getCurrentUserUseCase()?.uid!!
            deleteAccountUseCase(credential)
            alarmController.deleteAllAlarm(uid)
        }.onSuccess {
            _mypageEvent.emit(DefaultEvent.Success)
            _mypageUiState.update {
                it.copy(isLoading = false)
            }
            _restartEvent.emit(DefaultEvent.Success)
        }.onFailure {e ->
            if(e is FirebaseAuthInvalidCredentialsException) _mypageEvent.emit(DefaultEvent.Failure(R.string.mypage_check_password))
            else _mypageEvent.emit(DefaultEvent.Failure(R.string.msg_delete_user_fail))
            _mypageUiState.update {
                it.copy(isLoading = false)
            }
        }
    }
    fun signInGoogle(idToken: String) = viewModelScope.launch {
        try {
            val isSuccess = signInWithGoogleUseCase(idToken)
            _isGoogleLoginSuccess.emit(isSuccess)

        } catch (e: IOException){
            Log.e("[signUpPage]IOException!", "$e")
            _mypageUiState.update {
                it.copy(isLoading = false)
            }
        }catch (e : Exception){
            Log.e("[signUpPage]UNKNOWN ERROR!", "$e")
            _mypageUiState.update {
                it.copy(isLoading = false)
            }
        }
    }
    fun googleLogOut() = viewModelScope.launch{
        _mypageUiState.update {
            it.copy(isLoading = true)
        }
        if(connectivityManager.activeNetwork == null) {
            _mypageUiState.update {
                _mypageEvent.emit(DefaultEvent.Failure(R.string.login_ioexception))
                it.copy(isLoading = false)
            }
        }
        else {
            runCatching {
                signOutUseCase()
            }.onSuccess {
                _mypageEvent.emit(DefaultEvent.Success)
            }.onFailure {
                _mypageEvent.emit(DefaultEvent.Failure(R.string.msg_delete_user_fail))
                _mypageUiState.update {
                    it.copy(isLoading = false)
                }
            }
        }
    }
}