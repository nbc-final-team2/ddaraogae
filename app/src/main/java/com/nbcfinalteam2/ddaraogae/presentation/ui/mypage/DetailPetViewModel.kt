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
    private val getDogByIdUseCase: GetDogByIdUseCase
) : ViewModel() {
    private val _uiState = MutableStateFlow(DetailDogUiState.init())
    val uiState: StateFlow<DetailDogUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            val dummyData = listOf(
                DogEntity(
                    "",
                    "쿠키",
                    0,
                    4,
                    "푸들",
                    "건들면 물어요",
                    "/storage/emulated/0/Pictures/lactofit.jpg"
                ),
                DogEntity(
                    "",
                    "초코",
                    1,
                    1,
                    "말티즈",
                    "옷 입는거 좋아함",
                    "/storage/emulated/0/Pictures/lactofit.jpg"
                ),
                DogEntity(
                    "",
                    "캔디",
                    1,
                    6,
                    "치와와",
                    "고양이 무서워함, 산책 갈 때 주의 필요",
                    "/storage/emulated/0/Pictures/lactofit.jpg"
                )
            )
            //val loadPetList = getDogListUseCase()
             val petList = dummyData.map {
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
                        listPet = list
                    )
                }
            }
        }
        //firebase에서 리스트 불러오기

        //petList = dummyData

    }

    fun loadDetailPet(dogData: DogItemModel) = viewModelScope.launch {
        var pet = dogData
        Log.d("testmodelDog", "${pet}")
        //firebase에서 받아오기
        pet?.let { petData ->
            _uiState.update { prev ->
                prev.copy(
                    pet = petData
                )
            }
        }

    }

}