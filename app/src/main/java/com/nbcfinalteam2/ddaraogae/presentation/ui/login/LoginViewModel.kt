package com.nbcfinalteam2.ddaraogae.presentation.ui.login

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nbcfinalteam2.ddaraogae.domain.entity.EmailAuthEntity
import com.nbcfinalteam2.ddaraogae.domain.usecase.DeleteAccountUseCase
import com.nbcfinalteam2.ddaraogae.domain.usecase.GetCurrentUserUseCase
import com.nbcfinalteam2.ddaraogae.domain.usecase.IsCurrentUserEmailVerifiedUseCase
import com.nbcfinalteam2.ddaraogae.domain.usecase.SendVerificationEmailUseCase
import com.nbcfinalteam2.ddaraogae.domain.usecase.SignInWithEmailUseCase
import com.nbcfinalteam2.ddaraogae.domain.usecase.SignInWithGoogleUseCase
import com.nbcfinalteam2.ddaraogae.domain.usecase.SignOutUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val getCurrentUserUseCase: GetCurrentUserUseCase,
    private val signInWithGoogleUseCase: SignInWithGoogleUseCase,
    private val signInWithEmailUseCase: SignInWithEmailUseCase,
    private val isCurrentUserEmailVerifiedUseCase: IsCurrentUserEmailVerifiedUseCase,
    private val sendVerificationEmailUseCase: SendVerificationEmailUseCase,
    private val deleteAccountUseCase: DeleteAccountUseCase,
    private val signOutUseCase: SignOutUseCase,
) : ViewModel() {
    //0 : 로그인 성공 / 1: 계정 존재 / 2: 로그인 실패 / 3:인증 메일 보내기  / 99: 그 외
    private val _isPossible = MutableSharedFlow<Int>(replay = 1)
    val userState = _isPossible.asSharedFlow()


    //스플래시 화면 관련
    private val _isLoading: MutableStateFlow<Boolean> = MutableStateFlow(true)
    val isLoading : StateFlow<Boolean> get() = _isLoading

    init {
        viewModelScope.launch {
            delay(1000)
            _isLoading.value = false
        }
    }

    fun getCurrentUser() = viewModelScope.launch {
        val getCurrentUser = getCurrentUserUseCase()
        if(getCurrentUser != null) {_isPossible.emit(0)}
    }

    fun signInEmail(email:String, password:String) = viewModelScope.launch{
        try{
            val isSuccess = signInWithEmailUseCase(EmailAuthEntity(email, password))
            if(isSuccess)_isPossible.emit(1)
            else _isPossible.emit(2)

        }
        catch (e : Exception){
            _isPossible.emit(99)
            Log.e("[signUpPage]UNKNOWN ERROR!", "$e")
        }
    }
    fun checkVerified() = viewModelScope.launch {
        try {
            val isVerified = isCurrentUserEmailVerifiedUseCase()
            if(isVerified) _isPossible.emit(0)
            else _isPossible.emit(3)

        }catch (e:Exception){
            Log.e("[signUpPage]UNKNOWN ERROR!", "$e")
        }
    }

    fun sendEmail() = viewModelScope.launch {
        try {
            sendVerificationEmailUseCase()
            signOut()
        }catch (e:Exception){
            _isPossible.emit(99)
            Log.e("[signUpPage]UNKNOWN ERROR!", "$e")
        }
    }

    fun signInGoogle(idToken: String) = viewModelScope.launch {
        try {
            val isSuccess = signInWithGoogleUseCase(idToken)
            if(isSuccess)_isPossible.emit(0)
            else _isPossible.emit(2)
        } catch (e : Exception){
            _isPossible.emit(99)
            Log.e("[signUpPage]UNKNOWN ERROR!", "$e")
        }
    }

    private fun signOut() = viewModelScope.launch{
        signOutUseCase()
    }

    fun deleteAccount() = viewModelScope.launch{
        try {
            deleteAccountUseCase()
        }catch (e : Exception){
            _isPossible.emit(99)
            Log.e("[signUpPage]UNKNOWN ERROR!", "$e")
        }

    }
}
