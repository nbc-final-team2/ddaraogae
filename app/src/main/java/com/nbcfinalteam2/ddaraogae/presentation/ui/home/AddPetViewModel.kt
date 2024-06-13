package com.nbcfinalteam2.ddaraogae.presentation.ui.home

import androidx.core.net.toUri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nbcfinalteam2.ddaraogae.domain.entity.DogEntity
import com.nbcfinalteam2.ddaraogae.domain.usecase.InsertDogUseCase
import com.nbcfinalteam2.ddaraogae.presentation.ui.model.DogItemModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddPetViewModel @Inject constructor(
    private val insertDogUseCase: InsertDogUseCase
) : ViewModel(){
    fun insertDog(getDogData:DogItemModel, dogUri:String) = viewModelScope.launch{
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
        insertDogUseCase(dogData, dogUri.toUri())
    }
}