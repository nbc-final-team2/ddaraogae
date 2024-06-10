package com.nbcfinalteam2.ddaraogae.presentation.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nbcfinalteam2.ddaraogae.domain.entity.DogEntity
import com.nbcfinalteam2.ddaraogae.domain.usecase.GetCurrentUserUseCase
import com.nbcfinalteam2.ddaraogae.domain.usecase.GetDogListUseCase
import com.nbcfinalteam2.ddaraogae.presentation.model.DogInfo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getCurrentUserUseCase: GetCurrentUserUseCase,
    private val getDogListUseCase: GetDogListUseCase
) : ViewModel() {
    // UI용 모델 만들어서 넣을것
    private val _dogList = MutableLiveData<List<DogInfo>>()
    val dogList: LiveData<List<DogInfo>> get() = _dogList

    private val _dogName = MutableLiveData<String>()
    val dogName: LiveData<String> get() = _dogName

    /** 유저데이터 함수로 뺴서 한번에 검사 */

    fun loadDogs() {
        viewModelScope.launch {
            val user = getCurrentUserUseCase()
            user?.let {
                val dogEntities = getDogListUseCase().orEmpty()
                val dogInfo = dogEntities.map { entitiy ->
                    DogInfo(
                        id = entitiy.id,
                        name = entitiy.name,
                        gender = entitiy.gender,
                        age = entitiy.age,
                        lineage = entitiy.lineage,
                        memo = entitiy.memo,
                        thumbnailUrl = entitiy.thumbnailUrl
                    )
                }
                _dogList.value = dogInfo
            }
        }
    }

    fun selectedWalkGraphDogName(dogName: String) {
        viewModelScope.launch {
            _dogName.value = dogName
        }
    }

    fun loadTodayWeather() {

    }
}

