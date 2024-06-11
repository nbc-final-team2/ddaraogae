package com.nbcfinalteam2.ddaraogae.presentation.ui.home

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nbcfinalteam2.ddaraogae.domain.usecase.GetCurrentUserUseCase
import com.nbcfinalteam2.ddaraogae.domain.usecase.GetDogListUseCase
import com.nbcfinalteam2.ddaraogae.domain.usecase.GetWalkingListByDogIdAndPeriodUseCase
import com.nbcfinalteam2.ddaraogae.domain.usecase.GetWeatherDataUseCase
import com.nbcfinalteam2.ddaraogae.presentation.model.DogInfo
import com.nbcfinalteam2.ddaraogae.presentation.model.WalkingInfo
import com.nbcfinalteam2.ddaraogae.presentation.util.DateFormatter
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getCurrentUserUseCase: GetCurrentUserUseCase,
    private val getDogListUseCase: GetDogListUseCase,
    private val getWalkingListByDogIdAndPeriodUseCase: GetWalkingListByDogIdAndPeriodUseCase,
    private val getWeatherDataUseCase: GetWeatherDataUseCase
) : ViewModel() {

    private val _dogList = MutableLiveData<List<DogInfo>>()
    val dogList: LiveData<List<DogInfo>> get() = _dogList

    private val _dogName = MutableLiveData<String>()
    val dogName: LiveData<String> get() = _dogName

    private val _selectedDogInfo = MutableLiveData<DogInfo>()
    val selectedDogInfo: LiveData<DogInfo> get() = _selectedDogInfo

    private val _walkData = MutableLiveData<List<WalkingInfo>>()
    val walkData: LiveData<List<WalkingInfo>> get() = _walkData

    private val _isWalkData = MutableLiveData<Boolean>()
    val isWalkData: LiveData<Boolean> get() = _isWalkData

    fun loadDogs() {
        viewModelScope.launch {
            val user = getCurrentUserUseCase()
            user?.let {
                val dogEntities = getDogListUseCase().orEmpty()
                val dogInfo = dogEntities.map { entity ->
                    DogInfo(
                        id = entity.id!!,
                        name = entity.name!!,
                        gender = entity.gender!!,
                        age = entity.age,
                        lineage = entity.lineage,
                        memo = entity.memo,
                        thumbnailUrl = entity.thumbnailUrl
                    )
                }
                _dogList.value = dogInfo
            }
        }
    }

    private fun selectedWalkGraphDogName(dogName: String, dogId: String) {
        viewModelScope.launch {
            _dogName.value = dogName
            loadWalkData(dogId)
        }
    }

    fun selectDog(dogInfo: DogInfo) {
        _selectedDogInfo.value = dogInfo
        selectedWalkGraphDogName(dogInfo.name, dogInfo.id)
    }

    private fun loadWalkData(dogId: String) {
        viewModelScope.launch {
            val startDate = DateFormatter.getStartDateForWeek()
            val endDate = DateFormatter.getEndDateForWeek()
            val walkEntities = getWalkingListByDogIdAndPeriodUseCase(dogId, startDate, endDate)
            val walkInfo = walkEntities.map { entity ->
                WalkingInfo(
                    id = entity.id,
                    dogId = entity.dogId,
                    timeTaken = entity.timeTaken,
                    distance = entity.distance,
                    startDateTime = entity.startDateTime,
                    endDateTime = entity.endDateTime,
                    path = entity.path
                )
            }
            _walkData.value = walkInfo
            _isWalkData.value = walkInfo.isNotEmpty()
            setDummyWalkData()
        }
    }

    private fun setDummyWalkData() = viewModelScope.launch {
        val dates = DateFormatter.getLast7Days()

        val list = dates.mapIndexed { index, date ->
            val distance = when (index) {
                0 -> 6.5
                1 -> 0.5
                2 -> 7.2
                3 -> 30.2
                4 -> 3.5
                5 -> 2.3
                6 -> 1.5
                else -> 0.0
            }
            Log.d("DummyData", "Date: $date, Distance: $distance")
            WalkingInfo(
                id = null,
                dogId = null,
                timeTaken = null,
                distance = distance,
                startDateTime = DateFormatter.testDate(date),
                endDateTime = DateFormatter.testDate(date),
                path = emptyList()
            )
        }

        _walkData.value = list
    }
}