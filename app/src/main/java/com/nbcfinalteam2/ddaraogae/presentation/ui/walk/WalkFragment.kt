package com.nbcfinalteam2.ddaraogae.presentation.ui.walk

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.CameraPosition
import com.naver.maps.map.CameraUpdate
import com.naver.maps.map.LocationSource
import com.naver.maps.map.LocationTrackingMode
import com.naver.maps.map.MapFragment
import com.naver.maps.map.NaverMap
import com.naver.maps.map.OnMapReadyCallback
import com.naver.maps.map.overlay.InfoWindow
import com.naver.maps.map.overlay.Marker
import com.naver.maps.map.util.FusedLocationSource
import com.nbcfinalteam2.ddaraogae.R
import com.nbcfinalteam2.ddaraogae.databinding.FragmentWalkBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.util.TimerTask

class WalkFragment : Fragment(), OnMapReadyCallback {

    private val LOCATION_PERMISSION_REQUEST_CODE = 5000
    private val PERMISSIONS = arrayOf(
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_COARSE_LOCATION
    )
    private lateinit var binding: FragmentWalkBinding

    private lateinit var naverMap: NaverMap
    private lateinit var locationSource: FusedLocationSource //
    //위치 서비스가 gps를 사용해서 위치를 확인
    lateinit var fusedLocationClient: FusedLocationProviderClient
    //위치 값 요청에 대한 갱신 정보를 받는 변수
    lateinit var locationCallback: LocationCallback
    private lateinit var cameraPosition: CameraPosition

    private val latLngList = mutableListOf<LatLng>()
    private var isTracking = false // 위치 추적 여부를 나타내는 변수 추가


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d("onCreated()", "asd")
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentWalkBinding.inflate(inflater, container, false)
        Log.d("onCreateView()", "asd")
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d("onViewCreated()", "asd")

        // TODO: 1. 권한 요청 전에 이미 권한이 체크되어 있는지 if문으로 구분하고
        //  그 후에 권한을 요청하게 코드 수정 필요 지금은 무조건 권한을 일단 물어보는걸 권함
        //  hasPermission 합쳐보라

        /**
         * 권한 확인에는 requireContext()를, 권한 요청에는 requireActivity()를 사용하는 것이 적절합니다.
         * 권한이 확인 되었으니 맡애 hasPermission일때 권한 요청을 하며 문제 없으면 initMapvView 합니다.
         * **/

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
            val intent = Intent(context, FinishActivity::class.java)
            // latLng 리스트를 FinishActivity로 전달합니다.
            // putExtra() 메서드를 사용하여 리스트를 전달할 수 있습니다.
            intent.putExtra("latLngList", latLngList.toTypedArray())
            Log.d("buttonListener()", latLngList.toString())
            startActivity(intent)
        }
    }

    override fun onStart() {
        super.onStart()
        Log.d("onStart()", "asd")
    }

    /**
     * hasPermission : 주로 위치 권한이나 다른 중요한 권한이 모두 허용되었는지 확인하는 데 사용됩니다. 이와 같은 권한 확인은 앱이 제대로 동작하기 위해 필수적인 경우가 많습니다.
     * initMapView : Fragemnt 위에 네이버 자체의 mapFragment를 사용하기에 Fragment 위에서 Fragemnt를 사용하는 격이라 child를 사용하며
     * 주요하게 보아야할 메서드들
     * onMapReady : 맵을 생성할 준비가 되었을 때 가장 먼저 호출되는 오버라이드 메소드이다.
     * updateLocation
     * setLastLocation
     * tracking 관련 메소드
     **/
    private fun initMapView() {
        val fm = childFragmentManager
        val mapFragment = fm.findFragmentById(R.id.fragment_walk) as MapFragment?
            ?: MapFragment.newInstance().also {
                fm.beginTransaction().add(R.id.fragment_walk, it).commit()
            }
        // fragment의 getMapAsync() 메서드로 OnMapReadyCallback 콜백을 등록하면 비동기로 NaverMap 객체를 얻을 수 있다.
        mapFragment.getMapAsync(this)
//        locationSource = FusedLocationSource(this, LOCATION_PERMISSION_REQUEST_CODE)
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

    /**
     *
     **/

    override fun onMapReady(naverMap: NaverMap) {
        Log.d("onMapReady()", "asd")
        this.naverMap = naverMap
        // FusedLocationSource 객체를 초기화하고, Naver Map의 locationSource로 설정합니다. 이를 통해 Naver Map은 위치 업데이트를 받아 현재 위치를 표시할 수 있습니다.
//        naverMap.locationSource = locationSource
        naverMap.locationSource
        // 현재 위치 버튼 기능
        naverMap.uiSettings.isLocationButtonEnabled = true
        // 위치를 추적하면서 카메라도 따라 움직인다.
        naverMap.locationTrackingMode = LocationTrackingMode.Follow
        /**
         여기서 잠깐!
         FusedLocationSource와 FusedLocationProviderClient는 어떤 차이가 있을까?
         FusedLocationSource : 위의 설명과 같이 '소스 설정(현재 위치 얻기)'만을 위함
         FusedLocationProviderClient : 위치 정보를 제공하는 소스를 통합(fused)하여 배터리 효율성을 높이고, 더 정확한 위치 정보를 제공, 다양한 위치 기반 기능
          여기서는 ~~~~~~
        * **/

        updateLocation()

        // 위치 추적 시작 버튼 클릭 리스너 설정
        binding.btnWalkStart.setOnClickListener {
            startOrStopTracking()
        }

    }

    /**
     1.
     **/
//    private fun updateLocation() {
//        @Suppress("DEPRECATION") val locationRequest = LocationRequest.create().apply {
//            // create
//            interval = 1000 // 1초마다 위치 업데이트를 요청
//            fastestInterval = 500 // 위치 업데이트를 받을 수 있는 가장 빠른 시간 간격을 나타냅니다. 시스템이 더 빈번하게 위치 업데이트를 제공할 수 있는 경우, 최소 500밀리초 간격으로 업데이트를 받을 수 있습니다.
//            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
//        }
    @SuppressLint("MissingPermission") // if-else, toast로 확인해보기
    private fun updateLocation() { // create 권장을 안한대서 builder 코드로 대체
        val locationRequest = LocationRequest.Builder(
            Priority.PRIORITY_HIGH_ACCURACY, // 위치 요청의 우선순위 설정
            1000 // 위치 업데이트 간격 설정 (밀리초)
        ).apply {
            setMinUpdateIntervalMillis(500) // 가장 빠른 위치 업데이트 간격 설정 (밀리초)
            // TODO: 권한에 정확한 위치 찾게하는 권한이 있음 찾아보셈
            // TODO: 1. toastMessage 2. 시스템 설정으로 보내야됨(토스트 필요) 3. 버튼을 눌렀을때 권한 요청해라
            setWaitForAccurateLocation(true) // 정확한 위치가 필요할 때까지 기다리도록 설정
        }.build()
        Log.d("locationRequest", locationRequest.toString())

        //위치 업데이트 요청, 현재 위치 가져오기 등의 작업을 할 수 있습니다.
        // FusedLocationProviderClient 객체를 초기화합니다. 이를 통해 앱은 Google의 위치 서비스를 사용하여 위치 정보를 가져올 수 있습니다.
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())


        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) { // 위치 업데이트 결과를 처리하는 메서드입니다. 이 메서드는 새로운 위치 업데이트가 있을 때 호출
                locationResult?.let {
                    for (location in it.locations) {
                        Log.d("위치정보", "위도: ${location.latitude} 경도: ${location.longitude}")
                        // locationResult에서 위치 목록을 반복하여 각 위치에 대한 작업을 수행하는데, location을 인자로 받아 마지막 위치를 설정하는 메서드를 호출합니다.
                        setLastLocation(location)
                    }
                }
            }
        }

        // 위치 권한아 부여 되어야만 업데이트할 수 있어서 위에 코드가 있고, 요청과 콜백과 루퍼를 요청 합니다.
        if (hasPermission()) {
            fusedLocationClient.requestLocationUpdates(
                locationRequest, locationCallback,
                Looper.myLooper()!!
            )
            Log.d("request", fusedLocationClient.requestLocationUpdates(
                locationRequest, locationCallback,
                Looper.myLooper()!!
            ).toString())
        }
        /**
        requestLocationUpdates 메서드는 위치 업데이트를 비동기적으로 처리하기 때문에 업데이트 이벤트가 발생하면 해당 이벤트를 메인 스레드의 메시지 큐에 추가하고,
        Looper를 통해 메시지 큐를 처리하여 위치 업데이트를 수신하게 됩니다.
        이를 위해 requestLocationUpdates 메서드의 마지막 매개변수로 Looper.myLooper()를 전달하여 현재 스레드의 Looper를 사용하도록 지정합니다.
        이렇게 하면 위치 업데이트 이벤트가 메인 스레드의 메시지 큐에서 처리되어 UI 업데이트 등이 안전하게 이루어집니다.
         */
    }

    fun setLastLocation(lastLocation: Location) {
        val latLng = LatLng(lastLocation.latitude, lastLocation.longitude)
        cameraPosition = CameraPosition(latLng, 15.0) // 배그에서 15배 30배 이런 느낌
        naverMap.moveCamera(CameraUpdate.toCameraPosition(cameraPosition)) // 카메라 움직이기
        Log.d("setLast", naverMap.moveCamera(CameraUpdate.toCameraPosition(cameraPosition)).toString())
        // CameraPosition 클래스는 Builder 패턴이 아니기 때문에 Builder()를 사용할 수 없습니다.
        // 대신에 CameraPosition 클래스의 생성자를 사용하여 직접 객체를 생성해야 합니다.
        //따라서 CameraPosition.Builder().target(latLng).zoom(15.0f).build() 대신에
        // CameraPosition(latLng, 15.0f)와 같이 생성자를 사용하여 CameraPosition 객체를 생성할 수 있습니다.
        //따라서 setLastLocation() 함수에서 다음과 같이 수정할 수 있습니다:
    }

    private fun startOrStopTracking() {
        if (isTracking) {
            // 위치 추적 중지
            stopTracking()
        } else {
            // 위치 추적 시작
            startTracking()
        }
    }

    private fun stopTracking() {
        isTracking = false // 위치 추적 중지
    }



    private fun startTracking() {
        // 위치 추적 중인 경우에는 다시 시작하지 않음
        if (isTracking) return
        isTracking = true // 위치 추적 시작

        // 실제로는 사용자의 위치를 받아오는 로직을 추가해야 합니다.
        // 여기서는 임의의 위치로 테스트합니다.
        val timer = java.util.Timer()
        val handler = Handler(Looper.getMainLooper())
        val task = object : TimerTask() {
            @SuppressLint("MissingPermission")
            override fun run() {
                lifecycleScope.launch {
                    val location = fusedLocationClient.getCurrentLocation(
                        LocationRequest.PRIORITY_HIGH_ACCURACY,
                        null
                    ).await()
                    // 현재 위치를 받아왔다. PRIoRITY는 왜 deprcated 되는지 확이나자, await한 이유는 원래 비동기해야하는 작업이기 떄문에
                    // await보다는 listener룰 사용하면 성공 실패여부에 따라 다른 처리를 할 수 있다.
                    // 위치 받는걸 기다리게 하기 위해 await을 썼다.
                    // lifecycle은 새로운 코루틴을 사용한 것
                    // 이 안에서 하는건 새로운 일꾼이 하고 있는 것, 비동기이며 오래걸리는 작업이다보니 listener를 달아야하는데 급하게 await을 사용,
                    // 그래서 await을 안쓰면 아래 코드들이 먼저 실행될 수 있음
                    val newLatLng = LatLng(location.latitude, location.longitude)
                    Log.d("latLngList", latLngList.toString())
                    Log.d("newLatLng", newLatLng.toString())

                    // 현재 위치를 지도 중심으로 이동
                    val cameraUpdate = CameraUpdate.scrollTo(newLatLng)
                    handler.post {
                        naverMap.moveCamera(cameraUpdate)
                        Log.d("cameraUpdate", naverMap.moveCamera(cameraUpdate).toString())
                    }
                    // 현재 위치를 리스트에 추가
                    latLngList.add(newLatLng)
                    spotMarker()
                }
            }
        }
        // TimerTask를 스케줄링
        timer.schedule(task, 0, 3000) // 3초마다 위치 업데이트
    }
    private fun spotMarker() {
        lifecycleScope.launch(Dispatchers.Main) {
            for (latLng in latLngList) {
                val marker = Marker()
                marker.position = latLng
                marker.map = naverMap
                Log.d("spotMarker", "Marker added at: ${latLng.latitude}, ${latLng.longitude}")

                val contentString = "".trimIndent()

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
    }
}