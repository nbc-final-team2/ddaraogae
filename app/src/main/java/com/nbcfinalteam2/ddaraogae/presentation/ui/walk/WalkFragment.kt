package com.nbcfinalteam2.ddaraogae.presentation.ui.walk

import android.Manifest
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatButton
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.graphics.red
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.CameraPosition
import com.naver.maps.map.CameraUpdate
import com.naver.maps.map.LocationTrackingMode
import com.naver.maps.map.MapFragment
import com.naver.maps.map.NaverMap
import com.naver.maps.map.overlay.InfoWindow
import com.naver.maps.map.overlay.Marker
import com.naver.maps.map.util.FusedLocationSource
import com.nbcfinalteam2.ddaraogae.R
import com.nbcfinalteam2.ddaraogae.databinding.FragmentWalkBinding
import com.nbcfinalteam2.ddaraogae.domain.entity.StoreEntity
import com.nbcfinalteam2.ddaraogae.presentation.service.LocationService
import com.nbcfinalteam2.ddaraogae.presentation.ui.main.MainActivity
import dagger.hilt.android.AndroidEntryPoint
import java.util.Timer

@AndroidEntryPoint
class WalkFragment : Fragment() {
    //runOnUiThread 사용을 위함
    private lateinit var walkActivity: MainActivity

    /** SearchApi 데이터를 사용하기 위해 임의로 TestViewModel을 참고하여, WalkTestViewModel을 만들었습니다.*/
    private val viewModel: WalkTestViewModel by viewModels()
    private var _binding: FragmentWalkBinding? = null
    private val binding get() = _binding!!

    private val LOCATION_PERMISSION_REQUEST_CODE = 5000
    private val PERMISSIONS = arrayOf(
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_COARSE_LOCATION
    )
    private lateinit var naverMap: NaverMap
    private lateinit var locationSource: FusedLocationSource // callback, providerclient 필요가 없었다.
    private lateinit var cameraPosition: CameraPosition
    private lateinit var cameraUpdate: CameraUpdate /*TODO: 나중에 animate을 위해 남김*/

    private var locationService: LocationService? = null
    private var bound = false
    private var isServiceRunning = false // 버튼을 토글하여 서비스가 run 중인지 판별하기 위함.

    private var markerList = mutableListOf<Marker>()

    //timer 계산을 위한 변수들..
    private var totalDistance = "0.0"
    private var timerTask: Timer? = null
    private var time = 0
    private var totalWalkTime = ""
    private var hour = 0
    private var min = 0
    private var sec = 0

    private val serviceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            val binder = service as LocationService.LocalBinder
            locationService = binder.getService()
            bound = true
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            locationService = null
            bound = false
        }
    }
    //TODO: 버튼애 리스너를 달아놓고 bind됫는지 확인하고 값을 가져온다, 서비스
    //값 가져오고, map이나 다른 함수로 변환해도된다.

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentWalkBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
//        if (ActivityCompat.checkSelfPermission(
//                requireContext(),
//                Manifest.permission.ACCESS_FINE_LOCATION
//            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
//                requireContext(),
//                Manifest.permission.ACCESS_COARSE_LOCATION
//            ) != PackageManager.PERMISSION_GRANTED
//        ) {
//            return
//        }
        if (ActivityCompat.checkSelfPermission(
                requireActivity(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                requireActivity(),
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }


        if (!hasPermission()) {
            ActivityCompat.requestPermissions(
                requireActivity(),
                PERMISSIONS,
                LOCATION_PERMISSION_REQUEST_CODE
            )
        } else {
            initMapView()
        }

        binding.btnWalkStart.setOnClickListener {
            startLocationService() // 트래킹 활성
            startTimer() // 타이머 시작
        }
        binding.ibWalkStop.setOnClickListener {
            endLocationService() // 트래킹 비활성화
            timerTask?.cancel() // 타이머 멈춤
        }
        observeStoreData() // 마커를 새로 그리기(맵뷰애 있으면 안됨)
    }

    //runOnUiThread 사용을 위함22
    override fun onAttach(context: Context) {
        super.onAttach(context)
        walkActivity = context as MainActivity
    }

    override fun onStart() {
        super.onStart()
        if (!bound) {
            Intent(requireContext(), LocationService::class.java).also { intent ->
                requireContext().bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE)
            }
            bound = true
        }
    }

    override fun onStop() {
        super.onStop()
        if (bound) {
            requireContext().unbindService(serviceConnection)
            bound = false
        }
    }

    private fun initMapView() {
        val fm = childFragmentManager
        val mapFragment = fm.findFragmentById(R.id.fragment_walk) as MapFragment?
            ?: MapFragment.newInstance().also {
                fm.beginTransaction().add(R.id.fragment_walk, it).commit()
            }

        locationSource = FusedLocationSource(this, LOCATION_PERMISSION_REQUEST_CODE)
        // fragment의 getMapAsync() 메서드로 OnMapReadyCallback 콜백을 등록하면 비동기로 NaverMap 객체를 얻을 수 있다.
        mapFragment.getMapAsync { map ->
            naverMap = map
            // 현재 위치 활성화
            naverMap.locationSource = locationSource
            // 현재 위치 버튼 기능
            naverMap.uiSettings.isLocationButtonEnabled = true
            // 위치를 추적하면서 카메라도 따라 움직인다.
            naverMap.locationTrackingMode = LocationTrackingMode.Face
            //
            naverMap.locationOverlay.iconWidth = 60
            naverMap.locationOverlay.iconHeight = 60
            naverMap.locationOverlay
            // 카메라 설정
            naverMap.addOnLocationChangeListener {
                setCamera(it)
                viewModel.fetchStoreData(it.latitude, it.longitude) // 위치 데이터 가져오기(꼭 있어야함)
                observeDistance()// 거리 측정하기
            }
        }
    }

    // hasPermission()에서는 위치 권한이 있을 경우 true를, 없을 경우 false를 반환한다.
    private fun hasPermission(): Boolean {
        for (permission in PERMISSIONS) {
            if (ContextCompat.checkSelfPermission(requireActivity(), permission)
                != PackageManager.PERMISSION_GRANTED
            ) {
                return false
            }
        }
        return true
    }

    private fun setCamera(lastLocation: Location) {
        val latLng = LatLng(lastLocation.latitude, lastLocation.longitude)
        cameraPosition = CameraPosition(latLng, 15.0)
    }

    private fun endLocationService() {

        // 서비스에서 리스트 받아오기
        val locationList = locationService?.locationList?.map {
            LatLng(it.latitude, it.longitude)
        }.orEmpty().toTypedArray() // Null되면 빈 리스트가 들어감
        // 종료 할 때
        locationService?.stopService()
        requireActivity().unbindService(serviceConnection)
        bound = false
        sendLocationListToFinishActivity(locationList) // FinishActivirty로 list 보내기
        isServiceRunning = false

    }

    private fun startLocationService() {
        binding.btnWalkStart.visibility = AppCompatButton.INVISIBLE
        binding.clWalkController.visibility = ConstraintLayout.VISIBLE

        val intent = Intent(requireContext(), LocationService::class.java)
        requireActivity().startForegroundService(intent)
        if (!bound) {
            Intent(requireContext(), LocationService::class.java).also { intent ->
                requireContext().bindService(
                    intent,
                    serviceConnection,
                    Context.BIND_AUTO_CREATE
                )
            }
            bound = true
        }
        isServiceRunning = true

    }

    /** 좋은 방법인지는 모르겠으나 서비스 도입하면서 이렇게 Intent를 보내게 되었음. */
    private fun sendLocationListToFinishActivity(locationList: Array<LatLng>) {
        val intent = Intent(requireContext(), FinishActivity::class.java).apply {
            putExtra("locationList", locationList)
            putExtra("time", totalWalkTime)
            putExtra("distance", totalDistance)
        }
        startActivity(intent)
    }

    private fun observeStoreData() {
        viewModel.storeData.observe(viewLifecycleOwner) { storeDataList ->
            Log.d("walk frag", storeDataList.toString())
            storeDataList?.let { stores ->
                clearMarkers()
                addMarkers(stores)
            }
        }
    }
    private fun observeDistance(){
        viewModel.distanceData.observe(viewLifecycleOwner) { storeDataList ->
            Log.d("walk frag", storeDataList.toString())
            totalDistance = (String.format("%.1f", storeDataList))
        }
    }

    private fun clearMarkers() {
        markerList.forEach { it.map = null }
        markerList.clear()
    }

    private fun addMarkers(stores: List<StoreEntity?>) {
        stores.forEach { store ->
            store?.let { storeEntity ->
                val latLng = LatLng(storeEntity.lat!!.toDouble(), storeEntity.lng!!.toDouble())
                val marker = Marker()
                marker.width = Marker.SIZE_AUTO
                marker.height = 60 /** 적절한 크기 찾아야하는데..*/
                marker.position = latLng
                marker.map = naverMap
                markerList.add(marker)

                val contentString = """
                ${storeEntity.placeName} | ${storeEntity.categoryGroupName}
                    ${storeEntity.address}
                    ${storeEntity.phone} 
                """.trimIndent()

                val infoWindow = InfoWindow().apply {
                    adapter = object : InfoWindow.DefaultTextAdapter(requireContext()) {
                        override fun getText(infoWindow: InfoWindow): CharSequence {
                            return contentString
                        }
                    }
                }

                marker.setOnClickListener {
                    if (infoWindow.isAdded) {
                        infoWindow.close()
                    } else {
                        infoWindow.open(marker)
                    }
                    true
                }
            }
        }
    }

    private fun startTimer(){
        //kotlin에서 제공하는 타이머 사용
        //백그라운드 스레드에서 동작, 기본적으로 ui조작이 불가능 하지만 runOnUiThread를 호출하면 가능
        timerTask = kotlin.concurrent.timer(period = 1000){//1초마다 작동
            time++

            //시간 구하기
            hour = time/3600
            min = (time /60)%60
            sec = time % 60

            //한자리 수 일땐 앞에 0을 붙이기 위함
            val tHour = "%02d".format(hour)
            val tMin = "%02d".format(min)
            val tSec = "%02d".format(sec)

            //1시간 이상 측정 시 시간 단위로 표현
            if(hour >= 1) totalWalkTime = "${tHour}시간  ${tMin}분"
            else totalWalkTime = "${tMin}분 ${tSec}초"

            //intent로 넘기기 위해 저장
            totalWalkTime = "$totalWalkTime"

            //ui수정
            walkActivity.runOnUiThread{
                binding.tvWalkTime.text = "$totalWalkTime"
                binding.tvWalkDistance.text = "$totalDistance km"
            }

        }
    }
}



