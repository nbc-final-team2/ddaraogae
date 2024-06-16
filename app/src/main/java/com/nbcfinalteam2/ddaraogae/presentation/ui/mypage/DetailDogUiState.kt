package com.nbcfinalteam2.ddaraogae.presentation.ui.mypage

import com.nbcfinalteam2.ddaraogae.presentation.model.DogInfo

data class DetailDogUiState(
    val dogList:List<DogInfo>,
    val selectedDog: DogInfo?,
    val listPetEmpty : Boolean
) {
    companion object{
        fun init() = DetailDogUiState(
            dogList = emptyList(),
            selectedDog = null,
            listPetEmpty = false
        )
    }
}