package com.nbcfinalteam2.ddaraogae.domain.usecase

import com.nbcfinalteam2.ddaraogae.domain.entity.WeatherEntity
import com.nbcfinalteam2.ddaraogae.domain.repository.WeatherRepository

class GetWeatherDataUseCaseImpl(
    private val weatherRepository: WeatherRepository
) : GetWeatherDataUseCase {
    override suspend fun invoke(lat: String, lon: String): WeatherEntity =
        weatherRepository.getWeatherData(lat, lon)

}