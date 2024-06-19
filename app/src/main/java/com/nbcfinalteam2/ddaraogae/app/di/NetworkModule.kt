package com.nbcfinalteam2.ddaraogae.app.di

import com.nbcfinalteam2.ddaraogae.BuildConfig
import com.nbcfinalteam2.ddaraogae.data.datasource.remote.retrofit.CityApiService
import com.nbcfinalteam2.ddaraogae.data.datasource.remote.retrofit.KakaoInterceptor
import com.nbcfinalteam2.ddaraogae.data.datasource.remote.retrofit.SearchApiService
import com.nbcfinalteam2.ddaraogae.data.datasource.remote.retrofit.WeatherApiService
import com.nbcfinalteam2.ddaraogae.data.datasource.remote.retrofit.WeatherInterceptor
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit.Builder
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Qualifier
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {
    private const val WEATHER_BASE_URL = "https://api.openweathermap.org/data/2.5/"
    private const val KAKAO_BASE_URL = "https://dapi.kakao.com/"

    @Qualifier
    @Retention(AnnotationRetention.BINARY)
    annotation class WeatherInterceptorOkHttpClient

    @Qualifier
    @Retention(AnnotationRetention.BINARY)
    annotation class KakaoInterceptorOkHttpClient

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
    @Singleton
    fun provideWeatherInterceptor(): WeatherInterceptor {
        return WeatherInterceptor
    }

    @Provides
    @Singleton
    fun provideKakaoInterceptor(): KakaoInterceptor {
        return KakaoInterceptor
    }

    @WeatherInterceptorOkHttpClient
    @Provides
    @Singleton
    fun provideWeatherInterceptorOkHttpClient(
        loggingInterceptor: HttpLoggingInterceptor,
        weatherInterceptor: WeatherInterceptor
    ): OkHttpClient {
        return OkHttpClient.Builder()
            .connectTimeout(20, TimeUnit.SECONDS)
            .readTimeout(20, TimeUnit.SECONDS)
            .writeTimeout(20, TimeUnit.SECONDS)
            .addNetworkInterceptor(loggingInterceptor)
            .addInterceptor(weatherInterceptor)
            .build()
    }

    @KakaoInterceptorOkHttpClient
    @Provides
    @Singleton
    fun provideKakaoInterceptorOkHttpClient(
        loggingInterceptor: HttpLoggingInterceptor,
        kakaoInterceptor: KakaoInterceptor
    ): OkHttpClient {
        return OkHttpClient.Builder()
            .connectTimeout(20, TimeUnit.SECONDS)
            .readTimeout(20, TimeUnit.SECONDS)
            .writeTimeout(20, TimeUnit.SECONDS)
            .addNetworkInterceptor(loggingInterceptor)
            .addInterceptor(kakaoInterceptor)
            .build()
    }

    @Provides
    @Singleton
    fun provideRetrofitBuilder(): Builder {
        return Builder()
            .addConverterFactory(GsonConverterFactory.create())
    }

    @Provides
    @Singleton
    fun provideWeatherService(
        retrofitBuilder: Builder,
        @WeatherInterceptorOkHttpClient okHttpClient: OkHttpClient
    ): WeatherApiService {
        return retrofitBuilder
            .baseUrl(WEATHER_BASE_URL)
            .client(okHttpClient)
            .build()
            .create(WeatherApiService::class.java)
    }

    @Provides
    @Singleton
    fun provideKakaoService(
        retrofitBuilder: Builder,
        @KakaoInterceptorOkHttpClient okHttpClient: OkHttpClient
    ): SearchApiService {
        return retrofitBuilder
            .baseUrl(KAKAO_BASE_URL)
            .client(okHttpClient)
            .build()
            .create(SearchApiService::class.java)
    }

    @Provides
    @Singleton
    fun provideCityService(
        retrofitBuilder: Builder
    ): CityApiService {
        return retrofitBuilder
            .baseUrl(KAKAO_BASE_URL)
            .client(OkHttpClient())
            .build()
            .create(CityApiService::class.java)
    }
}