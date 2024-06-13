package com.nbcfinalteam2.ddaraogae.presentation.model

import com.nbcfinalteam2.ddaraogae.domain.entity.LatLngEntity
import java.util.Date

data class WalkingInfo (
    val id: String?,
    val dogId: String?,
    val timeTaken: Int?,
    val distance: Double?,
    val startDateTime: Date?,
    val endDateTime: Date?,
    val path: List<LatLngEntity>?
)