package com.nbcfinalteam2.ddaraogae.data.mapper

import com.nbcfinalteam2.ddaraogae.data.dto.Dust
import com.nbcfinalteam2.ddaraogae.data.dto.Weather
import com.nbcfinalteam2.ddaraogae.domain.entity.WeatherEntity

object WeatherMapper {
    fun toWeatherData(weather: Weather, dust: Dust) : WeatherEntity {
        return WeatherEntity(
            id = weather.weather?.firstOrNull()?.id ?: 0L,
            temperature = weather.main?.temp ?: 0.0,
            pm10 = dust.list?.firstOrNull()?.components?.pm10 ?: 0.0,
            pm25 = dust.list?.firstOrNull()?.components?.pm25 ?: 0.0
        )
    }
}