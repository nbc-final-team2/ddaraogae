package com.nbcfinalteam2.ddaraogae.presentation.ui.walk

sealed class InsertTaskState {
    data object Idle : InsertTaskState()
    data object Loading : InsertTaskState()
    data object Success : InsertTaskState()
    data class Error(val exception: Exception) : InsertTaskState()
}

sealed class StampTaskState {
    data object Idle : StampTaskState()
    data object Loading : StampTaskState()
    data object Success : StampTaskState()
    data class Error(val exception: Exception) : StampTaskState()
}