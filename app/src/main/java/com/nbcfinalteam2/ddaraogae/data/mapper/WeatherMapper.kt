package com.nbcfinalteam2.ddaraogae.data.mapper

import android.util.Log
import com.nbcfinalteam2.ddaraogae.data.dto.CityDto
import com.nbcfinalteam2.ddaraogae.data.dto.DustDto
import com.nbcfinalteam2.ddaraogae.data.dto.WeatherDto
import com.nbcfinalteam2.ddaraogae.domain.entity.WeatherEntity

object WeatherMapper {
    fun toWeatherData(weather: WeatherDto, dust: DustDto, address: CityDto) : WeatherEntity {
        val cityData = if (address.documents[0].region2depthName.isNotEmpty()) {
            address.documents[0].region2depthName
        } else {
            address.documents[1].region2depthName
        }

        val weatherData = WeatherEntity(
            id = weather.weather[0].id,
            temperature = weather.main.temp,
            pm10 = dust.list[0].components.pm10,
            pm25 = dust.list[0].components.pm25,
            city = cityData
        )

       return weatherData
    }
}