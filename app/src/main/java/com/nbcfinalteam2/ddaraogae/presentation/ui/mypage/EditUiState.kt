package com.nbcfinalteam2.ddaraogae.presentation.ui.mypage

import android.net.Uri

data class EditUiState(
    val imageUri: Uri? = null,
    val byteArray: ByteArray? = null,
    val isThumbnailVisible: Boolean = false,
) {
    companion object {
        fun init() = EditUiState(imageUri = null, byteArray = null,  isThumbnailVisible = false)
    }
}
