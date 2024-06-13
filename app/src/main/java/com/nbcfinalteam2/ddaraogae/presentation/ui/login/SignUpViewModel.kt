package com.nbcfinalteam2.ddaraogae.presentation.ui.login

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.nbcfinalteam2.ddaraogae.domain.entity.EmailAuthEntity
import com.nbcfinalteam2.ddaraogae.domain.entity.UserEntity
import com.nbcfinalteam2.ddaraogae.domain.usecase.DeleteAccountUseCase
import com.nbcfinalteam2.ddaraogae.domain.usecase.GetCurrentUserUseCase
import com.nbcfinalteam2.ddaraogae.domain.usecase.IsCurrentUserEmailVerifiedUseCase
import com.nbcfinalteam2.ddaraogae.domain.usecase.SendVerificationEmailUseCase
import com.nbcfinalteam2.ddaraogae.domain.usecase.SignInWithEmailUseCase
import com.nbcfinalteam2.ddaraogae.domain.usecase.SignOutUseCase
import com.nbcfinalteam2.ddaraogae.domain.usecase.SignUpWithEmailUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SignUpViewModel @Inject constructor(
    private val signUpWithEmailUseCase: SignUpWithEmailUseCase,
):ViewModel(){
    //회원가입 여부 판단
    private val _userState = MutableSharedFlow<Int>()
    val userState = _userState.asSharedFlow()

    //0 : success 1: email 중복 2: 이 외 에러
    fun signUp(email:String, password:String) = viewModelScope.launch{
        try {
            signUpWithEmailUseCase(EmailAuthEntity(email, password))
            _userState.emit(0)
        } catch (e : FirebaseAuthUserCollisionException) {
            //이메일 중복 체크
            _userState.emit(1)
        } catch (e : Exception){
            _userState.emit(2)
            Log.e("[signUpPage]UNKNOWN ERROR!", "$e")
        }
    }
}