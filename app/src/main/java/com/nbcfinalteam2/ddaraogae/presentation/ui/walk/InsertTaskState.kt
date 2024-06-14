package com.nbcfinalteam2.ddaraogae.presentation.ui.walk

sealed class InsertTaskState {
    object Idle : InsertTaskState()
    object Loading : InsertTaskState()
    object Success : InsertTaskState()
    data class Error(val exception: Exception) : InsertTaskState()
}