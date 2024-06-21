package com.nbcfinalteam2.ddaraogae.presentation.ui.finish

data class FinishUiState(
    val isLoading: Boolean
) {
    companion object {
        fun init() = FinishUiState(
            isLoading = false
        )
    }
}
