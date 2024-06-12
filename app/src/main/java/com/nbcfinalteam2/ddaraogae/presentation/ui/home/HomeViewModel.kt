package com.nbcfinalteam2.ddaraogae.presentation.ui.home

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nbcfinalteam2.ddaraogae.R
import com.nbcfinalteam2.ddaraogae.domain.usecase.GetCurrentUserUseCase
import com.nbcfinalteam2.ddaraogae.domain.usecase.GetDogListUseCase
import com.nbcfinalteam2.ddaraogae.domain.usecase.GetWalkingListByDogIdAndPeriodUseCase
import com.nbcfinalteam2.ddaraogae.domain.usecase.GetWeatherDataUseCase
import com.nbcfinalteam2.ddaraogae.presentation.model.DogInfo
import com.nbcfinalteam2.ddaraogae.presentation.model.WalkingInfo
import com.nbcfinalteam2.ddaraogae.presentation.model.WeatherInfo
import com.nbcfinalteam2.ddaraogae.presentation.util.DateFormatter
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.math.log10

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

    private val _weatherInfo = MutableLiveData<WeatherInfo>()
    val weatherInfo: LiveData<WeatherInfo> get() = _weatherInfo

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

    fun loadWeather(lat: String, lon: String) {
        viewModelScope.launch {
            Log.d("lat", lat)
            Log.d("l", lat)
//            val weatherEntity = getWeatherDataUseCase(lat, lon)
//            val weatherInfo = WeatherInfo(
//                id = weatherEntity.id.toString(),
//                temperature = "${weatherEntity.temperature}°",
//                city = weatherEntity.city ?: "Unknown",
//                condition = getConditionDescription(weatherEntity.id),
//                fineDustStatusIcon = getFineDustIcon(weatherEntity.pm10),
//                ultraFineDustStatusIcon = getUltraFineDustIcon(weatherEntity.pm25)
//            )
//            _weatherInfo.value = weatherInfo
        }
    }

    private fun getConditionDescription(weatherId: Long?): String {
        return when (weatherId?.toInt()) {
            in 200..232 -> "천둥번개"
            in 300..321, in 520..531 -> "비"
            in 500..504 -> "약간 비"
            511, in 600..622 -> "눈"
            701, 711, 721, 741 -> "안개"
            731, 751, 761, 762 -> "황사"
            in 771..781 -> "태풍"
            800 -> "맑음"
            801 -> "약간 흐림"
            802 -> "흐림"
            in 803..804 -> "많이 흐림"
            else -> "날씨 정보 없음"
        }
    }

    private fun getFineDustIcon(pm10: Double?): Int {
        return when {
            pm10 == null -> R.drawable.ic_weather_condition_good
            pm10 <= 30 -> R.drawable.ic_weather_condition_good
            pm10 <= 80 -> R.drawable.ic_weather_condition_normal
            pm10 <= 150 -> R.drawable.ic_weather_condition_bad
            else -> R.drawable.ic_weather_condition_very_bad
        }
    }

    private fun getUltraFineDustIcon(pm25: Double?): Int {
        return when {
            pm25 == null -> R.drawable.ic_weather_condition_good
            pm25 <= 30 -> R.drawable.ic_weather_condition_good
            pm25 <= 80 -> R.drawable.ic_weather_condition_normal
            pm25 <= 150 -> R.drawable.ic_weather_condition_bad
            else -> R.drawable.ic_weather_condition_very_bad
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