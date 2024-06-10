package com.nbcfinalteam2.ddaraogae.data.dto

import com.google.gson.annotations.SerializedName

data class WeatherDto (
    @SerializedName("coord") val coord : WeatherCoord,
    @SerializedName("weather") val weather: List<WeatherWeather>,
    @SerializedName("main") val main : WeatherMain
)

data class WeatherCoord (
    @SerializedName("lat") val lat: Double, //위도
    @SerializedName("lon") val lon : Double //경도
)

data class WeatherWeather (
    @SerializedName("id") val id : Long //기상 조건 id
)

data class WeatherMain (
    @SerializedName("temp") val temp : Double //기온
)