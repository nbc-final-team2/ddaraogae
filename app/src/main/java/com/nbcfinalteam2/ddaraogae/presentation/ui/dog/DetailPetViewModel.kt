package com.nbcfinalteam2.ddaraogae.presentation.ui.dog

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nbcfinalteam2.ddaraogae.R
import com.nbcfinalteam2.ddaraogae.domain.usecase.DeleteDogUseCase
import com.nbcfinalteam2.ddaraogae.domain.usecase.GetDogListUseCase
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
class DetailPetViewModel @Inject constructor(
    private val deleteDogUseCase: DeleteDogUseCase,
    private val getDogListUseCase: GetDogListUseCase,
) : ViewModel() {

    private val _detailUiState = MutableStateFlow(DetailUiState.init())
    val detailUiState: StateFlow<DetailUiState> = _detailUiState.asStateFlow()

    private val _dogListState = MutableStateFlow<List<DogInfo>>(emptyList())
    val dogListState: StateFlow<List<DogInfo>> = _dogListState.asStateFlow()

    private val _selectedDogState = MutableStateFlow<DogInfo?>(null)
    val selectedDogState: StateFlow<DogInfo?> = _selectedDogState.asStateFlow()

    private val _deleteEvent = MutableSharedFlow<DefaultEvent>()
    val deleteEvent: SharedFlow<DefaultEvent> = _deleteEvent.asSharedFlow()

    private val _loadEvent = MutableSharedFlow<DefaultEvent>()
    val loadEvent: SharedFlow<DefaultEvent> = _loadEvent.asSharedFlow()

    init {
        getDogList()
    }

    private fun getDogList() = viewModelScope.launch {
        _detailUiState.update {
            it.copy(isLoading = true)
        }
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
            _selectedDogState.update {
                _dogListState.value.firstOrNull()
            }
        }.onSuccess {
            _detailUiState.update {
                it.copy(isLoading = false)
            }
            _loadEvent.emit(DefaultEvent.Success)
        }.onFailure {
            _detailUiState.update {
                it.copy(isLoading = false)
            }
            _loadEvent.emit(DefaultEvent.Failure(R.string.msg_load_dog_fail))
        }
    }

    fun refreshDogList() = viewModelScope.launch {
        _detailUiState.update {
            it.copy(isLoading = true)
        }
        runCatching {
            var selectedDogInd: Int? = null

            val dogList = getDogListUseCase().mapIndexed { ind, dogEntity ->
                DogInfo(
                    id = dogEntity.id,
                    name = dogEntity.name,
                    gender = dogEntity.gender,
                    age = dogEntity.age,
                    lineage = dogEntity.lineage,
                    memo = dogEntity.memo,
                    thumbnailUrl = dogEntity.thumbnailUrl,
                    isSelected = selectedDogState.value?.let {
                        if(it.id == dogEntity.id) {
                            selectedDogInd = ind
                            true
                        } else {
                            false
                        }
                    }?:false
                )
            }.toMutableList()

            _dogListState.update {
                selectedDogInd?.let {
                    dogList
                }?:dogList.apply { if(this.isNotEmpty()) this[0].isSelected=true }
            }
            _selectedDogState.update {
                selectedDogInd?.let {
                    dogList[it]
                }?: dogList.firstOrNull()
            }
        }.onSuccess {
            _detailUiState.update {
                it.copy(isLoading = false)
            }
            _loadEvent.emit(DefaultEvent.Success)
        }.onFailure {
            _detailUiState.update {
                it.copy(isLoading = false)
            }
            _loadEvent.emit(DefaultEvent.Failure(R.string.msg_load_changes_fail))
        }
    }

    fun deleteSelectedDogData() = viewModelScope.launch {
        _detailUiState.update {
            it.copy(isLoading = true)
        }
        runCatching {
            selectedDogState.value?.let {
                deleteDogUseCase(it.id!!)
            }
        }.onSuccess {
            _deleteEvent.emit(DefaultEvent.Success)
            _detailUiState.update {
                it.copy(isLoading = false)
            }
        }.onFailure {
            _deleteEvent.emit(DefaultEvent.Failure(R.string.msg_delete_dog_fail))
            _detailUiState.update {
                it.copy(isLoading = false)
            }
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
        _selectedDogState.update {
            dogInfo
        }
    }
}