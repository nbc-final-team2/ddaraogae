package com.nbcfinalteam2.ddaraogae.data.repository

import com.nbcfinalteam2.ddaraogae.data.datasource.remote.retrofit.WeatherApiService
import com.nbcfinalteam2.ddaraogae.data.mapper.WeatherMapper
import com.nbcfinalteam2.ddaraogae.domain.entity.WeatherEntity
import com.nbcfinalteam2.ddaraogae.domain.repository.WeatherRepository

class WeatherRepositoryImpl(private val weatherApiService: WeatherApiService) : WeatherRepository {
    override suspend fun getWeatherData(lat: String, lon: String): WeatherEntity {
        val weatherResponse = weatherApiService.getWeather(lat = lat, lon = lon)
        val dustResponse = weatherApiService.getDust(lat = lat, lon = lon)
        val weatherItem = WeatherMapper.toWeatherData(weatherResponse, dustResponse)
        return weatherItem
    }
}