package com.nbcfinalteam2.ddaraogae.data.datasource.remote.retrofit

import com.nbcfinalteam2.ddaraogae.data.dto.Dust
import com.nbcfinalteam2.ddaraogae.data.dto.WeatherDto
import retrofit2.http.*

interface WeatherApiService {
    @GET("weather")
    suspend fun getWeather(
        @Query("lat") lat: String,
        @Query("lon") lon: String,
        @Query("units") units: String = "metric",
    ) : WeatherDto

    @GET("air_pollution")
    suspend fun getDust(
        @Query("lat") lat: String,
        @Query("lon") lon: String,
    ) : Dust
}