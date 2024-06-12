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
    private val signInWithEmailUseCase: SignInWithEmailUseCase,
    private val sendVerificationEmailUseCase: SendVerificationEmailUseCase,
    private val isCurrentUserEmailVerifiedUseCase: IsCurrentUserEmailVerifiedUseCase,
    private val signOutUseCase: SignOutUseCase,
    private val deleteAccountUseCase: DeleteAccountUseCase,
    private val getCurrentUserUseCase: GetCurrentUserUseCase,
):ViewModel(){
    //회원가입 여부 판단
    private val _userState = MutableSharedFlow<Boolean>(
        replay = 0, extraBufferCapacity = 1, onBufferOverflow = BufferOverflow.DROP_OLDEST
    )
    val userState = _userState.asSharedFlow()

    //이메일 중복 여부 판단
    private val _emailState = MutableSharedFlow<Boolean>()
    val emailState = _emailState.asSharedFlow()

    //인증여부 판단
    private val _verificationState = MutableSharedFlow<Boolean>()
    val verificationState = _verificationState.asSharedFlow()

    private val _currentUserState = MutableSharedFlow<Boolean>()
    val currentUserState = _currentUserState.asSharedFlow()

    private fun getCurrentUser() = viewModelScope.launch {
        val getCurrentUser = getCurrentUserUseCase()
        var currentUser = getCurrentUser != null

        Log.d("ginger_회원가입페이지", "${currentUser}, ${getCurrentUser}")
        _currentUserState.emit(currentUser)
    }
    fun signUp(email:String, password:String) = viewModelScope.launch{
        try {
            signUpWithEmailUseCase(EmailAuthEntity(email, password))
            signIn(email, password)
        } catch (e : FirebaseAuthUserCollisionException) {
            //이메일 중복 체크
            _emailState.emit(false)
        }
    }
    private fun signIn(email:String, password:String) = viewModelScope.launch {
        signInWithEmailUseCase(EmailAuthEntity(email, password))
        Log.d("ginger_로그인", "로그인!")
        sendVerification()
    }
    private fun sendVerification() = viewModelScope.launch{
        sendVerificationEmailUseCase()
        getCurrentUser()
    }

    fun isCurrentUserEmailVerified() = viewModelScope.launch{
        val isUserVerified = isCurrentUserEmailVerifiedUseCase()
        _verificationState.emit(isUserVerified)
        _userState.emit(isUserVerified)
    }
    fun deleteAccount() = viewModelScope.launch {
        deleteAccountUseCase()
    }
    fun signOut() = viewModelScope.launch{
        signOutUseCase()
        Log.d("ginger_로그아웃", "로그아웃!")
    }
}