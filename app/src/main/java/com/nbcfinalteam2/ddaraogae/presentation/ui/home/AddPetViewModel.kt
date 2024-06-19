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

    fun insertDog(getDogData: DogInfo) = viewModelScope.launch {
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
            insertDogUseCase(dogData, addUiState.value.byteArray)
        }.onSuccess {
            _insertEvent.emit(DefaultEvent.Success)
        }.onFailure {
            _insertEvent.emit(DefaultEvent.Failure(R.string.home_add_msg_fail_insert))
        }
    }

    fun setImageUri(imageUri: Uri?, byteArray: ByteArray?) {
        _addUiState.value = AddUiState(
            imageUri = imageUri,
            byteArray = byteArray,
            isThumbnailVisible = imageUri != null
        )
    }
}