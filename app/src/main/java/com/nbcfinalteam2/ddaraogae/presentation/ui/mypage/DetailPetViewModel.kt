package com.nbcfinalteam2.ddaraogae.presentation.ui.mypage

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nbcfinalteam2.ddaraogae.domain.entity.DogEntity
import com.nbcfinalteam2.ddaraogae.domain.usecase.DeleteDogUseCase
import com.nbcfinalteam2.ddaraogae.domain.usecase.GetDogByIdUseCase
import com.nbcfinalteam2.ddaraogae.domain.usecase.GetDogListUseCase
import com.nbcfinalteam2.ddaraogae.presentation.ui.model.DogItemModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DetailPetViewModel @Inject constructor(
    private val deleteDogUseCase: DeleteDogUseCase,
    private val getDogListUseCase: GetDogListUseCase,
) : ViewModel() {
    private val _uiState = MutableStateFlow(DetailDogUiState.init())
    val uiState: StateFlow<DetailDogUiState> = _uiState.asStateFlow()

    fun getDogList() = viewModelScope.launch{
        try{
            val loadPetList = getDogListUseCase()
            val petList = loadPetList.map {
                DogItemModel(
                    it.id,
                    it.name,
                    it.gender,
                    it.age,
                    it.lineage,
                    it.memo,
                    it.thumbnailUrl
                )
            }
            petList.let { list ->
                _uiState.update { prev ->
                    prev.copy(
                        listPet = list,
                        pet = list[0],
                        listPetEmpty = false
                    )
                }
            }
        } catch (e:IndexOutOfBoundsException){
            Log.e("error : detailPetViewModel", "$e")
                _uiState.update { prev ->
                    prev.copy(
                        listPetEmpty = true
                    )

            }
        }
    }
    fun deleteDogData(dogId:String) = viewModelScope.launch{
        deleteDogUseCase(dogId)
    }
}