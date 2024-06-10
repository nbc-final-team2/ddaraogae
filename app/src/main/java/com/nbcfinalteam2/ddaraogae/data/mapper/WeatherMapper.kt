package com.nbcfinalteam2.ddaraogae.data.mapper

import com.nbcfinalteam2.ddaraogae.data.dto.CityDto
import com.nbcfinalteam2.ddaraogae.data.dto.DustDto
import com.nbcfinalteam2.ddaraogae.data.dto.WeatherDto
import com.nbcfinalteam2.ddaraogae.domain.entity.WeatherEntity

object WeatherMapper {
    fun toWeatherData(weather: WeatherDto, dust: DustDto, address: CityDto) : WeatherEntity {
        var addressData = address.documents?.get(0)
        if (addressData == null) { addressData = address.documents?.get(1) }
        val cityData = addressData?.region3depthName ?: addressData?.region2depthName ?: addressData?.region1depthName

        val weatherData = WeatherEntity(
            id = weather.weather?.firstOrNull()?.id,
            temperature = weather.main?.temp,
            pm10 = dust.list?.firstOrNull()?.components?.pm10,
            pm25 = dust.list?.firstOrNull()?.components?.pm25,
            city = cityData
        )

       return weatherData
    }
}