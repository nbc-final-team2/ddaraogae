package com.nbcfinalteam2.ddaraogae.presentation.ui.walk

import com.nbcfinalteam2.ddaraogae.presentation.model.DogInfo
import com.nbcfinalteam2.ddaraogae.presentation.model.StoreUiModel

data class WalkUiState(
    val isWalking: Boolean,
    val isLoading: Boolean
) {
    companion object {
        fun init() = WalkUiState(
            isWalking = false,
            isLoading = false
        )
    }
}

data class DogSelectionState(
    val dogList: List<DogInfo>
) {
    companion object {
        fun init() = DogSelectionState(
            emptyList()
        )
    }
}

data class StoreListState(
    val storeList: List<StoreUiModel>
) {
    companion object {
        fun init() = StoreListState(
            emptyList()
        )
    }
}