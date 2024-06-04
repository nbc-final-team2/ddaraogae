package com.nbcfinalteam2.ddaraogae.data.datasource.remote.retrofit

import com.nbcfinalteam2.ddaraogae.data.dto.Dust
import com.nbcfinalteam2.ddaraogae.data.dto.Weather
import retrofit2.http.GET
import retrofit2.http.Query

interface RetrofitDataSource {
    @GET("weather")
    suspend fun getWeather(
        @Query("lat") lat: String = "37.57",
        @Query("lon") lon: String = "126.98",
        @Query("appid") appid: String = Key.API_KEY,
        @Query("units") units: String = "metric",
    ) : Weather

    @GET("air_pollution")
    suspend fun getDust(
        @Query("lat") lat: String = "37.57",
        @Query("lon") lon: String = "126.98",
        @Query("appid") appid: String = Key.API_KEY
    ) : Dust
}