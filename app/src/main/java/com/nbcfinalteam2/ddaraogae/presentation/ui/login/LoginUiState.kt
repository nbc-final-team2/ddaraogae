package com.nbcfinalteam2.ddaraogae.presentation.ui.login

data class LoginUiState(
    val successGoogleLogin:Boolean,

    ) {
    companion object{
        fun init() = LoginUiState(
            successGoogleLogin = false
        )
    }
}