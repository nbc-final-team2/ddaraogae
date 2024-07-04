package com.nbcfinalteam2.ddaraogae.presentation.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nbcfinalteam2.ddaraogae.R
import com.nbcfinalteam2.ddaraogae.domain.usecase.GetDogListUseCase
import com.nbcfinalteam2.ddaraogae.domain.usecase.GetStampNumByPeriodUseCase
import com.nbcfinalteam2.ddaraogae.domain.usecase.GetWalkingListByDogIdAndPeriodUseCase
import com.nbcfinalteam2.ddaraogae.domain.usecase.GetWeatherDataUseCase
import com.nbcfinalteam2.ddaraogae.presentation.model.DefaultEvent
import com.nbcfinalteam2.ddaraogae.presentation.model.DogInfo
import com.nbcfinalteam2.ddaraogae.presentation.model.WalkingInfo
import com.nbcfinalteam2.ddaraogae.presentation.model.WeatherInfo
import com.nbcfinalteam2.ddaraogae.presentation.util.DateFormatter
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getDogListUseCase: GetDogListUseCase,
    private val getWalkingListByDogIdAndPeriodUseCase: GetWalkingListByDogIdAndPeriodUseCase,
    private val getWeatherDataUseCase: GetWeatherDataUseCase,
    private val getStampNumByPeriodUseCase: GetStampNumByPeriodUseCase
) : ViewModel() {

    private val _homeUiState = MutableStateFlow(HomeUiState())
    val homeUiState: StateFlow<HomeUiState> = _homeUiState.asStateFlow()

    private val _loadDogEvent = MutableSharedFlow<DefaultEvent>()
    val loadDogEvent: SharedFlow<DefaultEvent> = _loadDogEvent.asSharedFlow()

    private val _updateDogEvent = MutableSharedFlow<DefaultEvent>()
    val updateDogEvent: SharedFlow<DefaultEvent> = _updateDogEvent.asSharedFlow()

    private val _loadWeatherEvent = MutableSharedFlow<DefaultEvent>()
    val loadWeatherEvent: SharedFlow<DefaultEvent> = _loadWeatherEvent.asSharedFlow()

    private val _loadWalkDataEvent = MutableSharedFlow<DefaultEvent>()
    val loadWalkDataEvent: SharedFlow<DefaultEvent> = _loadWalkDataEvent.asSharedFlow()

    private val _loadStampEvent = MutableSharedFlow<DefaultEvent>()
    val loadStampEvent: SharedFlow<DefaultEvent> = _loadStampEvent.asSharedFlow()

    init {
        loadDogs()
        loadStampProgress()
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
                _homeUiState.update {
                    it.copy(
                        dogList = dogInfo,
                        selectedDog = dogInfo.firstOrNull()
                    )
                }
                _loadDogEvent.emit(DefaultEvent.Success)
            } catch (exception: Exception) {
                exception.printStackTrace()
                _loadDogEvent.emit(DefaultEvent.Failure(R.string.msg_load_dog_fail))
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
                    isSelected = _homeUiState.value.selectedDog?.let {
                        if (it.id == dogEntity.id) {
                            selectedDogInd = ind
                            true
                        } else {
                            false
                        }
                    } ?: false
                )
            }.toMutableList()

            _homeUiState.update {
                it.copy(
                    dogList = selectedDogInd?.let {
                        dogList
                    } ?: dogList.apply { if (this.isNotEmpty()) this[0].isSelected = true },
                    selectedDog = selectedDogInd?.let { dogList[it] } ?: dogList.firstOrNull()
                )
            }
        }.onSuccess {
            _updateDogEvent.emit(DefaultEvent.Success)
        }.onFailure {
            _updateDogEvent.emit(DefaultEvent.Failure(R.string.msg_load_changes_fail))
        }
    }

    fun loadSelectedDogWalkGraph() {
        viewModelScope.launch {
            try {
                homeUiState.value.selectedDog?.id?.let { dogId ->
                    val startDate = DateFormatter.getStartDateForWeek()
                    val endDate = DateFormatter.getEndDateForWeek()
                    val walkEntities =
                        getWalkingListByDogIdAndPeriodUseCase(dogId, startDate, endDate)
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
                    }.sortedBy { it.endDateTime }
                    _homeUiState.update {
                        it.copy(
                            walkList = walkInfo
                        )
                    }
                    _loadWalkDataEvent.emit(DefaultEvent.Success)
                }
            } catch (exception: Exception) {
                exception.printStackTrace()
                _loadWalkDataEvent.emit(DefaultEvent.Failure(R.string.msg_load_walking_data_fail))
            }
        }
    }

    fun selectDog(dogInfo: DogInfo) {
        _homeUiState.update {
            it.copy(
                selectedDog = dogInfo,
                dogList = it.dogList.map {
                    it.copy(isSelected = dogInfo.id == it.id)
                }
            )
        }
    }

    fun loadWeather(lat: String, lon: String) {
        viewModelScope.launch {
            try {
                val weatherEntity = getWeatherDataUseCase(lat, lon)
                val weatherInfo = WeatherInfo(
                    id = weatherEntity.id,
                    temperature = "${weatherEntity.temperature}°",
                    city = weatherEntity.city ?: "Unknown",
                    condition = getConditionDescription(weatherEntity.id),
                    fineDustStatusIcon = getFineDustIcon(weatherEntity.pm10),
                    fineDustStatus = getFineDustStatus(weatherEntity.pm10),
                    ultraFineDustStatusIcon = getUltraFineDustIcon(weatherEntity.pm25),
                    ultraFineDustStatus = getUltraFineDustStatus(weatherEntity.pm25)
                )

                _homeUiState.update {
                    it.copy(
                        weatherInfo = weatherInfo
                    )
                }
                _loadWeatherEvent.emit(DefaultEvent.Success)

            } catch (e: Exception) {
                e.printStackTrace()
                _loadWeatherEvent.emit(DefaultEvent.Failure(R.string.msg_load_weather_fail))
            }
        }
    }

    private fun getConditionDescription(weatherId: Long?): Int {
        return when (weatherId?.toInt()) {
            in 200..232 -> R.string.weather_status_thunder
            in 300..321, in 520..531 -> R.string.weather_status_rain
            in 500..504 -> R.string.weather_status_slight_rain
            511, in 600..622 -> R.string.weather_status_snow
            701, 711, 721, 741 -> R.string.weather_status_fog
            731, 751, 761, 762 -> R.string.weather_status_dust
            in 771..781 -> R.string.weather_status_typoon
            800 -> R.string.weather_status_sunny
            801 -> R.string.weather_status_slightly_cloudy
            802 -> R.string.weather_status_cloudy
            in 803..804 -> R.string.weather_status_very_cloudy
            else -> R.string.weather_status_no_data
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

    private fun getFineDustStatus(pm10: Double?): Int {
        return when {
            pm10 == null -> R.string.fine_dust_status_good
            pm10 <= 30 -> R.string.fine_dust_status_good
            pm10 <= 80 -> R.string.fine_dust_status_general
            pm10 <= 150 -> R.string.fine_dust_status_bad
            else -> R.string.fine_dust_status_very_bad
        }
    }

    private fun getUltraFineDustStatus(pm25: Double?): Int {
        return when {
            pm25 == null -> R.string.fine_dust_status_good
            pm25 <= 30 -> R.string.fine_dust_status_good
            pm25 <= 80 -> R.string.fine_dust_status_general
            pm25 <= 150 -> R.string.fine_dust_status_bad
            else -> R.string.fine_dust_status_very_bad
        }
    }

    fun loadStampProgress() = viewModelScope.launch {
        runCatching {
            val startDate = DateFormatter.getCurrentMonday()
            val endDate = DateFormatter.getEndDateForWeek()
            val stampNum = getStampNumByPeriodUseCase(startDate, endDate)
            _homeUiState.update {
                it.copy(
                    stampProgress = stampNum
                )
            }
        }.onSuccess {
            _loadStampEvent.emit(DefaultEvent.Success)
        }.onFailure {
            _loadStampEvent.emit(DefaultEvent.Failure(R.string.msg_load_walk_stamp_data_fail))
        }
    }
}