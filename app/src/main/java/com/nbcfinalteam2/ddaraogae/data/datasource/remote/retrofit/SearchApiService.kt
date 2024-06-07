package com.nbcfinalteam2.ddaraogae.data.datasource.remote.retrofit

import com.nbcfinalteam2.ddaraogae.data.dto.Store
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

interface SearchApiService {
    @GET("v2/local/search/keyword.json")
    suspend fun getStore(
        @Header("Authorization") key: String = Key.KAKAO_API_KEY,
        @Query("query") query: String = "동물병원",
        @Query("x") x: String = "37.5560294", //longitude
        @Query("y") y: String = "127.0872547", //latitude
        @Query("page") page: String = "1",
        @Query("sort") sort: String = "distance",
    ) : Store
}