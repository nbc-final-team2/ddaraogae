package com.nbcfinalteam2.ddaraogae.presentation.ui.edit

import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nbcfinalteam2.ddaraogae.R
import com.nbcfinalteam2.ddaraogae.domain.entity.DogEntity
import com.nbcfinalteam2.ddaraogae.domain.usecase.DeleteDogUseCase
import com.nbcfinalteam2.ddaraogae.domain.usecase.UpdateDogUseCase
import com.nbcfinalteam2.ddaraogae.presentation.model.DefaultEvent
import com.nbcfinalteam2.ddaraogae.presentation.model.DogInfo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EditPetViewModel @Inject constructor(
    private val deleteDogUseCase: DeleteDogUseCase,
    private val updateDogUseCase: UpdateDogUseCase
) : ViewModel() {

    private val _updateEvent = MutableSharedFlow<DefaultEvent>()
    val updateEvent: SharedFlow<DefaultEvent> = _updateEvent.asSharedFlow()

    private val _editUiState = MutableStateFlow(EditUiState.init())
    val editUiState: StateFlow<EditUiState> = _editUiState.asStateFlow()

    private val _deleteEvent = MutableSharedFlow<DefaultEvent>()
    val deleteEvent: SharedFlow<DefaultEvent> = _deleteEvent.asSharedFlow()

    private var imgByteArray: ByteArray? = null

    fun updateDog(getDogData: DogInfo) = viewModelScope.launch {
        _editUiState.update {
            it.copy(
                isLoading = true
            )
        }
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
            updateDogUseCase(dogData, imgByteArray)
        }.onSuccess {
            _updateEvent.emit(DefaultEvent.Success)
            _editUiState.update {
                it.copy(
                    isLoading = false
                )
            }
        }.onFailure { e ->
            _updateEvent.emit(DefaultEvent.Failure(R.string.msg_edit_fail))
            _editUiState.update {
                it.copy(
                    isLoading = false
                )
            }
        }
    }

    fun setImageUri(imageUri: Uri?, byteArray: ByteArray?) {
        _editUiState.value = EditUiState(
            imageSource = imageUri?.let { ImageSource.ImageUri(imageUri) },
            isThumbnailVisible = imageUri != null,
            isInit = true
        )
        imgByteArray = byteArray
    }

    fun setImageUrl(imageUrl: String?) {
        _editUiState.value = EditUiState(
            imageSource = imageUrl?.let { ImageSource.ImageUrl(imageUrl) },
            isThumbnailVisible = !imageUrl.isNullOrBlank(),
            isInit = true
        )
    }
    fun deleteSelectedDogData(dogId : String?) = viewModelScope.launch {
        runCatching {
            deleteDogUseCase(dogId!!)
        }.onSuccess {
            _deleteEvent.emit(DefaultEvent.Success)

        }.onFailure {
            _deleteEvent.emit(DefaultEvent.Failure(R.string.msg_delete_dog_fail))
        }
    }
}