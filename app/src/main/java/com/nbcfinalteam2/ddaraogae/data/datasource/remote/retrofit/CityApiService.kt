package com.nbcfinalteam2.ddaraogae.data.datasource.remote.retrofit

import com.nbcfinalteam2.ddaraogae.data.dto.City
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

interface CityApiService {
    @GET("v2/local/geo/coord2regioncode")
    suspend fun getCity(
        @Header("Authorization") Authorization: String = Key.KAKAO_API_KEY,
        @Query("x") x: String,
        @Query("y") y: String,
    ) : City
}