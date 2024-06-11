package com.nbcfinalteam2.ddaraogae.data.mapper

import com.nbcfinalteam2.ddaraogae.data.dto.CityDto
import com.nbcfinalteam2.ddaraogae.data.dto.DustDto
import com.nbcfinalteam2.ddaraogae.data.dto.WeatherDto
import com.nbcfinalteam2.ddaraogae.domain.entity.WeatherEntity

object WeatherMapper {
    fun toWeatherData(weather: WeatherDto, dust: DustDto, address: CityDto) : WeatherEntity {
        val cityData = address.documents?.let { docs ->
            if (docs.isNotEmpty() && docs[0].region2depthName.isNullOrEmpty()) {
                docs.getOrNull(1)?.region2depthName
            } else {
                docs.getOrNull(0)?.region2depthName
            }
        }

        val weatherData = WeatherEntity(
            id = weather.weather?.get(0)?.id,
            temperature = weather.main?.temp,
            pm10 = dust.list?.get(0)?.components?.pm10,
            pm25 = dust.list?.get(0)?.components?.pm25,
            city = cityData
        )

       return weatherData
    }
}