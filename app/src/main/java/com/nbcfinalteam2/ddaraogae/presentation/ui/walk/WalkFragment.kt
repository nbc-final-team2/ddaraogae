package com.nbcfinalteam2.ddaraogae.presentation.ui.walk

import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import android.content.Intent
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
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.CameraPosition
import com.naver.maps.map.CameraUpdate
import com.naver.maps.map.LocationTrackingMode
import com.naver.maps.map.MapFragment
import com.naver.maps.map.NaverMap
import com.naver.maps.map.OnMapReadyCallback
import com.naver.maps.map.overlay.PolylineOverlay
import com.naver.maps.map.util.FusedLocationSource
import com.nbcfinalteam2.ddaraogae.R
import com.nbcfinalteam2.ddaraogae.databinding.FragmentWalkBinding
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.util.TimerTask

class WalkFragment : Fragment(), OnMapReadyCallback {
    private val LOCATION_PERMISSION_REQUEST_CODE = 5000

    private val PERMISSIONS = arrayOf(
        android.Manifest.permission.ACCESS_FINE_LOCATION,
        android.Manifest.permission.ACCESS_COARSE_LOCATION
    )
    private lateinit var binding: FragmentWalkBinding

    private lateinit var naverMap: NaverMap
    private lateinit var locationSource: FusedLocationSource
    //위치 서비스가 gps를 사용해서 위치를 확인
    lateinit var fusedLocationClient: FusedLocationProviderClient
    //위치 값 요청에 대한 갱신 정보를 받는 변수
    lateinit var locationCallback: LocationCallback

    // latLng 변수를 멤버 변수로 선언합니다.
    private val latLngList = mutableListOf<LatLng>()
    private lateinit var cameraPosition: CameraPosition

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (!hasPermission()) {
            ActivityCompat.requestPermissions(
                requireActivity(),
                PERMISSIONS,
                LOCATION_PERMISSION_REQUEST_CODE
            )
        } else {
            initMapView()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentWalkBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // Fragment -> Activity
        // 데이터 보내, Activity에 가보자.
        binding.btnNext.setOnClickListener {
            val intent = Intent(getActivity(), FinishActivity::class.java)
            // latLng 리스트를 FinishActivity로 전달합니다.
            // putExtra() 메서드를 사용하여 리스트를 전달할 수 있습니다.
            intent.putExtra("latLngList", latLngList.toTypedArray())
            startActivity(intent)
        }
    }

    private fun initMapView() {
        val fm = childFragmentManager
        val mapFragment = fm.findFragmentById(R.id.fragment_walk) as MapFragment?
            ?: MapFragment.newInstance().also {
                fm.beginTransaction().add(R.id.fragment_walk, it).commit()
            }
        // fragment의 getMapAsync() 메서드로 OnMapReadyCallback 콜백을 등록하면 비동기로 NaverMap 객체를 얻을 수 있다.
        mapFragment.getMapAsync(this)
        locationSource = FusedLocationSource(this, LOCATION_PERMISSION_REQUEST_CODE)
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

    override fun onMapReady(naverMap: NaverMap) {
        this.naverMap = naverMap
        // 현재 위치
        naverMap.locationSource = locationSource
        // 현재 위치 버튼 기능
        naverMap.uiSettings.isLocationButtonEnabled = true
        // 위치를 추적하면서 카메라도 따라 움직인다.
        naverMap.locationTrackingMode = LocationTrackingMode.Follow

        

        // 위치 정보 받겠다고 선언하는 곳
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())
        updateLocation()

        // 현재 위치 추적 시작
        startTracking()

//        // Polyline 초기화
//        drawPolyLine(latLngList)
    }

    fun updateLocation() {
        val locationRequest = LocationRequest.create().apply {
            interval = 1000
            fastestInterval = 500
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        }

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())

        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                locationResult?.let {
                    for (location in it.locations) {
                        Log.d("위치정보", "위도: ${location.latitude} 경도: ${location.longitude}")
                        setLastLocation(location)
                    }
                }
            }
        }

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
        fusedLocationClient.requestLocationUpdates(
            locationRequest, locationCallback,
            Looper.myLooper()!!
        )
    }

    fun setLastLocation(lastLocation: Location) {
        val latLng = LatLng(lastLocation.latitude, lastLocation.longitude)


        cameraPosition = CameraPosition(latLng, 15.0)
        naverMap.moveCamera(CameraUpdate.toCameraPosition(cameraPosition))
        // CameraPosition 클래스는 Builder 패턴이 아니기 때문에 Builder()를 사용할 수 없습니다.
        // 대신에 CameraPosition 클래스의 생성자를 사용하여 직접 객체를 생성해야 합니다.
        //따라서 CameraPosition.Builder().target(latLng).zoom(15.0f).build() 대신에
        // CameraPosition(latLng, 15.0f)와 같이 생성자를 사용하여 CameraPosition 객체를 생성할 수 있습니다.
        //따라서 setLastLocation() 함수에서 다음과 같이 수정할 수 있습니다:
    }

    private fun startTracking() {
        // 실제로는 사용자의 위치를 받아오는 로직을 추가해야 합니다.
        // 여기서는 임의의 위치로 테스트합니다.
        val timer = java.util.Timer()
        val handler = Handler(Looper.getMainLooper())
        val task = object : TimerTask() {
            override fun run() {
//                // 위치가 업데이트되지 않은 경우에는 기존의 latLng를 사용합니다.
//                val newLatLng = if (latLngList.isEmpty()) {
//                    latLng
//                } else {
//                    latLngList.last()
//                }
                if (ActivityCompat.checkSelfPermission(
                        requireActivity(),
                        Manifest.permission.ACCESS_FINE_LOCATION
                    ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                        requireActivity(),
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
//                var location : Location
                lifecycleScope.launch {
                    val location = fusedLocationClient.getCurrentLocation(LocationRequest.PRIORITY_HIGH_ACCURACY, null).await()
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
                    }

                    // 현재 위치를 리스트에 추가
                    latLngList.add(newLatLng)
                }
            }
        }
        // TimerTask를 스케줄링
        timer.schedule(task, 0, 3000) // 3초마다 위치 업데이트
    }
}