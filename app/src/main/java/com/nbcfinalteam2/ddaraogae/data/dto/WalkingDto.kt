package com.nbcfinalteam2.ddaraogae.data.dto

import java.util.Date

data class WalkingDto(
    val dogId: String? = null,
    val timeTaken: Int? = null,
    val distance: Double? = null,
    val startDateTime: Date? = null,
    val endDateTime: Date? = null,
    val path: String? = null,
)
