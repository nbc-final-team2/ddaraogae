package com.nbcfinalteam2.ddaraogae.app.di

import com.nbcfinalteam2.ddaraogae.BuildConfig
import com.nbcfinalteam2.ddaraogae.data.datasource.remote.retrofit.RetrofitInterceptor
import com.nbcfinalteam2.ddaraogae.data.datasource.remote.retrofit.SearchApiService
import com.nbcfinalteam2.ddaraogae.data.datasource.remote.retrofit.WeatherApiService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {
    private const val WEATHER_BASE_URL = "https://api.openweathermap.org/data/2.5/"
    private const val KAKAO_BASE_URL = "https://dapi.kakao.com/"

    @Provides
    fun provideLoggingInterceptor(): HttpLoggingInterceptor {
        return HttpLoggingInterceptor().apply {
            level = if (BuildConfig.DEBUG) {
                HttpLoggingInterceptor.Level.BODY
            } else {
                HttpLoggingInterceptor.Level.NONE
            }
        }
    }

    @Provides
    fun provideRetrofitInterceptorOkHttpClient(
        loggingInterceptor: HttpLoggingInterceptor
    ): OkHttpClient {
        return OkHttpClient.Builder()
            .connectTimeout(20, TimeUnit.SECONDS)
            .readTimeout(20, TimeUnit.SECONDS)
            .writeTimeout(20, TimeUnit.SECONDS)
            .addNetworkInterceptor(loggingInterceptor)
            .addInterceptor(RetrofitInterceptor)
            .build()
    }

    @Provides
    fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit.Builder {
        return Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .client(okHttpClient)
    }

    @Provides
    fun provideWeatherService(retrofitBuilder: Retrofit.Builder): WeatherApiService {
        return retrofitBuilder
            .baseUrl(WEATHER_BASE_URL)
            .build()
            .create(WeatherApiService::class.java)
    }

    @Provides
    fun provideKakaoService(retrofitBuilder: Retrofit.Builder): SearchApiService {
        return retrofitBuilder
            .baseUrl(KAKAO_BASE_URL)
            .build()
            .create(SearchApiService::class.java)
    }
}