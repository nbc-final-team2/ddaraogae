package com.nbcfinalteam2.ddaraogae.domain.usecase

import com.nbcfinalteam2.ddaraogae.domain.entity.WeatherEntity
import com.nbcfinalteam2.ddaraogae.domain.repository.WeatherRepository
import javax.inject.Inject

class GetWeatherDataUseCaseImpl @Inject constructor(
    private val weatherRepository: WeatherRepository
) : GetWeatherDataUseCase {
    override suspend fun invoke(lat: String, lon: String): WeatherEntity =
        weatherRepository.getWeatherData(lat, lon)

}