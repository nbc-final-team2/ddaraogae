package com.nbcfinalteam2.ddaraogae.presentation.ui.mypage

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class DetailPetViewModel() : ViewModel() {
    private val _uiState = MutableStateFlow(DetailUiState.init())
    val uiState:StateFlow<DetailUiState> = _uiState.asStateFlow()
    fun loadPetList() = viewModelScope.launch {
        var petList = listOf<DogModel>()
        //firebase에서 리스트 불러오기
        val dummyData = listOf(
            DogModel("","쿠키",false,4, "푸들", "건들면 물어요", "/storage/emulated/0/Pictures/lactofit.jpg"),
            DogModel("","초코",true,1, "말티즈", "옷 입는거 좋아함", "/storage/emulated/0/Pictures/lactofit.jpg"),
            DogModel("","캔디",false,6, "치와와", "고양이 무서워함, 산책 갈 때 주의 필요", "/storage/emulated/0/Pictures/lactofit.jpg"))
        petList = dummyData
        petList?.let { list ->
            _uiState.update { prev ->
                prev.copy(
                    listPet = list
                )
            }
        }
    }
    fun loadDetailPet(dogData:DogModel) = viewModelScope.launch {
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
class DetailViewModelFactory(context: Context):ViewModelProvider.Factory{
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return super.create(modelClass)
    }
}