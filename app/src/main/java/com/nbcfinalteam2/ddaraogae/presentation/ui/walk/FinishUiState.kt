package com.nbcfinalteam2.ddaraogae.presentation.ui.walk

data class FinishUiState(
    val isLoading: Boolean
) {
    companion object {
        fun init() = FinishUiState(
            isLoading = false
        )
    }
}
