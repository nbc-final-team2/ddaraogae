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
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class WalkFragment : Fragment() {

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
    private lateinit var locationList: MutableList<Location>
    private var bound = false
    private var isServiceRunning = false

    private var lat: Double = 0.0
    private var lng: Double = 0.0

    private var markerList = mutableListOf<Marker>()

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

        binding.btnWalkStart.setOnClickListener {
            toggleLocationService()
        }
    }

    override fun onStart() {
        super.onStart()
        if (!bound) {
            Intent(requireContext(), LocationService::class.java).also { intent ->
                requireContext().bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE)
            }
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
            // 현재 위치
            naverMap.locationSource = locationSource
            // 현재 위치 버튼 기능
            naverMap.uiSettings.isLocationButtonEnabled = true
            // 위치를 추적하면서 카메라도 따라 움직인다.
            naverMap.locationTrackingMode = LocationTrackingMode.Follow
            // 카메라 설정
            naverMap.addOnLocationChangeListener {
                setCamera(it)
                lat = it.latitude
                lng = it.longitude // TODO: 거리비교
                ////////// 위치 정보 불러오는 곳
                viewModel.fetchStoreData(lat.toString(), lng.toString()) // 꼭 있어야함
                observeStoreData()

//                // 위치 추적 시작 버튼 클릭 리스너 설정
//                binding.btnWalkStart.setOnClickListener {
//                    toggleLocationService()
//                }
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

    //    private fun startTracking(locationList: List<LocationService.LatLng>) {
//        /*TODO: 산책버튼을 눌렀을때 이벤트 처리 주기*/
////        cameraUpdate.animate(CameraAnimation.Easing)
//        binding.btnWalkStart.setOnClickListener {
//
//        }
//
//    }
    private fun toggleLocationService() {
        if (isServiceRunning) {
            locationService?.stopService()
            isServiceRunning = false
            binding.btnWalkStart.text = "다시 시작하기"
            sendLocationListToFinishActivity()

        } else {
            Intent(requireContext(), LocationService::class.java).also { intent ->
                requireActivity().startService(intent)
                requireActivity().bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE)
            }
            isServiceRunning = true
            binding.btnWalkStart.text = "종료"
        }
    }

    private fun sendLocationListToFinishActivity() {
        val intent = Intent(requireContext(), FinishActivity::class.java).apply {
            putParcelableArrayListExtra("locationList", ArrayList(locationList))
        }
        startActivity(intent)
        // 2번째 방법
//        locationService?.let { service ->
//            locationList = service.locationList.toMutableList()
//            val intent = Intent(activity, FinishActivity::class.java).apply {
//                putParcelableArrayListExtra("locationList", ArrayList(locationList))
//            }
//            startActivity(intent)
//        }
    }

    private fun observeStoreData() {
        viewModel.storeData.observe(viewLifecycleOwner) { storeDataList ->
            storeDataList?.let { stores ->
                clearMarkers()
                addMarkers(stores)
            }
        }
    }

    private fun clearMarkers() {
        markerList.forEach { it.map = null }
        markerList.clear()
    }

    private fun addMarkers(stores: List<StoreEntity?>) {
        /* TODO:
        *   1. 처음 맵이 생성될때 위치정보에 따른 Marker 띄워주기(완료)
        *   2. 위치 거리가 '몇미터' 이상 변경됬을때 Marker 재생성하기, 4. 기존 데이터랑 멀어졌을떄 갱신(지금은 계속 동작하고 있음), 위치정보를 불러올떄마 조건 검사viewModel.fetchStoreData(lat.toString(), lng.toString())
        *   3. 마커 세팅(완료)
        *   4. 갱신하는 애를 따로 빼고(완료, 아마 addOn쪽을 말하는듯)
        *   5. observe도 딴대로 빼라(완료,observeStoreData 메서드)
        *   클릭시 정보창 띄우기로 변경
        *   사이즈 줄이기, 영어로 표시
        *   bound는 어떻게
        *   */

        stores.forEach { store ->
            store?.let { storeEntity ->
                val latLng = LatLng(storeEntity.lat!!.toDouble(), storeEntity.lng!!.toDouble())
                val marker = Marker()
                marker.position = latLng
                marker.map = naverMap
                markerList.add(marker)

                val contentString =
                    storeEntity.placeName // Assuming `storeEntity` has a `name` property

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

                infoWindow.open(marker)
            }
        }
    }
}


private fun spotMarker() {

//        viewModel.fetchStoreData(lat.toString(), lng.toString()) // 여기에다, 위치정보를 불러올떄마 조건 검사
//        viewModel.storeData.observe(viewLifecycleOwner) { storeDataList -> // observe도 딴대로 빼라
//            Log.d("walk frag", storeDataList.toString())
//            storeDataList?.forEach { store ->
//                store?.let { storeEntity ->
//                    val latLng = LatLng(storeEntity.lat!!.toDouble(), storeEntity.lng!!.toDouble())
//                    val marker = Marker()
//                    marker.position = latLng
//                    marker.map = naverMap

//                    val contentString = storeEntity.placeName // Assuming `store` has a `name` property
//
//                    val infoWindow = InfoWindow().apply {
//                        adapter = object : InfoWindow.DefaultTextAdapter(requireContext()) {
//                            override fun getText(infoWindow: InfoWindow): CharSequence {
//                                return contentString.toString()
//                            }
//                        }
//                    }
//
//                    marker.setOnClickListener {
//                        if (infoWindow.isAdded) {
//                            infoWindow.close()
//                        } else {
//                            infoWindow.open(marker)
//                        }
//                        true
//                    }
//
//                    infoWindow.open(marker)
//                }
//            }
//        }
//    }
//        val currentLocation = locationSource.lastLocation ?: return@launch
//        val lat = currentLocation.latitude.toString()
//        val lng = currentLocation.longitude.toString()
//        viewModel.fetchStoreData(lat, lng)
//        viewModel.fetchStoreData(lat.toString(), lng.toString())
//        viewModel.storeData.observe(requireActivity()) {
//            Log.d("walk frag", it.toString())
//        }
//        lifecycleScope.launch(Dispatchers.Main) {
//            viewModel.storeData { store ->
//                val latLng = LatLng(store.lat, store.lng)
//                val marker = Marker()
//                marker.position = latLng
//                marker.map = naverMap
//
//                val contentString = store.name // Assuming `store` has a `name` property
//
//                val infoWindow = InfoWindow().apply {
//                    adapter = object : InfoWindow.DefaultTextAdapter(requireContext()) {
//                        override fun getText(infoWindow: InfoWindow): CharSequence {
//                            return contentString
//                        }
//                    }
//                }
//
//                marker.setOnClickListener {
//                    if (infoWindow.isAdded) {
//                        infoWindow.close()
//                    } else {
//                        infoWindow.open(marker)
//                    }
//                    true
//                }
//
//                infoWindow.open(marker)
//            }
//        }
//    }
//        withContext(Dispatchers.Main) {
//                storeData?.forEach { store ->
//                    val latLng = LatLng(store.lat, store.lng)
//                    val marker = Marker()
//                    marker.position = latLng
//                    marker.map = naverMap
//
//                    val contentString = store.name // Assuming `store` has a `name` property
//
//                    val infoWindow = InfoWindow().apply {
//                        adapter = object : InfoWindow.DefaultTextAdapter(requireContext()) {
//                            override fun getText(infoWindow: InfoWindow): CharSequence {
//                                return contentString
//                            }
//                        }
//                    }
//
//                    marker.setOnClickListener {
//                        if (infoWindow.isAdded) {
//                            infoWindow.close()
//                        } else {
//                            infoWindow.open(marker)
//                        }
//                        true
//                    }
//
//                    infoWindow.open(marker)
//                }
//            }
}

//    lifecycleScope.launch {
//        try {
//            // Assuming you want to use the current location to get nearby stores
//            val currentLocation = locationSource.lastLocation ?: return@launch
//            val lat = currentLocation.latitude.toString()
//            val lng = currentLocation.longitude.toString()
//
//            // Get stored data using the use case
//            val storeData = getStoreDataUseCase(lat, lng)
//
//            // Show markers on the map
//
//        } catch (e: Exception) {
//            Log.e("spotMarker", "Failed to get store data", e)
//        }
//    }
//}


