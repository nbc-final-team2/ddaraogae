package com.nbcfinalteam2.ddaraogae.domain.usecase

import android.net.Uri
import com.nbcfinalteam2.ddaraogae.domain.entity.DogEntity

interface InsertDogUseCase {
    suspend operator fun invoke(dogEntity: DogEntity, imageUri: Uri?)
}