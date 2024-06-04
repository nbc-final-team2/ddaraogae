package com.nbcfinalteam2.ddaraogae.domain.repository

import com.nbcfinalteam2.ddaraogae.data.mapper.model.WeatherItemModel

interface WeatherRepository {
    suspend fun getWeatherData(lat: String, lon: String): WeatherItemModel
}