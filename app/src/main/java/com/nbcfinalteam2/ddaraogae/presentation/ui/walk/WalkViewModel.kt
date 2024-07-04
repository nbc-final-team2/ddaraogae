package com.nbcfinalteam2.ddaraogae.presentation.ui.walk

import android.location.LocationManager
import android.net.ConnectivityManager
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nbcfinalteam2.ddaraogae.domain.usecase.GetDogListUseCase
import com.nbcfinalteam2.ddaraogae.domain.usecase.GetStoreDataUseCase
import com.nbcfinalteam2.ddaraogae.presentation.model.DogInfo
import com.nbcfinalteam2.ddaraogae.presentation.model.toUiModel
import com.nbcfinalteam2.ddaraogae.presentation.util.DistanceCalculator
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WalkViewModel @Inject constructor(
    private val getStoreDataUseCase: GetStoreDataUseCase,
    private val getDogListUseCase: GetDogListUseCase,
    private val connectivityManager: ConnectivityManager,
    private val locationManager: LocationManager
) : ViewModel() {

    private val _walkUiState = MutableStateFlow(WalkUiState.init())
    val walkUiState = _walkUiState.asStateFlow()

    private val _dogSelectionState = MutableStateFlow(DogSelectionState.init())
    val dogSelectionState = _dogSelectionState.asStateFlow()

    private val _storeListState = MutableStateFlow(StoreListState.init())
    val storeListState = _storeListState.asStateFlow()

    private val _walkEvent = MutableSharedFlow<WalkEvent>()
    val walkEvent = _walkEvent.asSharedFlow()

    //저장된 최근 위치
    private var latitude: Double = 0.0
    private var longtitude: Double = 0.0

    fun fetchDogList() {
        viewModelScope.launch {
            runCatching {
                val dogList = getDogListUseCase().map {
                    DogInfo(
                        id = it.id!!,
                        name = it.name!!,
                        gender = it.gender!!,
                        age = it.age,
                        lineage = it.lineage,
                        memo = it.memo,
                        thumbnailUrl = it.thumbnailUrl,
                        false
                    )
                }
                _dogSelectionState.update {
                    DogSelectionState(dogList)
                }
            }.onFailure {
                _walkEvent.emit(WalkEvent.Error(WalkError.LOAD_DOG_FAIL.getStrResId()))
            }

        }
    }

    fun setWalking() {
        _walkUiState.update {
            it.copy(
                isWalking = true
            )
        }
    }

    fun fetchStoreData(lat: Double, lng: Double) {
        if (latitude == 0.0 && longtitude == 0.0) {
            getStoreData(lat, lng)
            val distanceDiff = DistanceCalculator.getDistance(lat, lng, latitude, longtitude)
            if (distanceDiff > 1) {
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
                longtitude = lng
            } catch (e: Exception) {

            }
        }
    }

    fun walkToggle() = viewModelScope.launch {
        if(!_walkUiState.value.isWalking) {
            walkStart()
        } else {
            walkStop()
        }
    }

    private fun walkStart() = viewModelScope.launch {
        if(dogSelectionState.value.dogList.none { it.isSelected }) {
            _walkEvent.emit(
                WalkEvent.Error(WalkError.NO_SELECTED_DOG.getStrResId())
            )
        } else if(connectivityManager.activeNetwork == null) {
            _walkEvent.emit(
                WalkEvent.Error(WalkError.NO_NETWORK_CONNECTION.getStrResId())
            )
        } else if(!locationManager.isLocationEnabled) {
            _walkEvent.emit(
                WalkEvent.Error(WalkError.NO_LOCATION_CONNECTION.getStrResId())
            )
        } else {
            _walkUiState.update {
                it.copy(isWalking = true)
            }
            _walkEvent.emit(
                WalkEvent.StartWalking
            )
        }
    }

    private fun walkStop() = viewModelScope.launch {
        _walkUiState.update {
            it.copy(isWalking = false)
        }
        _walkEvent.emit(
            WalkEvent.StopWalking
        )
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

    fun setLoading() {
        _walkUiState.update {
            it.copy(
                isLoading = true
            )
        }
    }

    fun releaseLoading() {
        _walkUiState.update {
            it.copy(
                isLoading = false
            )
        }
    }
}