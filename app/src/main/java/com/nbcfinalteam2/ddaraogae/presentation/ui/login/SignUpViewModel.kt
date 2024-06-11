package com.nbcfinalteam2.ddaraogae.presentation.ui.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.nbcfinalteam2.ddaraogae.domain.entity.EmailAuthEntity
import com.nbcfinalteam2.ddaraogae.domain.usecase.SignUpWithEmailUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SignUpViewModel @Inject constructor(
    private val signUpWithEmailUseCase: SignUpWithEmailUseCase
):ViewModel(){
    private val _userState = MutableSharedFlow<Boolean>(
        replay = 0, extraBufferCapacity = 1, onBufferOverflow = BufferOverflow.DROP_OLDEST
    )
    val userState = _userState.asSharedFlow()
    private val _emailState = MutableSharedFlow<Boolean>()
    val emailState = _emailState.asSharedFlow()

    fun signUp(email:String, password:String) = viewModelScope.launch{
        try {
            _userState.emit(signUpWithEmailUseCase(EmailAuthEntity(email, password)))
        } catch (e : FirebaseAuthUserCollisionException) {
            _emailState.emit(false)
        }
    }
}