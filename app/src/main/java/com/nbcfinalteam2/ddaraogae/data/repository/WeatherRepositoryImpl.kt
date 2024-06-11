package com.nbcfinalteam2.ddaraogae.data.repository

import com.nbcfinalteam2.ddaraogae.data.datasource.remote.retrofit.CityApiService
import com.nbcfinalteam2.ddaraogae.data.datasource.remote.retrofit.WeatherApiService
import com.nbcfinalteam2.ddaraogae.data.mapper.WeatherMapper
import com.nbcfinalteam2.ddaraogae.domain.entity.WeatherEntity
import com.nbcfinalteam2.ddaraogae.domain.repository.WeatherRepository
import javax.inject.Inject

class WeatherRepositoryImpl @Inject constructor(
    private val weatherApiService: WeatherApiService,
    private val cityApiService: CityApiService
) : WeatherRepository {
    override suspend fun getWeatherData(lat: String, lng: String): WeatherEntity {
        val weatherResponse = weatherApiService.getWeather(lat = lat, lon = lng)
        val dustResponse = weatherApiService.getDust(lat = lat, lon = lng)
        val cityResponse = cityApiService.getCity(x = lng, y = lat)

        val weatherItem = WeatherMapper.toWeatherData(weatherResponse, dustResponse, cityResponse)
        return weatherItem
    }
}