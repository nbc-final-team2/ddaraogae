package com.nbcfinalteam2.ddaraogae.presentation.ui.walk

sealed class InsertTaskState {
    object Idle : InsertTaskState()
    object Loading : InsertTaskState()
    object Success : InsertTaskState()
    data class Error(val exception: Exception) : InsertTaskState()
}

sealed class StampTaskState {
    object Idle : StampTaskState()
    object Loading : StampTaskState()
    object Success : StampTaskState()
    data class Error(val exception: Exception) : StampTaskState()
}