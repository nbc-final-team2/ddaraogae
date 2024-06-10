package com.nbcfinalteam2.ddaraogae.presentation.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nbcfinalteam2.ddaraogae.domain.entity.DogEntity
import com.nbcfinalteam2.ddaraogae.domain.usecase.GetCurrentUserUseCase
import com.nbcfinalteam2.ddaraogae.domain.usecase.InsertDogUseCase
import com.nbcfinalteam2.ddaraogae.presentation.model.DogInfo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddViewModel @Inject constructor(
    private val insertDogUseCase: InsertDogUseCase,
    private val getCurrentUserUseCase: GetCurrentUserUseCase
) : ViewModel() {

    // 네트워크 예외처리, try catch가 좋을것같긴함
    fun addDog(dogInfo: DogInfo) {
        viewModelScope.launch {
            val user = getCurrentUserUseCase()
            user?.let {
                val dogEntity = DogEntity(
                    id = dogInfo.id,
                    name = dogInfo.name,
                    gender = dogInfo.gender,
                    age = dogInfo.age,
                    lineage = dogInfo.lineage,
                    memo = dogInfo.memo,
                    thumbnailUrl = dogInfo.thumbnailUrl
                )
                insertDogUseCase(dogEntity) // 네트워크 예외처리
            }
        }
    }
}