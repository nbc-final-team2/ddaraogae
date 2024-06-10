package com.nbcfinalteam2.ddaraogae.domain.entity

data class WeatherEntity (
    val id : Long,
    val temperature: Double,
    val pm25: Double,
    val pm10: Double,
    val city: String,
)