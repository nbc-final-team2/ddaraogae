package com.nbcfinalteam2.ddaraogae.presentation.ui.mypage

import androidx.core.net.toUri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nbcfinalteam2.ddaraogae.domain.entity.DogEntity
import com.nbcfinalteam2.ddaraogae.domain.usecase.UpdateDogUseCase
import com.nbcfinalteam2.ddaraogae.presentation.ui.model.DogItemModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EditPetViewModel @Inject constructor(
    private val updateDogUseCase: UpdateDogUseCase
) : ViewModel() {
    fun updateDog(getDogData:DogItemModel, byteImage: ByteArray?) = viewModelScope.launch{
            val dogData = getDogData.let {
                DogEntity(
                    it.id,
                    it.name,
                    it.gender,
                    it.age,
                    it.lineage,
                    it.memo,
                    it.thumbnailUrl
                )
            }
            updateDogUseCase(dogData, byteImage)

    }

}