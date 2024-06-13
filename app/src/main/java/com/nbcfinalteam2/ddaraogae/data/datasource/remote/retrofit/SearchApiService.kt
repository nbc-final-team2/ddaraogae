package com.nbcfinalteam2.ddaraogae.data.datasource.remote.retrofit

import com.nbcfinalteam2.ddaraogae.data.dto.StoreDto
import retrofit2.http.GET
import retrofit2.http.Query

interface SearchApiService {
    @GET("v2/local/search/keyword.json")
    suspend fun getStore(
        @Query("query") query: String,
        @Query("x") x: String,
        @Query("y") y: String,
        @Query("page") page: String = "1",
        @Query("sort") sort: String = "distance",
        @Query("radius") radius: String = "3000"
    ): StoreDto

}