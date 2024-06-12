package com.nbcfinalteam2.ddaraogae.domain.usecase

import android.content.Context
import android.net.Uri
import com.nbcfinalteam2.ddaraogae.domain.entity.WalkingEntity

interface InsertWalkingDataUseCase {
    suspend operator fun invoke(walkingEntity: WalkingEntity, mapImage: Uri?, context: Context)
}