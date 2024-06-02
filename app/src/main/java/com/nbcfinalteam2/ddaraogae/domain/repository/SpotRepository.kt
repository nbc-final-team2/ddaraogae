package com.nbcfinalteam2.ddaraogae.domain.repository

import com.nbcfinalteam2.ddaraogae.domain.entity.SpotEntity

interface SpotRepository {
    fun getSpotList(): List<SpotEntity>
}