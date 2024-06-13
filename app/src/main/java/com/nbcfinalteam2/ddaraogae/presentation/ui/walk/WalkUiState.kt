package com.nbcfinalteam2.ddaraogae.presentation.ui.walk

import com.nbcfinalteam2.ddaraogae.presentation.model.DogInfo
import com.nbcfinalteam2.ddaraogae.presentation.model.StoreUiModel

data class WalkUiState(
    val isWalking: Boolean,
) {
    companion object {
        fun init() = WalkUiState(
            isWalking = false,
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

data class WalkingInfoState(
    val walkingTime: Int
) {
    companion object {
        fun init() = WalkingInfoState(
            walkingTime = 0
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