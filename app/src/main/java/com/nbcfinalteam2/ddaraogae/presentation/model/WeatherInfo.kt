package com.nbcfinalteam2.ddaraogae.presentation.model

data class WeatherInfo(
    val id: Long?,
    val temperature: String,
    val city: String,
    val condition: Int,
    val fineDustStatusIcon: Int,
    val fineDustStatus: Int,
    val ultraFineDustStatusIcon: Int,
    val ultraFineDustStatus: Int
)
