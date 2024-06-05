package com.nbcfinalteam2.ddaraogae.presentation.ui.mypage

import com.nbcfinalteam2.ddaraogae.data.dto.DogDto

data class DetailUiState(
    val listPet:List<DogModel>,
    val pet:DogModel
) {
    companion object{
        fun init() = DetailUiState(
            listPet = emptyList(),
            pet = DogModel()
        )
    }
}