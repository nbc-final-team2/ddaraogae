package com.nbcfinalteam2.ddaraogae.presentation.ui.login

import android.util.Log
import android.util.LogPrinter
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.nbcfinalteam2.ddaraogae.domain.entity.EmailAuthEntity
import com.nbcfinalteam2.ddaraogae.domain.usecase.DeleteAccountUseCase
import com.nbcfinalteam2.ddaraogae.domain.usecase.GetCurrentUserUseCase
import com.nbcfinalteam2.ddaraogae.domain.usecase.IsCurrentUserEmailVerifiedUseCase
import com.nbcfinalteam2.ddaraogae.domain.usecase.SendVerificationEmailUseCase
import com.nbcfinalteam2.ddaraogae.domain.usecase.SignInWithEmailUseCase
import com.nbcfinalteam2.ddaraogae.domain.usecase.SignInWithGoogleUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
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
) : ViewModel() {
    //0 : 로그인 성공 / 1: 계정 존재 / 2: 로그인 실패 / 3:인증 메일 보내기  / 99: 그 외
    private val _isPossible = MutableSharedFlow<Int>()
    val userState = _isPossible.asSharedFlow()

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

    fun deleteAccount() = viewModelScope.launch{
        try {
            deleteAccountUseCase()
        }catch (e : Exception){
            _isPossible.emit(99)
            Log.e("[signUpPage]UNKNOWN ERROR!", "$e")
        }

    }
}
