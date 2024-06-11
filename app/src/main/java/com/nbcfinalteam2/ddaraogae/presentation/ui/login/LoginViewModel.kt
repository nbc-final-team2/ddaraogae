package com.nbcfinalteam2.ddaraogae.presentation.ui.login

import android.util.Log
import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.nbcfinalteam2.ddaraogae.domain.entity.DogEntity
import com.nbcfinalteam2.ddaraogae.domain.entity.EmailAuthEntity
import com.nbcfinalteam2.ddaraogae.domain.usecase.GetCurrentUserUseCase
import com.nbcfinalteam2.ddaraogae.domain.usecase.SignInWithEmailUseCase
import com.nbcfinalteam2.ddaraogae.domain.usecase.SignInWithGoogleUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val getCurrentUserUseCase: GetCurrentUserUseCase,
    private val signInWithGoogleUseCase: SignInWithGoogleUseCase,
    private val signInWithEmailUseCase: SignInWithEmailUseCase
) : ViewModel() {
    private val _uiState = MutableStateFlow(LoginUiState.init())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            val getCurrentUser = getCurrentUserUseCase()
            var currentUser = if (getCurrentUser != null) true
            else false
            _uiState.update { prev ->
                prev.copy(
                    successLogin = currentUser
                )
            }

        }
    }
    fun signInEmail(email:String, password:String) = viewModelScope.launch{
        var successSignInEmail = false
        try{
            successSignInEmail = signInWithEmailUseCase(EmailAuthEntity(email, password))
        } catch (e : FirebaseAuthInvalidCredentialsException) {
            successSignInEmail = false
        }
        _uiState.update { prev ->
            prev.copy(
                successLogin = successSignInEmail,
                // 유효하지 않은 계정일 시 관련 메세지를 띄워주기 위함.
                correctEmailAccount = successSignInEmail
            )
        }
    }

    fun signInGoogle(idToken: String) = viewModelScope.launch {
        val successSignInGoogle = signInWithGoogleUseCase(idToken)
        _uiState.update { prev ->
            prev.copy(
                successLogin = successSignInGoogle
            )
        }
    }
}
