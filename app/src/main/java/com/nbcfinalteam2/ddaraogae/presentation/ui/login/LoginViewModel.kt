package com.nbcfinalteam2.ddaraogae.presentation.ui.login

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nbcfinalteam2.ddaraogae.domain.usecase.GetCurrentUserUseCase
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
    private val signInWithGoogleUseCase: SignInWithGoogleUseCase
) : ViewModel() {
    private val _uiState = MutableStateFlow(LoginUiState.init())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()
    fun getCurrentUser() = viewModelScope.launch {
        val getCurrentUser = getCurrentUserUseCase()
        var currentUser = if (getCurrentUser != null) true
        else false
        currentUser?.let { user ->
            _uiState.update { prev ->
                prev.copy(
                    currentUser = user
                )
            }
        }
    }

    fun signInGoogle(idToken: String) = viewModelScope.launch {
        val successSignInGoogle = signInWithGoogleUseCase(idToken)
        successSignInGoogle?.let { successToken ->
            _uiState.update { prev ->
                prev.copy(
                    successGoogleLogin = successSignInGoogle
                )
            }
        }
    }

}