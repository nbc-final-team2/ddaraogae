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

    /** 유저데이터 함수로 뺴서 한번에 검사 */

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
    /** dogid 추가한것 확인할것 */
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
            val walkInfo = walkEntities.map {entity ->
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

    fun setDummyWalkData() = viewModelScope.launch {
        // 최근 7일간의 날짜를 가져옴
        val dates = DateFormatter.getLast7Days()

        // 각 날짜에 산책 거리를 할당
        val list = dates.mapIndexed { index, date ->
            val distance = when (index) {
                0 -> 1.2
                1 -> 0.5
                2 -> 0.1
                3 -> 3.2
                4 -> 1.0
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

