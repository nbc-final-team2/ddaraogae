package com.nbcfinalteam2.ddaraogae.presentation.ui.mypage

data class MypageUiState(
    val isLoading: Boolean
) {
    companion object {
        fun init() = MypageUiState(isLoading = false)
    }
}