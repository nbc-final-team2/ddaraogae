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
import com.nbcfinalteam2.ddaraogae.domain.entity.StoreEntity
import com.nbcfinalteam2.ddaraogae.domain.usecase.GetStoreDataUseCase
import com.nbcfinalteam2.ddaraogae.presentation.service.LocationService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

class WalkFragment : Fragment() {

    @Inject
    lateinit var getStoreDataUseCase: GetStoreDataUseCase // Inject the use case
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
    private var isServiceRunning = false

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
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return
        }
        // Dependency injection initialization
        (requireActivity().application as MyApplication).appComponent.inject(this)

        if (!hasPermission()) {
            ActivityCompat.requestPermissions(
                requireActivity(),
                PERMISSIONS,
                LOCATION_PERMISSION_REQUEST_CODE
            )
        } else {
            initMapView()
        }
        // Fragment -> Activity
        binding.btnNext.setOnClickListener {
            val intent = Intent(activity, FinishActivity::class.java)
            startActivity(intent)
        }

        binding.btnWalkStart.setOnClickListener {
            toggleLocationService()
        }
        spotMarker()
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
            }
            // 위치 추적 시작 버튼 클릭 리스너 설정
            binding.btnWalkStart.setOnClickListener {
                toggleLocationService()
                /* TODO: viewMdoel 작업할 때 필요*/
                naverMap.addOnLocationChangeListener {

                }
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
            binding.btnWalkStart.text = "Start Walk"
        } else {
            Intent(requireContext(), LocationService::class.java).also { intent ->
                requireActivity().startService(intent)
                requireActivity().bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE)
            }
            isServiceRunning = true
            binding.btnWalkStart.text = "Stop Walk"
        }
    }

    private fun spotMarker() {
        /* TODO:
        *   1. 처음 맵이 생성될때 위치정보에 따른 Marker 띄워주기
        *   2. 위치 거리가 '몇미터' 이상 변경됬을때 Marker 재생성하기
        *   3. 마커 세팅*/
        lifecycleScope.launch {
            try {
                // Assuming you want to use the current location to get nearby stores
                val currentLocation = locationSource.lastLocation ?: return@launch
                val lat = currentLocation.latitude.toString()
                val lng = currentLocation.longitude.toString()

                // Get stored data using the use case
                val storeData = getStoreDataUseCase(lat, lng)

                // Show markers on the map
                withContext(Dispatchers.Main) {
                    storeData?.forEach { store ->
                        val latLng = LatLng(store.lat, store.lng)
                        val marker = Marker()
                        marker.position = latLng
                        marker.map = naverMap

                        val contentString = store.name // Assuming `store` has a `name` property

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

                        infoWindow.open(marker)
                    }
                }
            } catch (e: Exception) {
                Log.e("spotMarker", "Failed to get store data", e)
            }
        }
    }
}


