package com.nbcfinalteam2.ddaraogae.data.datasource.remote.retrofit

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import com.nbcfinalteam2.ddaraogae.BuildConfig
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object RetrofitClient {
    private const val WEATHER_BASE_URL = "https://api.openweathermap.org/data/2.5/"

    private fun createOkHttpClient() : OkHttpClient {
        val httpLoggingInterceptor = HttpLoggingInterceptor()

        if (BuildConfig.DEBUG) {
            httpLoggingInterceptor.level = HttpLoggingInterceptor.Level.BODY
        } else {
            httpLoggingInterceptor.level = HttpLoggingInterceptor.Level.NONE
        }

        return OkHttpClient.Builder()
            .connectTimeout(20, TimeUnit.SECONDS)
            .readTimeout(20, TimeUnit.SECONDS)
            .writeTimeout(20, TimeUnit.SECONDS)
            .addNetworkInterceptor(httpLoggingInterceptor)
            .build()
    }

    private val weatherRetrofit = Retrofit.Builder()
        .baseUrl(WEATHER_BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .client(createOkHttpClient())
        .build()

    private val dustRetrofit = Retrofit.Builder()
        .baseUrl(WEATHER_BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .client(createOkHttpClient())
        .build()

    val weatherNetwork : RetrofitDataSource  = weatherRetrofit.create(RetrofitDataSource::class.java)
    val dustNetwork : RetrofitDataSource  = dustRetrofit.create(RetrofitDataSource::class.java)
}