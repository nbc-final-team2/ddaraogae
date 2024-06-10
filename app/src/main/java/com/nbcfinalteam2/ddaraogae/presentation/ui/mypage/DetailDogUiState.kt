package com.nbcfinalteam2.ddaraogae.presentation.ui.mypage

import com.nbcfinalteam2.ddaraogae.presentation.ui.model.DogItemModel

data class DetailDogUiState(
    val listPet:List<DogItemModel>,
    val pet: DogItemModel
) {
    companion object{
        fun init() = DetailDogUiState(
            listPet = emptyList(),
            pet = DogItemModel("", "", 0)
        )
    }
}