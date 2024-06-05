package com.nbcfinalteam2.ddaraogae.presentation.ui.walk

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.os.Looper
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
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
import com.nbcfinalteam2.ddaraogae.databinding.ActivityFinishBinding
import kotlinx.coroutines.*

class FinishActivity : FragmentActivity(), OnMapReadyCallback {

    private val binding by lazy { ActivityFinishBinding.inflate(layoutInflater) }

    private val LOCATION_PERMISSION_REQUEST_CODE = 5000

    private val PERMISSIONS = arrayOf(
        android.Manifest.permission.ACCESS_FINE_LOCATION,
        android.Manifest.permission.ACCESS_COARSE_LOCATION
    )

    private lateinit var naverMap: NaverMap
    private lateinit var locationSource: FusedLocationSource

    //위치 값 요청에 대한 갱신 정보를 받는 변수
    lateinit var locationCallback: LocationCallback

    //위치 서비스가 gps를 사용해서 위치를 확인
    lateinit var fusedLocationClient: FusedLocationProviderClient

    private var polyline = PolylineOverlay()
    private lateinit var cameraPosition: CameraPosition


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        if (!hasPermission()) {
            ActivityCompat.requestPermissions(this, PERMISSIONS, LOCATION_PERMISSION_REQUEST_CODE)
        } else {
            initMapView()
            // polyline
            // Intent에서 latLng 리스트를 받아옵니다.
            val latLngList = intent.getSerializableExtra("latLngList") as? List<LatLng> ?: emptyList()
            drawPolyLine(latLngList)
        }
    }

    private fun initMapView() {
        val fm = supportFragmentManager
        val mapFragment = fm.findFragmentById(R.id.fragment_map_finish) as MapFragment?
            ?: MapFragment.newInstance().also {
                fm.beginTransaction().add(R.id.fragment_map_finish, it).commit()
            }
        // fragment의 getMapAsync() 메서드로 OnMapReadyCallback 콜백을 등록하면 비동기로 NaverMap 객체를 얻을 수 있다.
        mapFragment.getMapAsync(this)
        locationSource = FusedLocationSource(this, LOCATION_PERMISSION_REQUEST_CODE)
    }

    // hasPermission()에서는 위치 권한이 있을 경우 true를, 없을 경우 false를 반환한다.
    private fun hasPermission(): Boolean {
        for (permission in PERMISSIONS) {
            if (ContextCompat.checkSelfPermission(this, permission)
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
        // 줌 버튼 삭제해버리기
        naverMap.uiSettings.isZoomControlEnabled = false
        // 위치 추적 모드 설정
        naverMap.locationTrackingMode = LocationTrackingMode.Follow

        // 현재 위치를 가져와서 지도에 표시
        updateLocation()
    }

    fun updateLocation() {
        val locationRequest = LocationRequest.create().apply {
            interval = 1000
            fastestInterval = 500
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        }

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

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
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
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


    fun drawPolyLine(latLngList: List<LatLng>) {
//            coords = latLngList
        // Polyline 갱신
        // 좌표가 2개 이상인 경우에만 Polyline을 그림
//            if (latLngList.size >= 2) {
//                handler.post {
//                    polyline.coords = latLngList
//                }
//            }
        // 좌표가 2개 이상인 경우에만 Polyline을 그립니다.
        if (latLngList.size >= 2) {

            // Main 코루틴 컨텍스트를 사용하여 UI 업데이트를 수행합니다.
            GlobalScope.launch(Dispatchers.Main) {
                polyline.apply {
                    // UI 업데이트는 여기서 수행됩니다.
                    polyline.width = 10
                    polyline.color = resources.getColor(R.color.red)
                    polyline.map = naverMap

                    polyline.coords = latLngList
                    Log.d("latLngList", latLngList.toString())
                    Log.d("map", polyline.map.toString())
                }
            }
        }
    }
}