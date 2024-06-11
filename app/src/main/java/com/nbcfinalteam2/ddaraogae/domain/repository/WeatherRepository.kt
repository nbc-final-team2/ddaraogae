package com.nbcfinalteam2.ddaraogae.domain.repository

import com.nbcfinalteam2.ddaraogae.domain.entity.WeatherEntity

interface WeatherRepository {
    suspend fun getWeatherData(lat: String, lng: String): WeatherEntity
}