package com.nbcfinalteam2.ddaraogae.presentation.ui.walk

import android.Manifest
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.content.pm.PackageManager
import android.location.Location
import android.os.Build
import android.os.Bundle
import android.os.IBinder
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
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
import com.nbcfinalteam2.ddaraogae.presentation.model.WalkingUiModel
import com.nbcfinalteam2.ddaraogae.presentation.service.LocationService
import com.nbcfinalteam2.ddaraogae.presentation.service.ServiceInfoState
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.util.Date

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

    private val LOCATION_PERMISSIONS = arrayOf(
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_COARSE_LOCATION
    )
    private val NOTIFICATION_PERMISSION = Manifest.permission.POST_NOTIFICATIONS


    private lateinit var naverMap: NaverMap
    private lateinit var locationSource: FusedLocationSource // callback, providerclient 필요가 없었다.
    private lateinit var cameraPosition: CameraPosition
    private lateinit var cameraUpdate: CameraUpdate /*TODO: 나중에 animate을 위해 남김*/

    private var locationService: LocationService? = null
    private var bound = false
    private var serviceInfoStateFlow: StateFlow<ServiceInfoState>? = null

    private var markerList = mutableListOf<Marker>()

    private val serviceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            val binder = service as LocationService.LocalBinder
            locationService = binder.getService()
            serviceInfoStateFlow = LocationService.serviceInfoState.asStateFlow()
            startCollectingServiceFlow()
            bound = true
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            stopCollectingServiceFlow()
            serviceInfoStateFlow = null
            bound = false
            locationService = null
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (LocationService.isRunning) {
            walkViewModel.setWalking()
        }

        walkViewModel.fetchDogList()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentWalkBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (!hasPermissions()) {
            locationPermissionLauncher.launch(getPermissionsToRequest())
        } else {
            initViewModel()
            initView()
            initMapView()
        }
    }


    private val locationPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        if (permissions.all { it.value }) {
            initMapView()
            initView()
            initViewModel()
        } else {
            Toast.makeText(requireContext(), "위치 권한이 필요합니다.", Toast.LENGTH_SHORT).show()
        }
    }

    private val notificationPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            startLocationService()
        } else {
            Toast.makeText(requireContext(), "알림 권한이 필요합니다.", Toast.LENGTH_SHORT).show()
        }
    }

    // hasPermission()에서는 위치 권한이 있을 경우 true를, 없을 경우 false를 반환한다.
    private fun hasPermissions(): Boolean {
        return getPermissionsToRequest().all {
            ContextCompat.checkSelfPermission(requireContext(), it) == PackageManager.PERMISSION_GRANTED
        }
    }

    private fun getPermissionsToRequest(): Array<String> {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            LOCATION_PERMISSIONS + NOTIFICATION_PERMISSION
        } else {
            LOCATION_PERMISSIONS
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

            naverMap.locationOverlay.iconWidth = 60
            naverMap.locationOverlay.iconHeight = 60
            naverMap.locationOverlay

            // 카메라 설정
            lifecycleScope.launch {
                walkViewModel.storeListState.flowWithLifecycle(viewLifecycleOwner.lifecycle)
                    .collectLatest {
                        clearMarkers()
                        addMarkers(it)
                    }
            }
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
            startLocationService()
            bindToService()
            locationService?.saveData(
                walkViewModel.dogSelectionState.value.dogList.filter { it.isSelected }
                    .map { it.id },
                Date(System.currentTimeMillis())
            )
        }
        binding.ibWalkStop.setOnClickListener {
            //todo 정보 미리 저장
            val walkingUiModel = WalkingUiModel(
                id = null,
                dogId = null,
                timeTaken = serviceInfoStateFlow?.value?.time,
                distance = serviceInfoStateFlow?.value?.distance,
                startDateTime = locationService?.savedStartDate,
                endDateTime = Date(System.currentTimeMillis()),
                url = null
            )

            val walkedDogIdList = locationService?.savedDogIdList

//            for test
//            Log.d("check", walkingUiModel.toString())
//            Log.d("check", walkedDogIdList.toString())

            walkViewModel.walkToggle()
            serviceInfoStateFlow = null
            endLocationService()

            getFinishActivity()
        }
        binding.rvWalkDogs.adapter = walkDogAdapter
    }

    private fun initViewModel() {

        lifecycleScope.launch {
            walkViewModel.walkUiState.flowWithLifecycle(viewLifecycleOwner.lifecycle)
                .collectLatest {
                    if (it.isWalking) {
                        binding.grWalkPrev.isVisible = false
                        binding.grWalkUi.isVisible = true
                    } else {
                        binding.grWalkUi.isVisible = false
                        binding.grWalkPrev.isVisible = true
                    }
                }
        }

        lifecycleScope.launch {
            walkViewModel.dogSelectionState.flowWithLifecycle(viewLifecycleOwner.lifecycle)
                .collectLatest {
                    walkDogAdapter.submitList(it.dogList)
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

            val contentString = """
                ${store.placeName} | ${store.categoryGroupName}
                    ${store.address}
                    ${store.phone} 
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

    private fun startLocationService() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) { // Android 13 이상
            if (ContextCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                return
            }
        }

        if (!LocationService.isRunning) {
            val intent = Intent(requireContext(), LocationService::class.java)
            requireActivity().startForegroundService(intent)
        }

        if (!bound) {
            bindToService()
        }
    }


    private fun bindToService() {
        Intent(requireContext(), LocationService::class.java).also { intent ->
            requireContext().bindService(
                intent,
                serviceConnection,
                Context.BIND_AUTO_CREATE
            )
        }
        bound = true

    }

    private fun startCollectingServiceFlow() {
        lifecycleScope.launch {
            serviceInfoStateFlow?.flowWithLifecycle(viewLifecycleOwner.lifecycle)?.collectLatest {
                updateDistanceText(it.distance)
                updateTimerText(it.time)
            }
        }
    }

    private fun unbindFromService() {
        stopCollectingServiceFlow()
        requireContext().unbindService(serviceConnection)
        bound = false
    }

    private fun stopCollectingServiceFlow() {
        serviceInfoStateFlow?.let { _ ->
            viewLifecycleOwner.lifecycleScope.coroutineContext[Job]?.cancel()
        }
    }

    private fun endLocationService() {
        locationService?.stopService()
        bound = false
    }

    override fun onStart() {
        super.onStart()
        if (!bound) {
            bindToService()
        }
    }

    override fun onStop() {
        super.onStop()
        if (bound) {
            stopCollectingServiceFlow()
            unbindFromService()
        }
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

    /** 좋은 방법인지는 모르겠으나 서비스 도입하면서 이렇게 Intent를 보내게 되었음. */
    //인자 정의 필요
    private fun getFinishActivity() {
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



