package com.nbcfinalteam2.ddaraogae.domain.entity

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class LatLngEntity(
    val lat: Double?,
    val lng: Double?
): Parcelable
