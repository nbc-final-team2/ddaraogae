package com.nbcfinalteam2.ddaraogae.data.datasource.remote.retrofit

import com.nbcfinalteam2.ddaraogae.data.dto.Dust
import com.nbcfinalteam2.ddaraogae.data.dto.Weather
import retrofit2.http.*

interface RetrofitDataSource {
    @GET
    suspend fun getWeather(
        @Url url: String = "https://api.openweathermap.org/data/2.5/weather",
        @Query("lat") lat: String,
        @Query("lon") lon: String,
        @Query("units") units: String = "metric",
    ) : Weather

    @GET
    suspend fun getDust(
        @Url url: String = "https://api.openweathermap.org/data/2.5/air_pollution",
        @Query("lat") lat: String,
        @Query("lon") lon: String,
    ) : Dust
}