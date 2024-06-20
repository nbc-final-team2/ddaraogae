package com.nbcfinalteam2.ddaraogae.presentation.ui.mypage

data class DetailUiState(
    val isLoading: Boolean
) {
    companion object {
        fun init() = DetailUiState(isLoading = false)
    }
}
