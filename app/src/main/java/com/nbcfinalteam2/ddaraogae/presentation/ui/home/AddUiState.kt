package com.nbcfinalteam2.ddaraogae.presentation.ui.home

import android.net.Uri

data class AddUiState(
    val imageUri: Uri? = null,
    val isThumbnailVisible: Boolean = false
) {
    companion object {
        fun init() = AddUiState(imageUri = null, isThumbnailVisible = false)
    }
}
