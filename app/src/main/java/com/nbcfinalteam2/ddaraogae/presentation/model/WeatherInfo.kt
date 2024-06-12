package com.nbcfinalteam2.ddaraogae.presentation.model

data class WeatherInfo(
    val temperature: String,
    val fineDust: String,
    val ultraFineDust: String,
    val city: String,
    val condition: String,
    val fineDustStatusIcon: Int,
    val ultraFineDustStatusIcon: Int
)
