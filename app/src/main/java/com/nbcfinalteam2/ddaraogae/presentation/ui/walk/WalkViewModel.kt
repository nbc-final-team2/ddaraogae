package com.nbcfinalteam2.ddaraogae.presentation.ui.walk

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nbcfinalteam2.ddaraogae.domain.entity.StoreEntity
import com.nbcfinalteam2.ddaraogae.domain.usecase.GetDogListUseCase
import com.nbcfinalteam2.ddaraogae.domain.usecase.GetStoreDataUseCase
import com.nbcfinalteam2.ddaraogae.domain.usecase.GetWeatherDataUseCase
import com.nbcfinalteam2.ddaraogae.presentation.model.WalkingInfo
import com.nbcfinalteam2.ddaraogae.presentation.model.toUiModel
import com.nbcfinalteam2.ddaraogae.presentation.util.DistanceCalculator
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WalkViewModel @Inject constructor(
    private val getStoreDataUseCase: GetStoreDataUseCase,
    private val getDogListUseCase: GetDogListUseCase
) : ViewModel() {

    private val _walkUiState = MutableStateFlow(WalkUiState.init())
    val walkUiState = _walkUiState.asStateFlow()

    private val _dogSelectionState = MutableStateFlow(DogSelectionState.init())
    val dogSelectionState = _dogSelectionState.asStateFlow()

    private val _walkInfoState = MutableStateFlow(WalkingInfoState.init())
    val walkInfoState = _walkInfoState.asStateFlow()

    private val _storeListState = MutableStateFlow(StoreListState.init())
    val storeListState = _storeListState.asStateFlow()

    private var timerJob: Job? = null

    //저장된 최근 위치
    private var latitude: Double = 0.0
    private var lngtitude: Double = 0.0

    fun fetchStoreData(lat: Double, lng: Double) {
        /** 여기서 이동거리에 따른 마커 재생성 여부를 판단하는 로직을 넣습니다.*/
        //TODO: 1. 0.0일때 lat, lng 초기값이므로 1km 계산을 하면 안된다. 그러므로 값은 현재위치를
        if (latitude == 0.0 && lngtitude == 0.0) {
            getStoreData(lat, lng) /** lat, lng는 지도에서 받아온 값이며 여기로 가지고 옵니다. */
            /** 1. latitude, lngtitude는 0.0은 초기값이며 현재 위치에 대한 비교 대상 */
        } else {
            /** 현재위치와 나도 모르는 위치에 대한 1km의 거리 계산 */
            /** 2. 팀원분이 만들어주신 DistanceCalculator로 지도에서 쓰이는 lat, lng
             * api 호출시에 판단할 latitude, lngtitude를 가지고 거리를 판단합니다. */
            val distanceDiff = DistanceCalculator.getDistance(lat, lng, latitude, lngtitude)
            if (distanceDiff > 1) { /** 1은 1m를 의미합니다.*/
                /** latitude = lat lngtitude = lng,
                 * api 요청이 실패하면 위의 위치 정보와 같은 업데이트(값을 할당)를 하면 안됩니다.
                 * 이 뷰모델 로직은 조금 더 이해가 필요함.*/
                getStoreData(lat, lng)
            }
        }
    }

    private fun getStoreData(lat: Double, lng: Double) {
        viewModelScope.launch {
            try {
                val storeList =
                    getStoreDataUseCase(lat.toString(), lng.toString())
                        .orEmpty()
                        .map {
                        it.toUiModel()
                    }
                _storeListState.update { StoreListState(storeList) }
                latitude = lat
                lngtitude = lng // 이 줄에 왔다는건 성공한거니까 위치를 업데이트 해줌
            } catch (e: Exception) {
                // 예외처리
            }
        }
    }

    fun walkToggle() {
        if(_walkUiState.value.isWalking) {
            stopTimer()
        }else {
            startTimer()
        }

        _walkUiState.update { prev ->
            prev.copy(isWalking = !prev.isWalking)
        }
    }

    private fun startTimer() {
        timerJob = viewModelScope.launch {
            while(true) {
                delay(1000)
                _walkInfoState.update { prev ->
                    prev.copy(
                        walkingTime = prev.walkingTime+1
                    )
                }
            }
        }
    }

    private fun stopTimer() {
        timerJob?.cancel()
        timerJob = null
        _walkInfoState.update { prev ->
            prev.copy(
                walkingTime = 0
            )
        }
    }

    fun selectDog(dogId: String) {
        _dogSelectionState.update { prev ->
            prev.copy(
                dogList = prev.dogList.map { dogInfo ->
                    if(dogInfo.id == dogId) {
                        dogInfo.copy(isSelected = !dogInfo.isSelected)
                    }else {
                        dogInfo
                    }
                }
            )
        }
    }
}