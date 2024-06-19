package com.nbcfinalteam2.ddaraogae.presentation.ui.home

import android.content.Context
import androidx.core.content.ContextCompat
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nbcfinalteam2.ddaraogae.R
import com.nbcfinalteam2.ddaraogae.domain.usecase.GetDogListUseCase
import com.nbcfinalteam2.ddaraogae.domain.usecase.GetWalkingListByDogIdAndPeriodUseCase
import com.nbcfinalteam2.ddaraogae.domain.usecase.GetWeatherDataUseCase
import com.nbcfinalteam2.ddaraogae.presentation.model.DogInfo
import com.nbcfinalteam2.ddaraogae.presentation.model.WalkingInfo
import com.nbcfinalteam2.ddaraogae.presentation.model.WeatherInfo
import com.nbcfinalteam2.ddaraogae.presentation.util.DateFormatter
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getDogListUseCase: GetDogListUseCase,
    private val getWalkingListByDogIdAndPeriodUseCase: GetWalkingListByDogIdAndPeriodUseCase,
    private val getWeatherDataUseCase: GetWeatherDataUseCase,
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val _dogListState = MutableStateFlow<List<DogInfo>>(emptyList())
    val dogListState : StateFlow<List<DogInfo>> = _dogListState.asStateFlow()

    private val _selectDogState = MutableStateFlow<DogInfo?>(null)
    val selectDogState = _selectDogState.asStateFlow()

    private val _walkListState = MutableStateFlow<List<WalkingInfo>>(emptyList())
    val walkListState = _walkListState.asStateFlow()

    private val _weatherInfoState = MutableStateFlow<WeatherInfo?>(null)
    val weatherInfoState = _weatherInfoState.asStateFlow()


    init {
        loadDogs()
    }

    private fun loadDogs() = viewModelScope.launch {
        runCatching {
            try {
                val dogEntities = getDogListUseCase()
                val dogInfo = dogEntities.mapIndexed { ind, dogEntity ->
                    DogInfo(
                        id = dogEntity.id ?: "",
                        name = dogEntity.name ?: "",
                        gender = dogEntity.gender ?: 0,
                        age = dogEntity.age,
                        lineage = dogEntity.lineage,
                        memo = dogEntity.memo,
                        thumbnailUrl = dogEntity.thumbnailUrl,
                        isSelected = ind == 0
                    )
                }
                _dogListState.update{
                    dogInfo
                }
                _selectDogState.update {
                    _dogListState.value.firstOrNull()
                }

            } catch (exception: Exception) {
                exception.printStackTrace()
            }
        }
    }


    fun refreshDogList() = viewModelScope.launch {
        runCatching {
            var selectedDogInd: Int? = null

            val dogList = getDogListUseCase().mapIndexed { ind, dogEntity ->
                DogInfo(
                    id = dogEntity.id,
                    name = dogEntity.name,
                    gender = dogEntity.gender,
                    age = dogEntity.age,
                    lineage = dogEntity.lineage,
                    memo = dogEntity.memo,
                    thumbnailUrl = dogEntity.thumbnailUrl,
                    isSelected = selectDogState.value?.let {
                        if(it.id == dogEntity.id) {
                            selectedDogInd = ind
                            true
                        } else {
                            false
                        }
                    }?:false
                )
            }.toMutableList()

            _dogListState.update {
                selectedDogInd?.let {
                    dogList
                }?:dogList.apply { if(this.isNotEmpty()) this[0].isSelected=true }
            }
            _selectDogState.update {
                selectedDogInd?.let {
                    dogList[it]
                }?: dogList.firstOrNull()
            }
        }
    }

    fun loadSelectedDogWalkGraph() {
        viewModelScope.launch {
            try {
                selectDogState.value?.id?.let { dogId ->
                    val startDate = DateFormatter.getStartDateForWeek()
                    val endDate = DateFormatter.getEndDateForWeek()
                    val walkEntities = getWalkingListByDogIdAndPeriodUseCase(dogId, startDate, endDate)
                    val walkInfo = walkEntities.map {
                        WalkingInfo(
                            id = it.id,
                            dogId = it.dogId,
                            timeTaken = it.timeTaken,
                            distance = it.distance,
                            startDateTime = it.startDateTime,
                            endDateTime = it.endDateTime,
                            walkingImage = it.walkingImage
                        )
                    }
                    _walkListState.value = walkInfo
                }
            } catch (exception: Exception) {
                exception.printStackTrace()
            }
        }
    }

    fun selectDog(dogInfo: DogInfo) {
        _selectDogState.value = dogInfo
        _dogListState.value = dogListState.value.orEmpty().map {
            it.copy(
                isSelected = dogInfo.id == it.id
            )
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
                _weatherInfoState.value = weatherInfo
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun getConditionDescription(weatherId: Long?): String {
        return when (weatherId?.toInt()) {
            in 200..232 -> ContextCompat.getString(context, R.string.weather_status_thunder)
            in 300..321, in 520..531 -> ContextCompat.getString(
                context,
                R.string.weather_status_rain
            )
            in 500..504 -> ContextCompat.getString(context, R.string.weather_status_slight_rain)
            511, in 600..622 -> ContextCompat.getString(context, R.string.weather_status_snow)
            701, 711, 721, 741 -> ContextCompat.getString(context, R.string.weather_status_fog)
            731, 751, 761, 762 -> ContextCompat.getString(context, R.string.weather_status_dust)
            in 771..781 -> ContextCompat.getString(context, R.string.weather_status_typoon)
            800 -> ContextCompat.getString(context, R.string.weather_status_sunny)
            801 -> ContextCompat.getString(context, R.string.weather_status_slightly_cloudy)
            802 -> ContextCompat.getString(context, R.string.weather_status_cloudy)
            in 803..804 -> ContextCompat.getString(context, R.string.weather_status_very_cloudy)
            else -> ContextCompat.getString(context, R.string.weather_status_no_data)
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
            pm10 == null -> ContextCompat.getString(context, R.string.fine_dust_status_good)
            pm10 <= 30 -> ContextCompat.getString(context, R.string.fine_dust_status_good)
            pm10 <= 80 -> ContextCompat.getString(context, R.string.fine_dust_status_general)
            pm10 <= 150 -> ContextCompat.getString(context, R.string.fine_dust_status_bad)
            else -> ContextCompat.getString(context, R.string.fine_dust_status_very_bad)
        }
    }

    private fun getUltraFineDustStatus(pm25: Double?): String {
        return when {
            pm25 == null -> ContextCompat.getString(context, R.string.fine_dust_status_good)
            pm25 <= 30 -> ContextCompat.getString(context, R.string.fine_dust_status_good)
            pm25 <= 80 -> ContextCompat.getString(context, R.string.fine_dust_status_general)
            pm25 <= 150 -> ContextCompat.getString(context, R.string.fine_dust_status_bad)
            else -> ContextCompat.getString(context, R.string.fine_dust_status_very_bad)
        }
    }
}