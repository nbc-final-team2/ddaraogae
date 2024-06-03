package com.nbcfinalteam2.ddaraogae.data.datasource.remote.retrofit

import com.nbcfinalteam2.ddaraogae.BuildConfig
import com.nbcfinalteam2.ddaraogae.data.dto.Dust
import com.nbcfinalteam2.ddaraogae.data.dto.Weather
import retrofit2.http.GET
import retrofit2.http.Query

interface RetrofitDataSource {
    @GET("weather")
    suspend fun getWeather(
        @Query("lat") lat: String = "126.986",
        @Query("lon") lon: String = "37.541",
        @Query("appid") appid: String = BuildConfig.WEATHER_API_KEY,
        @Query("units") units: String = "metric",
        @Query("lang") lang: String = "kr"
    ) : Weather

    @GET("air_pollution")
    suspend fun getDust(
        @Query("lat") lat: String = "126.986",
        @Query("lon") lon: String = "37.541",
        @Query("appid") appid: String = BuildConfig.WEATHER_API_KEY
    ) : Dust
}