package com.nbcfinalteam2.ddaraogae.presentation.ui.mypage

import com.nbcfinalteam2.ddaraogae.presentation.model.DogInfo

data class DetailDogUiState(
    val listPet:List<DogInfo>,
    val pet: DogInfo,
    val listPetEmpty : Boolean
) {
    companion object{
        fun init() = DetailDogUiState(
            listPet = emptyList(),
            pet = DogInfo("", "", 0),
            listPetEmpty = false
        )
    }
}