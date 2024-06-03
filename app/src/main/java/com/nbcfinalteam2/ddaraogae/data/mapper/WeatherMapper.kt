package com.nbcfinalteam2.ddaraogae.data.mapper

import com.nbcfinalteam2.ddaraogae.data.dto.Dust
import com.nbcfinalteam2.ddaraogae.data.dto.Weather
import com.nbcfinalteam2.ddaraogae.data.mapper.model.WeatherItemModel

object WeatherMapper {
    fun toWeatherData(weather: Weather, dust: Dust) : WeatherItemModel {
        return WeatherItemModel(
            id = weather.weather[0].id,
            temperature = weather.main.temp,
            pm10 = dust.list[0].components.pm10,
            pm25 = dust.list[0].components.pm25
        )
    }
}