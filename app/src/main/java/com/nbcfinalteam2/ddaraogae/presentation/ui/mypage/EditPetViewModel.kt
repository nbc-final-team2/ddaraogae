package com.nbcfinalteam2.ddaraogae.presentation.ui.mypage

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nbcfinalteam2.ddaraogae.domain.entity.DogEntity
import com.nbcfinalteam2.ddaraogae.domain.usecase.UpdateDogUseCase
import com.nbcfinalteam2.ddaraogae.presentation.model.DogInfo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EditPetViewModel @Inject constructor(
    private val updateDogUseCase: UpdateDogUseCase
) : ViewModel() {

    private val _taskState = MutableStateFlow<UpdateTaskState>(UpdateTaskState.Idle)
    val taskState: StateFlow<UpdateTaskState> = _taskState.asStateFlow()

    fun updateDog(getDogData: DogInfo, byteImage: ByteArray?) = viewModelScope.launch {
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

        try {
            updateDogUseCase(dogData, byteImage)
            _taskState.value = UpdateTaskState.Success
        } catch (e: Exception) {
            _taskState.value = UpdateTaskState.Error(e)
        }
    }
}