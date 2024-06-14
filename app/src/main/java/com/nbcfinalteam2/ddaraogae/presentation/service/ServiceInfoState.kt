package com.nbcfinalteam2.ddaraogae.presentation.service

data class ServiceInfoState(
    val distance: Double,
    val time: Int
) {
    companion object {
        fun init() = ServiceInfoState(
            distance = 0.0,
            time = 0
        )
    }
}
