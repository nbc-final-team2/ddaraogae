package com.nbcfinalteam2.ddaraogae.presentation.ui.login

import android.util.Log
import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.nbcfinalteam2.ddaraogae.domain.entity.DogEntity
import com.nbcfinalteam2.ddaraogae.domain.entity.EmailAuthEntity
import com.nbcfinalteam2.ddaraogae.domain.usecase.DeleteAccountUseCase
import com.nbcfinalteam2.ddaraogae.domain.usecase.GetCurrentUserUseCase
import com.nbcfinalteam2.ddaraogae.domain.usecase.IsCurrentUserEmailVerifiedUseCase
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
    private val signInWithEmailUseCase: SignInWithEmailUseCase,
    private val isCurrentUserEmailVerifiedUseCase: IsCurrentUserEmailVerifiedUseCase,
    private val deleteAccountUseCase: DeleteAccountUseCase
) : ViewModel() {
    private val _uiState = MutableStateFlow(LoginUiState.init())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    init {
        getCurrentUser()
    }
    fun getCurrentUser() = viewModelScope.launch {
        val getCurrentUser = getCurrentUserUseCase()
        var currentUser = getCurrentUser != null

        Log.d("ginger_currentUser", "${currentUser}, ${getCurrentUser}")

        _uiState.update { prev ->
            prev.copy(
                isCurrentUser = currentUser
            )
        }
    }
    fun signInEmail(email:String, password:String) = viewModelScope.launch{
        var successSignInEmail: Boolean
        var successSignInEmailTemp : Boolean
        var checkEmailVerified : Boolean
        try{
            successSignInEmailTemp = signInWithEmailUseCase(EmailAuthEntity(email, password))
            checkEmailVerified = isCurrentUserEmailVerifiedUseCase()
            successSignInEmail = successSignInEmailTemp
            Log.d("ginger_로그인 가능 여부", "$successSignInEmail")
        }
        catch (e : Exception){ //IllegalArgumentException,FirebaseAuthInvalidCredentialsException
            successSignInEmail = false
            checkEmailVerified = false
        }
            _uiState.update { prev ->
                prev.copy(
                    // 유효하지 않은 계정일 시 관련 메세지를 띄워주기 위함.
                    correctEmailAccount = successSignInEmail,
                    successLogin = successSignInEmail,
                    verificationState = checkEmailVerified
                )
            }

    }

    fun signInGoogle(idToken: String) = viewModelScope.launch {
        val successSignInGoogle = signInWithGoogleUseCase(idToken)
        Log.d("ginger_이메일 인증", "$successSignInGoogle")
        _uiState.update { prev ->
            prev.copy(
                successLogin = successSignInGoogle
            )
        }
    }
    fun deleteAccount() = viewModelScope.launch {
        deleteAccountUseCase()
    }
}
