package com.nbcfinalteam2.ddaraogae.presentation.ui.home

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nbcfinalteam2.ddaraogae.R
import com.nbcfinalteam2.ddaraogae.domain.entity.DogEntity
import com.nbcfinalteam2.ddaraogae.domain.usecase.InsertDogUseCase
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
class AddPetViewModel @Inject constructor(
    private val insertDogUseCase: InsertDogUseCase
) : ViewModel() {

    private val _addUiState = MutableStateFlow(AddUiState.init())
    val addUiState: StateFlow<AddUiState> = _addUiState.asStateFlow()

    private val _insertEvent = MutableSharedFlow<DefaultEvent>()
    val insertEvent: SharedFlow<DefaultEvent> = _insertEvent.asSharedFlow()

    private var imgByteArray: ByteArray? = null

    fun insertDog(getDogData: DogInfo) = viewModelScope.launch {
        _addUiState.update {
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
            insertDogUseCase(dogData, imgByteArray)
        }.onSuccess {
            _addUiState.update {
                it.copy(
                    isLoading = false
                )
            }
            _insertEvent.emit(DefaultEvent.Success)
        }.onFailure {
            _addUiState.update {
                it.copy(
                    isLoading = false
                )
            }
            _insertEvent.emit(DefaultEvent.Failure(R.string.home_add_msg_fail_insert))
        }
    }

    fun setImageUri(imageUri: Uri?, byteArray: ByteArray?) {
        _addUiState.value = AddUiState(
            imageUri = imageUri,
            isThumbnailVisible = imageUri != null
        )
        imgByteArray = byteArray
    }
}