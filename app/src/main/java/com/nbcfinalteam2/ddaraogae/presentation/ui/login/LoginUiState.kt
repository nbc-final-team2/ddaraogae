package com.nbcfinalteam2.ddaraogae.presentation.ui.login

data class LoginUiState(
    val currentUser:Boolean,
    val successGoogleLogin:Boolean,

) {
    companion object{
        fun init() = LoginUiState(
            currentUser = false,
            successGoogleLogin = true
        )
    }
}