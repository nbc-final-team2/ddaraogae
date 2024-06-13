package com.nbcfinalteam2.ddaraogae.presentation.ui.walk

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nbcfinalteam2.ddaraogae.domain.entity.StoreEntity
import com.nbcfinalteam2.ddaraogae.domain.usecase.GetStoreDataUseCase
import com.nbcfinalteam2.ddaraogae.domain.usecase.GetWeatherDataUseCase
import com.nbcfinalteam2.ddaraogae.presentation.util.DistanceCalculator
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WalkTestViewModel @Inject constructor(
    private val getStoreDataUseCase: GetStoreDataUseCase,
    private val getWeatherUseCase: GetWeatherDataUseCase

) : ViewModel() {
    private val _storeData = MutableLiveData<List<StoreEntity?>?>()
    val storeData: LiveData<List<StoreEntity?>?> = _storeData

    //거리 체크
    private val _distanceData = MutableLiveData<Double>()
    val distanceData: LiveData<Double> = _distanceData

    //현재 위치
    private var latitude: Double = 0.0
    private var lngtitude: Double = 0.0

    //시작 위치
    private var initLatitude: Double = 0.0
    private var initLngtitude: Double = 0.0

    //이동 거리
    private var walkDistance : Double = 0.0

    fun fetchStoreData(lat: Double, lng: Double) {
        /** 여기서 이동거리에 따른 마커 재생성 여부를 판단하는 로직을 넣습니다.*/
        //TODO: 1. 0.0일때 lat, lng 초기값이므로 1km 계산을 하면 안된다. 그러므로 값은 현재위치를
        if (latitude == 0.0 && lngtitude == 0.0) {
            getStoreData(lat, lng) /** lat, lng는 지도에서 받아온 값이며 여기로 가지고 옵니다. */
            /** 1. latitude, lngtitude는 0.0은 초기값이며 현재 위치에 대한 비교 대상 */

            //시작 위치 저장
            initLatitude = lat
            initLngtitude = lng

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

            //값이 들어올 때마다 움직인 거리를 계산해서 계속 더함
            val checkWalkDistance = DistanceCalculator.getDistance(lat, lng, initLatitude, initLngtitude)
            walkDistance += checkWalkDistance

            //총 이동 거리
            _distanceData.value = walkDistance
        }
    }

    private fun getStoreData(lat: Double, lng: Double) {
        viewModelScope.launch {
            try {
                val storeDataList =
                    getStoreDataUseCase( // api 통신
                        lat.toString(),
                        lng.toString()
                    ) // 여기서 toSting 해주면서 프래그먼트에서도 toString 지워줌
                _storeData.value = storeDataList
                latitude = lat
                lngtitude = lng // 이 줄에 왔다는건 성공한거니까 위치를 업데이트 해줌
            } catch (e: Exception) {
                // 예외처리
            }
        }
    }
}