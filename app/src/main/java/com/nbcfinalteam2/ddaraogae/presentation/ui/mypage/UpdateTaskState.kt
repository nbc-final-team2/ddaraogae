package com.nbcfinalteam2.ddaraogae.presentation.ui.mypage

sealed class UpdateTaskState {
    object Idle : UpdateTaskState()
    object Loading : UpdateTaskState()
    object Success : UpdateTaskState()
    data class Error(val exception: Exception) : UpdateTaskState()
}