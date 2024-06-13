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
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
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
import com.nbcfinalteam2.ddaraogae.presentation.service.LocationService
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class WalkFragment : Fragment() {

    /** SearchApi 데이터를 사용하기 위해 임의로 TestViewModel을 참고하여, WalkTestViewModel을 만들었습니다.*/
    private val walkViewModel: WalkViewModel by viewModels()
    private var _binding: FragmentWalkBinding? = null
    private val binding get() = _binding!!

    private val walkDogAdapter by lazy {
        WalkDogAdapter { walkViewModel.selectDog(it) }
    }

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
    private var isServiceRunning = false
    private var serviceDistanceStateFlow: StateFlow<Double>? = null

    private var markerList = mutableListOf<Marker>()

    private val serviceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            val binder = service as LocationService.LocalBinder
            locationService = binder.getService()
            serviceDistanceStateFlow = locationService!!.getDistanceFlow()
            bound = true
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            locationService = null
            serviceDistanceStateFlow = null
            bound = false
        }
    }
    //TODO: 버튼애 리스너를 달아놓고 bind됫는지 확인하고 값을 가져온다, 서비스
    //값 가져오고, map이나 다른 함수로 변환해도된다.

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentWalkBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                requireContext(),
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

        initView()
        initViewModel()

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
                walkViewModel.fetchStoreData(it.latitude, it.longitude) // 위치 데이터 가져오기(꼭 있어야함)
            }
        }
    }

    private fun setCamera(lastLocation: Location) {
        val latLng = LatLng(lastLocation.latitude, lastLocation.longitude)
        cameraPosition = CameraPosition(latLng, 15.0)
    }

    private fun initView() {
        binding.btnWalkStart.setOnClickListener {
            walkViewModel.walkToggle()
        }
        binding.ibWalkStop.setOnClickListener {
            //todo 정보 미리 저장
            walkViewModel.walkToggle()
        }
        binding.rvWalkDogs.adapter = walkDogAdapter
    }

    private fun initViewModel() {

        lifecycleScope.launch {
            walkViewModel.storeListState.flowWithLifecycle(viewLifecycleOwner.lifecycle)
                .collectLatest {
                    clearMarkers()
                    addMarkers(it)
                }
        }

        lifecycleScope.launch {
            walkViewModel.walkUiState.flowWithLifecycle(viewLifecycleOwner.lifecycle)
                .collectLatest {
                    if (it.isWalking) {
                        binding.grWalkPrev.isVisible = false
                        binding.grWalkUi.isVisible = true
                        startLocationService()
                        bindToService()
                    } else {
                        binding.grWalkUi.isVisible = false
                        binding.grWalkPrev.isVisible = true
                        unbindFromService()
                        endLocationService()
                    }
                }
        }

        lifecycleScope.launch {
            walkViewModel.walkInfoState.flowWithLifecycle(viewLifecycleOwner.lifecycle)
                .collectLatest {
                    updateTimerText(it.walkingTime)
                }
        }

        lifecycleScope.launch {
            walkViewModel.dogSelectionState.flowWithLifecycle(viewLifecycleOwner.lifecycle)
                .collectLatest {

                }
        }

    }

    private fun clearMarkers() {
        markerList.forEach { it.map = null }
        markerList.clear()
    }

    private fun addMarkers(storeListState: StoreListState) {
        /* TODO:
        *   클릭시 정보창 띄우기로 변경, 마커 사이즈 줄이기, bound는 어떻게 */
        storeListState.storeList.forEach { store ->
            val latLng = LatLng(store.lat!!.toDouble(), store.lng!!.toDouble())
            val marker = Marker()
            marker.width = Marker.SIZE_AUTO
            marker.height = 60
            /** 적절한 크기 찾아야하는데..*/
            marker.position = latLng
            marker.map = naverMap
            markerList.add(marker)

            val contentString =
                store.placeName // Assuming `storeEntity` has a `name` property

            val infoWindow = InfoWindow().apply {
                adapter = object : InfoWindow.DefaultTextAdapter(requireContext()) {
                    override fun getText(infoWindow: InfoWindow): CharSequence {
                        return contentString.toString()
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

    private fun startLocationService() {
        val intent = Intent(requireContext(), LocationService::class.java)
        requireActivity().startForegroundService(intent)
        isServiceRunning = true
        if (!bound) {
            bindToService()
        }
    }

    private fun bindToService() {
        if(isServiceRunning) {
            Intent(requireContext(), LocationService::class.java).also { intent ->
                requireContext().bindService(
                    intent,
                    serviceConnection,
                    Context.BIND_AUTO_CREATE
                )
            }
            startCollectingServiceFlow()
            bound = true
        }
    }

    private fun startCollectingServiceFlow() {
//        serviceDistanceStateFlow?.let { flow ->
//            viewLifecycleOwner.lifecycleScope.launch {
//                flow.flowWithLifecycle(viewLifecycleOwner.lifecycle).collectLatest { dist ->
//                    updateDistanceText(dist)
//                }
//            }
//        }
        lifecycleScope.launch {
            serviceDistanceStateFlow?.flowWithLifecycle(viewLifecycleOwner.lifecycle)?.collectLatest {
                updateDistanceText(it)
                Log.d("WalkFragment", it.toString())
            }
        }
    }

    private fun unbindFromService() {
        if(isServiceRunning) {
            stopCollectingServiceFlow()
            requireContext().unbindService(serviceConnection)
            bound = false
        }
    }

    private fun stopCollectingServiceFlow() {
        serviceDistanceStateFlow?.let { _ ->
            viewLifecycleOwner.lifecycleScope.coroutineContext[Job]?.cancel()
        }
    }

    private fun endLocationService() {
        // 서비스에서 리스트 받아오기
        val locationList = locationService?.locationList?.map {
            LatLng(it.latitude, it.longitude)
        }.orEmpty().toTypedArray() // Null되면 빈 리스트가 들어감
        // 종료 할 때
        if(isServiceRunning) {
            locationService?.stopService()
            bound = false
            isServiceRunning = false
        }

//        sendLocationListToFinishActivity(locationList) // FinishActivirty로 list 보내기
    }

    override fun onStart() {
        super.onStart()
        if (!bound) {
            bindToService()
        }
    }

    override fun onStop() {
        super.onStop()
        if(bound) {
            stopCollectingServiceFlow()
            unbindFromService()
        }
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

    /** 좋은 방법인지는 모르겠으나 서비스 도입하면서 이렇게 Intent를 보내게 되었음. */
    private fun sendLocationListToFinishActivity(locationList: Array<LatLng>) {
        val intent = Intent(requireContext(), FinishActivity::class.java).apply {
            putExtra("locationList", locationList)
//            putExtra("time", totalWalkTime)
//            putExtra("distance", totalDistance)
        }
        startActivity(intent)
    }

    private fun updateDistanceText(dist: Double) {
        binding.tvWalkDistance.text = String.format("%.1f km", dist)
    }

    private fun updateTimerText(time: Int) {
        //시간 구하기
        val hour = time / 3600
        val min = (time / 60) % 60
        val sec = time % 60

        //한자리 수 일땐 앞에 0을 붙이기 위함
        val tHour = "%02d".format(hour)
        val tMin = "%02d".format(min)
        val tSec = "%02d".format(sec)

        //1시간 이상 측정 시 시간 단위로 표현
        val totalTimeText = if (hour >= 1) "${tHour}시간  ${tMin}분" else "${tMin}분 ${tSec}초"

        //ui수정
        binding.tvWalkTime.text = totalTimeText
    }
}



