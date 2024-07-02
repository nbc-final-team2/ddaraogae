package com.nbcfinalteam2.ddaraogae.presentation.ui.home

import com.nbcfinalteam2.ddaraogae.presentation.model.DogInfo
import com.nbcfinalteam2.ddaraogae.presentation.model.WalkingInfo
import com.nbcfinalteam2.ddaraogae.presentation.model.WeatherInfo

data class HomeUiState(
    val dogList: List<DogInfo> = emptyList(),
    val selectedDog: DogInfo? = null,
    val selectedDogWithTime: Int? = null,
    val walkList: List<WalkingInfo> = emptyList(),
    val weatherInfo: WeatherInfo? = null,
    val stampProgress: Int = 0
)
