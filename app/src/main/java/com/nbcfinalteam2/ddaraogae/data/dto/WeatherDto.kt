package com.nbcfinalteam2.ddaraogae.data.dto

data class Weather (
    val coord : WeatherCoord,
    val weather: List<WeatherWeather>,
    val main : WeatherMain,
)

data class WeatherCoord (
    val lat: Double, //위도
    val lon : Double //경도
)

data class WeatherWeather (
    val id : Long, //기상 조건 id
)

data class WeatherMain (
    val temp : Double, //기온
)