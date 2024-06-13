package com.nbcfinalteam2.ddaraogae.presentation.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nbcfinalteam2.ddaraogae.domain.usecase.GetWalkingListByDogIdAndPeriodUseCase
import com.nbcfinalteam2.ddaraogae.presentation.model.DogInfo
import com.nbcfinalteam2.ddaraogae.presentation.model.WalkingInfo
import com.nbcfinalteam2.ddaraogae.presentation.util.DateFormatter
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HistoryViewModel @Inject constructor(
    private val getWalkingListByDogIdAndPeriodUseCase: GetWalkingListByDogIdAndPeriodUseCase
) : ViewModel() {

    private val _selectedDate = MutableLiveData<String>()
    val selectedDate: LiveData<String> get() = _selectedDate

    private val _dogInfo = MutableLiveData<DogInfo>()
    val dogInfo: LiveData<DogInfo> get() = _dogInfo

    private val _walkData = MutableLiveData<List<WalkingInfo>>()
    val walkData: LiveData<List<WalkingInfo>> get() = _walkData

    private var selectedYear: Int = 0
    private var selectedMonth: Int = 0

    fun setSelectedDate(year: Int, month: Int) {
        selectedYear = year
        selectedMonth = month
        _selectedDate.value = "${year}년 ${month}월"
        loadWalkData(year, month)
    }

    fun setDogInfo(dog: DogInfo) {
        _dogInfo.value = dog
    }

    private fun loadWalkData(year: Int, month: Int) {
        viewModelScope.launch {
            val startDate = DateFormatter.getStartDateForAllDay(year, month)
            val endDate = DateFormatter.getEndDateForAllDay(year, month)

            val dogId = _dogInfo.value?.id ?: "처리할것"
            val walkEntities = getWalkingListByDogIdAndPeriodUseCase(dogId, startDate, endDate)
            val walkInfo = walkEntities.map { entity ->
                WalkingInfo(
                    id = entity.id,
                    dogId = entity.dogId,
                    timeTaken = entity.timeTaken,
                    distance = entity.distance,
                    startDateTime = entity.startDateTime,
                    endDateTime = entity.endDateTime,
                    walkingImage = entity.walkingImage
                )
            }
            _walkData.value = walkInfo
        }
    }

    fun getSelectedYearMonth(): Pair<Int, Int> {
        return Pair(selectedYear, selectedMonth)
    }
}