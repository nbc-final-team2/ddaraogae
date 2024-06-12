package com.nbcfinalteam2.ddaraogae.presentation.util

import android.location.Location

object DistanceCalculator {
    fun getDistance(lat1: Double, lng1: Double, lat2: Double, lng2: Double) : Float {
        val targetFrom = Location("from")
        targetFrom.latitude = lat1
        targetFrom.longitude = lng1

        val targetTo = Location("to")
        targetTo.latitude = lat2
        targetTo.longitude = lng2

        return targetFrom.distanceTo(targetTo)/1000
    }
}