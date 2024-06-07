package com.nbcfinalteam2.ddaraogae.data.datasource.remote.retrofit

import com.nbcfinalteam2.ddaraogae.data.dto.Store
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

interface SearchApiService {
    @GET("v2/local/search/keyword.json")
    suspend fun getStoreForHospital(
        @Header("Authorization") key: String = Key.KAKAO_API_KEY,
        @Query("query") query: String = "동물병원",
        @Query("x") x: String,
        @Query("y") y: String,
        @Query("page") page: String = "1",
        @Query("sort") sort: String = "distance",
        @Query("radius") radius: String = "3000",
    ) : Store

    @GET("v2/local/search/keyword.json")
    suspend fun getStoreForFood(
        @Header("Authorization") key: String = Key.KAKAO_API_KEY,
        @Query("query") query: String = "애견동반",
        @Query("x") x: String,
        @Query("y") y: String,
        @Query("page") page: String = "1",
        @Query("sort") sort: String = "distance",
        @Query("radius") radius: String = "3000",
        ) : Store
}