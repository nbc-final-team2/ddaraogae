package com.nbcfinalteam2.ddaraogae.presentation.ui.dog

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nbcfinalteam2.ddaraogae.R
import com.nbcfinalteam2.ddaraogae.domain.usecase.GetDogByIdUseCase
import com.nbcfinalteam2.ddaraogae.domain.usecase.GetDogListUseCase
import com.nbcfinalteam2.ddaraogae.presentation.model.DefaultEvent
import com.nbcfinalteam2.ddaraogae.presentation.model.DogInfo
import com.nbcfinalteam2.ddaraogae.presentation.ui.add.AddUiState
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
class DetailPetViewModel @Inject constructor(
    private val getDogListUseCase: GetDogListUseCase,
    private val getDogByIdUseCase: GetDogByIdUseCase,
) : ViewModel() {
    private val _detailUiState = MutableStateFlow(DetailUiState.init())
    val detailUiState: StateFlow<DetailUiState> = _detailUiState.asStateFlow()

    private val _dogListState = MutableStateFlow<List<DogInfo>>(emptyList())
    val dogListState: StateFlow<List<DogInfo>> = _dogListState.asStateFlow()

    private val _selectedDogState = MutableStateFlow<DogInfo?>(null)
    val selectedDogState: StateFlow<DogInfo?> = _selectedDogState.asStateFlow()

    private val _selectedDogIdState = MutableStateFlow<String?>(null)
    val selectedDogIdState: StateFlow<String?> = _selectedDogIdState.asStateFlow()

    private val _loadEvent = MutableSharedFlow<DefaultEvent>()
    val loadEvent: SharedFlow<DefaultEvent> = _loadEvent.asSharedFlow()

    private val _nullEvent = MutableSharedFlow<NullEvent>()
    val nullEvent: SharedFlow<NullEvent> = _nullEvent.asSharedFlow()

    private var selectDogId = ""

    init {
        getDogList()
    }

    fun getDogList() = viewModelScope.launch {
        runCatching {
            val dogList = getDogListUseCase().mapIndexed { ind, dogEntity ->
                DogInfo(
                    id = dogEntity.id,
                    name = dogEntity.name,
                    gender = dogEntity.gender,
                    age = dogEntity.age,
                    lineage = dogEntity.lineage,
                    memo = dogEntity.memo,
                    thumbnailUrl = dogEntity.thumbnailUrl,
                    isSelected = ind == 0
                )
            }
            _dogListState.update {
                dogList
            }
        }.onSuccess {
            _loadEvent.emit(DefaultEvent.Success)
        }.onFailure {
            _loadEvent.emit(DefaultEvent.Failure(R.string.msg_load_dog_fail))
        }
    }
    fun getDogData(dogId : String) = viewModelScope.launch{
        runCatching {
            val getDogInfo = getDogByIdUseCase(dogId)
            if(getDogInfo == null) {
                _nullEvent.emit(NullEvent.NullData)
            }
            var dogInfo = getDogInfo?.let {
                DogInfo(
                    id = it.id,
                    name = it.name,
                    gender = it.gender,
                    age = it.age,
                    lineage = it.lineage,
                    memo = it.memo,
                    thumbnailUrl = it.thumbnailUrl,
                    isSelected = true
                )
            }
            _selectedDogState.update {
                dogInfo
            }
        }.onSuccess {
            _loadEvent.emit(DefaultEvent.Success)
        }.onFailure {
            _loadEvent.emit(DefaultEvent.Failure(R.string.msg_load_dog_fail))
        }
    }

    fun selectDog(dogInfo: DogInfo) {
        _dogListState.update { prev ->
            prev.map {
                it.copy(
                    isSelected = dogInfo.id == it.id
                )
            }
        }
        _selectedDogIdState.update {
            dogInfo.id
        }
        if(dogInfo.id != null ) selectDogId = dogInfo.id
    }
    fun refreshDogInfo() = viewModelScope.launch {
        runCatching {
            selectedDogIdState.value?.let { getDogData(it) }
        }.onSuccess {
            _loadEvent.emit(DefaultEvent.Success)
        }.onFailure {
            _loadEvent.emit(DefaultEvent.Failure(R.string.msg_load_dog_fail))
        }
    }
}