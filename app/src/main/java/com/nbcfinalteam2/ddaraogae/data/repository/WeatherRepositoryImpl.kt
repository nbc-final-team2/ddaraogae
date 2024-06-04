package com.nbcfinalteam2.ddaraogae.data.repository

import com.nbcfinalteam2.ddaraogae.data.datasource.remote.retrofit.RetrofitDataSource
import com.nbcfinalteam2.ddaraogae.data.mapper.WeatherMapper
import com.nbcfinalteam2.ddaraogae.domain.entity.WeatherEntity
import com.nbcfinalteam2.ddaraogae.domain.repository.WeatherRepository

class WeatherRepositoryImpl(private val retrofitDataSource: RetrofitDataSource) : WeatherRepository {
    override suspend fun getWeatherData(lat: String, lon: String): WeatherEntity {
        val weatherResponse = retrofitDataSource.getWeather(lat, lon)
        val dustResponse = retrofitDataSource.getDust(lat, lon)
        val weatherItem = WeatherMapper.toWeatherData(weatherResponse, dustResponse)
        return weatherItem
    }
}