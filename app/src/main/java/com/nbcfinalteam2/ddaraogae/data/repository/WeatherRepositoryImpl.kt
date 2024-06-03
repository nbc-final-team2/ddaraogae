package com.nbcfinalteam2.ddaraogae.data.repository

import com.nbcfinalteam2.ddaraogae.data.datasource.remote.retrofit.RetrofitDataSource
import com.nbcfinalteam2.ddaraogae.data.mapper.WeatherMapper
import com.nbcfinalteam2.ddaraogae.data.mapper.model.WeatherItemModel
import com.nbcfinalteam2.ddaraogae.domain.repository.WeatherRepository

class WeatherRepositoryImpl(private val retrofitDataSource: RetrofitDataSource) : WeatherRepository {
    override suspend fun getWeatherData(): WeatherItemModel {
        val weatherResponse = retrofitDataSource.getWeather()
        val dustResponse = retrofitDataSource.getDust()
        val weatherItem = WeatherMapper.toWeatherData(weatherResponse, dustResponse)
        return weatherItem
    }
}