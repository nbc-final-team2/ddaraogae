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
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import com.google.android.material.floatingactionbutton.FloatingActionButton.SIZE_AUTO
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.CameraPosition
import com.naver.maps.map.LocationTrackingMode
import com.naver.maps.map.MapFragment
import com.naver.maps.map.NaverMap
import com.naver.maps.map.NaverMapOptions
import com.naver.maps.map.overlay.InfoWindow
import com.naver.maps.map.overlay.Marker
import com.naver.maps.map.overlay.OverlayImage
import com.naver.maps.map.util.FusedLocationSource
import com.naver.maps.map.util.MapConstants
import com.nbcfinalteam2.ddaraogae.R
import com.nbcfinalteam2.ddaraogae.databinding.FragmentWalkBinding
import com.nbcfinalteam2.ddaraogae.presentation.model.DogInfo
import com.nbcfinalteam2.ddaraogae.presentation.model.WalkingInfo
import com.nbcfinalteam2.ddaraogae.presentation.service.LocationService
import com.nbcfinalteam2.ddaraogae.presentation.service.ServiceInfoState
import com.nbcfinalteam2.ddaraogae.presentation.ui.finish.FinishActivity
import com.nbcfinalteam2.ddaraogae.presentation.ui.finish.FinishActivity.Companion.LOCATIONLIST
import com.nbcfinalteam2.ddaraogae.presentation.ui.finish.FinishActivity.Companion.WALKINGDOGS
import com.nbcfinalteam2.ddaraogae.presentation.ui.finish.FinishActivity.Companion.WALKINGUIMODEL
import com.nbcfinalteam2.ddaraogae.presentation.util.TextConverter.distanceDoubleToString
import com.nbcfinalteam2.ddaraogae.presentation.util.TextConverter.timeIntToStringForWalk
import com.nbcfinalteam2.ddaraogae.presentation.util.ToastMaker
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.util.Date

@AndroidEntryPoint
class WalkFragment : Fragment() {

    private val walkViewModel: WalkViewModel by viewModels()
    private var _binding: FragmentWalkBinding? = null
    private val binding get() = _binding!!

    private val initMapPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        if (permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true &&
            permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true
        ) {
            initMapView()
        } else {
            ToastMaker.make(requireContext(), R.string.require_map_permission)
        }
    }

    private val startServicePermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        if (permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true &&
            permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true &&
            permissions[Manifest.permission.POST_NOTIFICATIONS] == true
        ) {
            walkViewModel.walkToggle()
        } else {
            ToastMaker.make(requireContext(), R.string.require_walk_permission)
        }
    }

    private val walkDogAdapter by lazy {
        WalkDogAdapter { walkViewModel.selectDog(it) }
    }

    private val LOCATION_PERMISSION_REQUEST_CODE = 5000
    private lateinit var naverMap: NaverMap
    private lateinit var locationSource: FusedLocationSource // callback, providerclient 필요가 없었다.
    private lateinit var cameraPosition: CameraPosition

    private var locationService: LocationService? = null
    private var bound = false
    private var serviceInfoStateFlow: StateFlow<ServiceInfoState>? = null

    private var markerList = mutableListOf<Marker>()
    private var infoWindowBackup: InfoWindow? = null
    private var trackingModeJob: Job? = null

    private val serviceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            val binder = service as LocationService.LocalBinder
            locationService = binder.getService()
            serviceInfoStateFlow = LocationService.serviceInfoState.asStateFlow()
            startCollectingServiceFlow()
            locationService?.saveData(
                walkViewModel.dogSelectionState.value.dogList.filter { it.isSelected }.map { it },
                Date(System.currentTimeMillis())
            )
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

        checkMapPermission()
        initView()
        initViewModel()
    }

    private fun checkMapPermission() {
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED &&
            ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            initMapView()
        } else {
            initMapPermissionLauncher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION
                )
            )
        }
    }

    private fun initMapView() {
        val fm = childFragmentManager
        val mapFragment = fm.findFragmentById(R.id.fragment_walk) as MapFragment?
            ?: MapFragment.newInstance(NaverMapOptions().extent(MapConstants.EXTENT_KOREA)).also {
                fm.beginTransaction().add(R.id.fragment_walk, it).commit()
            }

        locationSource = FusedLocationSource(this, LOCATION_PERMISSION_REQUEST_CODE)
        // fragment의 getMapAsync() 메서드로 OnMapReadyCallback 콜백을 등록하면 비동기로 NaverMap 객체를 얻을 수 있다.
        mapFragment.getMapAsync { map ->
            naverMap = map
            naverMap.locationSource = locationSource
            // 위치 추적, 이게 없으면 현재 위치 감지를 못한다.
            naverMap.locationTrackingMode = LocationTrackingMode.Follow
            // 현재 위치 버튼 기능
            naverMap.uiSettings.isLocationButtonEnabled = true
            // 나침반 비활성화
            naverMap.uiSettings.isCompassEnabled = false
            // 하단에 padding으로 현재 위치랑 로고 가리는 문제 해결
            naverMap.setContentPadding(0, 0, 0, 200)
            naverMap.minZoom = 7.0
            naverMap.maxZoom = 18.0
            // 반투명 원(위치 정확도 UX) 크기, ZoomLevel에 따라 유동적이지 않게 하기
            naverMap.locationOverlay.circleRadius = SIZE_AUTO
            naverMap.locationOverlay.iconHeight = SIZE_AUTO

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

            naverMap.addOnCameraChangeListener { reason, animated ->
                // 사용자의 제스쳐로 인해 Camera가 변경된 경우
                if (reason == CameraUpdate.REASON_GESTURE) {
                    // 기존 코루틴 취소
                    trackingModeJob?.cancel()

                    // 새로운 코루틴 생성 및 10초 타이머 시작
                    trackingModeJob = viewLifecycleOwner.lifecycleScope.launch {
                        delay(10000) // 10초 지연
                        naverMap.locationTrackingMode = LocationTrackingMode.Follow
                    }
                }
            }
        }
    }

    private fun setCamera(lastLocation: Location) {
        val latLng = LatLng(lastLocation.latitude, lastLocation.longitude)
        cameraPosition = CameraPosition(latLng, 15.0)
    }

    private fun initView() {
        binding.btnWalkStart.setOnClickListener {
            checkServicePermission()
        }
        binding.ibWalkStop.setOnClickListener {
            walkViewModel.walkToggle()
        }
        binding.rvWalkDogs.adapter = walkDogAdapter
    }

    private fun checkServicePermission() {
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED &&
            ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED &&
            ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            walkViewModel.walkToggle()
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                startServicePermissionLauncher.launch(
                    arrayOf(
                        Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.POST_NOTIFICATIONS
                    )
                )
            } else {
                startServicePermissionLauncher.launch(
                    arrayOf(
                        Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.ACCESS_FINE_LOCATION,
                    )
                )
            }
        }
    }

    private fun startServiceAndWalk() {
        startLocationService()
        bindToService()
        locationService?.saveData(
            walkViewModel.dogSelectionState.value.dogList.filter { it.isSelected }.map { it },
            Date(System.currentTimeMillis())
        )
    }

    private fun stopServiceAndWalk() {
        val locationList = locationService?.locationList?.map {
            LatLng(it.latitude, it.longitude)
        }.orEmpty().toTypedArray()

        val walkingUiModel = WalkingInfo(
            id = null,
            dogId = null,
            timeTaken = serviceInfoStateFlow?.value?.time,
            distance = serviceInfoStateFlow?.value?.distance,
            startDateTime = locationService?.savedStartDate,
            endDateTime = Date(System.currentTimeMillis()),
            walkingImage = null
        )

        val walkedDogIdList = locationService?.savedDogIdList

        endLocationService()

        if (locationList.size < 2) {
            ToastMaker.make(requireContext(), getString(R.string.msg_short_walking_time))
            return
        }

        getFinishActivity(walkingUiModel, walkedDogIdList, locationList)
    }

    private fun initViewModel() {
        lifecycleScope.launch {
            walkViewModel.walkUiState.flowWithLifecycle(viewLifecycleOwner.lifecycle)
                .collectLatest {
                    if (it.isLoading) {
                        binding.btnWalkStart.isEnabled = false
                    } else {
                        binding.btnWalkStart.isEnabled = true
                    }

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

        lifecycleScope.launch {
            walkViewModel.walkEvent.flowWithLifecycle(viewLifecycleOwner.lifecycle)
                .collectLatest { event ->
                    when (event) {
                        is WalkEvent.Error -> ToastMaker.make(requireContext(), event.strResId)
                        is WalkEvent.StartWalking -> {
                            startServiceAndWalk()
                        }

                        is WalkEvent.StopWalking -> {
                            walkViewModel.setLoading()
                            stopServiceAndWalk()
                            walkViewModel.releaseLoading()
                        }
                    }
                }
        }
    }

    private fun clearMarkers() {
        markerList.forEach { it.map = null }
        markerList.clear()
    }

    private fun addMarkers(storeListState: StoreListState) {
        storeListState.storeList.forEach { store ->
            val latLng = LatLng(store.lat!!.toDouble(), store.lng!!.toDouble())
            val marker = Marker()
            marker.icon = OverlayImage.fromResource(R.drawable.spotmarker)
            marker.width = Marker.SIZE_AUTO
            marker.height = Marker.SIZE_AUTO
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
                    true
                } else {
                    infoWindow.open(marker)
                    if (infoWindowBackup == null) {
                        infoWindowBackup = infoWindow
                    } else {
                        infoWindowBackup!!.close()
                        infoWindowBackup = infoWindow
                    }
                    true
                }
            }
        }
    }

    private fun startLocationService() {
        if (!LocationService.isRunning) {
            val intent = Intent(requireContext(), LocationService::class.java)
            requireActivity().startForegroundService(intent)
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

    private fun getFinishActivity(
        walkingUiModel: WalkingInfo,
        walkedDogIdList: List<DogInfo>?,
        locationList: Array<LatLng>
    ) {
        val intent = Intent(requireContext(), FinishActivity::class.java).apply {
            putExtra(LOCATIONLIST, locationList)
            putExtra(WALKINGUIMODEL, walkingUiModel)
            putExtra(WALKINGDOGS, walkedDogIdList?.let { ArrayList(it) })
        }
        startActivity(intent)
    }


    override fun onStart() {
        super.onStart()
        if (!bound && LocationService.isRunning) {
            bindToService()
        }
    }

    override fun onStop() {
        super.onStop()
        if (bound) {
            stopCollectingServiceFlow()
            unbindFromService()
        }
        trackingModeJob?.cancel()
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
//        trackingModeJob = null
    }

    private fun updateDistanceText(dist: Double) {
        binding.tvWalkDistance.text = distanceDoubleToString(dist)
    }

    private fun updateTimerText(time: Int) {
        binding.tvWalkTime.text = timeIntToStringForWalk(time)
    }
}