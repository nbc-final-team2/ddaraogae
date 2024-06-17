package com.nbcfinalteam2.ddaraogae.presentation.ui.mypage

sealed class UpdateTaskState {
    data object Idle : UpdateTaskState()
    data object Loading : UpdateTaskState()
    data object Success : UpdateTaskState()
    data class Error(val exception: Throwable) : UpdateTaskState()
}