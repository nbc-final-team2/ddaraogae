package com.nbcfinalteam2.ddaraogae.presentation.ui.mypage

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nbcfinalteam2.ddaraogae.domain.entity.DogEntity
import com.nbcfinalteam2.ddaraogae.domain.usecase.UpdateDogUseCase
import com.nbcfinalteam2.ddaraogae.presentation.model.DogInfo
import com.nbcfinalteam2.ddaraogae.presentation.ui.home.AddUiState
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

    private val _editUiState = MutableStateFlow(EditUiState.init())
    val editUiState: StateFlow<EditUiState> = _editUiState.asStateFlow()

    fun updateDog(getDogData: DogInfo) = viewModelScope.launch {
        runCatching {
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
            updateDogUseCase(dogData, editUiState.value.byteArray)
        }.onSuccess {
            _taskState.value = UpdateTaskState.Success
        }.onFailure { e ->
            _taskState.value = UpdateTaskState.Error(e)
        }
    }

    fun setImageUri(imageUri: Uri?, byteArray: ByteArray?) {
        _editUiState.value = EditUiState(
            imageUri = imageUri,
            byteArray = byteArray
        )
    }
}