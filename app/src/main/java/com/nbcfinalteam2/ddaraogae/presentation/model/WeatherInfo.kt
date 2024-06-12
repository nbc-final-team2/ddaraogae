package com.nbcfinalteam2.ddaraogae.presentation.model

data class WeatherInfo(
    val id: String,
    val temperature: String,
    val city: String,
    val condition: String,
    val fineDustStatusIcon: Int,
    val ultraFineDustStatusIcon: Int
)
