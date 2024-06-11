package com.nbcfinalteam2.ddaraogae.presentation.ui.login

data class LoginUiState(
    val successLogin:Boolean,
    val correctEmailAccount:Boolean

    ) {
    companion object{
        fun init() = LoginUiState(
            successLogin = false,
            correctEmailAccount = true
        )
    }
}