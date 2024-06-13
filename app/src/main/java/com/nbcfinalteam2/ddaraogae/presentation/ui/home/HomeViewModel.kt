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
import java.lang.Exception
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
        }
    }

    fun loadWeather(lat: String, lon: String) {
        viewModelScope.launch {
            try {
                val weatherEntity = getWeatherDataUseCase(lat, lon)
                val weatherInfo = WeatherInfo(
                    id = weatherEntity.id.toString(),
                    temperature = "${weatherEntity.temperature}°",
                    city = weatherEntity.city ?: "Unknown",
                    condition = getConditionDescription(weatherEntity.id),
                    fineDustStatusIcon = getFineDustIcon(weatherEntity.pm10),
                    fineDustStatus = getFineDustStatus(weatherEntity.pm10),
                    ultraFineDustStatusIcon = getUltraFineDustIcon(weatherEntity.pm25),
                    ultraFineDustStatus = getUltraFineDustStatus(weatherEntity.pm25)
                )
                _weatherInfo.value = weatherInfo
            } catch (
                e: Exception
            ) {
                e.printStackTrace()
            }
        }
    }

    private fun getConditionDescription(weatherId: Long?): String {
        return when (weatherId?.toInt()) {
            in 200..232 -> R.string.weather_status_thunder.toString()
            in 300..321, in 520..531 -> R.string.weather_status_rain.toString()
            in 500..504 -> R.string.weather_status_slight_rain.toString()
            511, in 600..622 -> R.string.weather_status_snow.toString()
            701, 711, 721, 741 -> R.string.weather_status_fog.toString()
            731, 751, 761, 762 -> R.string.weather_status_dust.toString()
            in 771..781 -> R.string.weather_status_typoon.toString()
            800 -> R.string.weather_status_sunny.toString()
            801 -> R.string.weather_status_slightly_cloudy.toString()
            802 -> R.string.weather_status_cloudy.toString()
            in 803..804 -> R.string.weather_status_very_cloudy.toString()
            else -> R.string.weather_status_no_data.toString()
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

    private fun getFineDustStatus(pm10: Double?): String {
        return when {
            pm10 == null -> R.string.fine_dust_status_good.toString()
            pm10 <= 30 -> R.string.fine_dust_status_good.toString()
            pm10 <= 80 -> R.string.fine_dust_status_general.toString()
            pm10 <= 150 -> R.string.fine_dust_status_bad.toString()
            else -> R.string.fine_dust_status_very_bad.toString()
        }
    }

    private fun getUltraFineDustStatus(pm25: Double?): String {
        return when {
            pm25 == null -> R.string.fine_dust_status_good.toString()
            pm25 <= 30 -> R.string.fine_dust_status_good.toString()
            pm25 <= 80 -> R.string.fine_dust_status_general.toString()
            pm25 <= 150 -> R.string.fine_dust_status_bad.toString()
            else -> R.string.fine_dust_status_very_bad.toString()
        }
    }
}