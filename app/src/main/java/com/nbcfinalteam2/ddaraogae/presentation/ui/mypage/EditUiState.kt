package com.nbcfinalteam2.ddaraogae.presentation.ui.mypage

import android.net.Uri

data class EditUiState(
    val imageSource: ImageSource? = null,
    val isThumbnailVisible: Boolean = false,
    val isInit: Boolean = false,
) {
    companion object {
        fun init() = EditUiState(imageSource = null, isThumbnailVisible = false, isInit = false)
    }
}

sealed interface ImageSource {
    data class ImageUri(val value: Uri): ImageSource
    data class ImageUrl(val value: String): ImageSource
}
