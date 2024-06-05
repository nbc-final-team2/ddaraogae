package com.nbcfinalteam2.ddaraogae.domain.usecase

import com.nbcfinalteam2.ddaraogae.domain.entity.WeatherEntity

interface GetWeatherDataUseCase {
    suspend operator fun invoke(lat: String, lon: String): WeatherEntity
}