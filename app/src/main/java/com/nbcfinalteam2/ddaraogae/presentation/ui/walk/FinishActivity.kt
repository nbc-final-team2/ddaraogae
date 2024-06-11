package com.nbcfinalteam2.ddaraogae.presentation.ui.walk

import android.Manifest
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.IBinder
import android.os.Parcelable
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.lifecycleScope
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.CameraPosition
import com.naver.maps.map.CameraUpdate
import com.naver.maps.map.MapFragment
import com.naver.maps.map.NaverMap
import com.naver.maps.map.overlay.PolylineOverlay
import com.nbcfinalteam2.ddaraogae.R
import com.nbcfinalteam2.ddaraogae.databinding.ActivityFinishBinding
import com.nbcfinalteam2.ddaraogae.presentation.service.LocationService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class FinishActivity : FragmentActivity() {

    private val binding by lazy { ActivityFinishBinding.inflate(layoutInflater) }

    private val LOCATION_PERMISSION_REQUEST_CODE = 5000
    private val PERMISSIONS = arrayOf(
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_COARSE_LOCATION
    )

    private lateinit var naverMap: NaverMap
    private var polyline = PolylineOverlay()
    private lateinit var cameraPosition: CameraPosition
    private lateinit var cameraUpdate: CameraUpdate

    private var locationService: LocationService? = null
    private var bound = false

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


        override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        if (!hasPermission()) {
            ActivityCompat.requestPermissions(this, PERMISSIONS, LOCATION_PERMISSION_REQUEST_CODE)
        } else {
            initMapView()
        }
    }

    override fun onStart() { //TODO: 바운드가 트루인지 확인 연결해주는 작업 필요, 아래 stop()처럼
        super.onStart()
        if (!bound) {
            Intent(this, LocationService::class.java).also { intent ->
                bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE)
            }
            updatePolylineFromService()
            Log.d("onStart-updatePoly", updatePolylineFromService().toString())
        }
    }

    override fun onStop() {
        super.onStop()
        if (bound) {
            unbindService(serviceConnection)
            bound = false
        }
    }

    private fun initMapView() {
        val fm = supportFragmentManager
        val mapFragment = fm.findFragmentById(R.id.fragment_map_finish) as MapFragment?
            ?: MapFragment.newInstance().also {
                fm.beginTransaction().add(R.id.fragment_map_finish, it).commit()
            }
        mapFragment.getMapAsync { map ->
            naverMap = map
            // 스크롤 막기
            naverMap.uiSettings.isScrollGesturesEnabled = false
            // 줌인, 줌아웃 버튼 삭제
            naverMap.uiSettings.isZoomControlEnabled = false
            // 더블 터치시 줌되는 제스처 막기
            naverMap.uiSettings.isZoomGesturesEnabled = false
            //
            naverMap.addOnLocationChangeListener {
                /* TODO: 위치 정보 불러오는 서비스가 필요하다.*/
//                drawPolyLine(latLngList)
//                Log.d("drawPolyLine", drawPolyLine(latLngList).toString())
//                setCameraOnPolyLine(latLngList)
//                Log.d("setCameraOn", setCameraOnPolyLine(latLngList).toString())
//                updatePolylineFromService()
            }
        }
    }

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

    private fun updatePolylineFromService() {
        locationService?.let { service ->
            val locationList = service.locationList
            if (locationList.isNotEmpty()) {
                drawPolyLine(locationList)
                setCameraOnPolyLine(locationList)
            } else {
                Log.d("updatePolylineFromService", "Location list is empty")
            }
        }
    }

    private fun drawPolyLine(locationList: List<LocationService.LatLng>){
        Log.d("drawPolyLine", "Invoked with ${locationList.size} locations")
        if (locationList.size >= 2) {
            lifecycleScope.launch(Dispatchers.Main) {
                polyline.apply {
                    width = 10
                    color = resources.getColor(R.color.red, null)
                    coords = locationList.map {
                        LatLng(it.latitude, it.longitude)
                    }
                    map = naverMap
                    Log.d("drawPolyLine", "Polyline drawn with ${coords.size} points")
                }
            }
        } else {
            Log.d("drawPolyLine", "Not enough points to draw polyline")
        }
    }
    private fun setCameraOnPolyLine(locationList: List<LocationService.LatLng>) {
        /*
        TODO: 위도 경도의 최대값과 최소값을 통해 중심점을 찾아 카메라포지션을 잡을 수 있을까?
        TODO: '위치 정보를 불러왔을때' 조건을 추가해주면 되겠다.
        */
        if (locationList.isNotEmpty()) {
            var latMin = locationList[0].latitude
            var latMax = locationList[0].latitude
            var lngMin = locationList[0].longitude
            var lngMax = locationList[0].longitude

            for (latLng in locationList) {
                if (latLng.latitude < latMin) latMin = latLng.latitude
                if (latLng.latitude > latMax) latMax = latLng.latitude
                if (latLng.longitude < lngMin) lngMin = latLng.longitude
                if (latLng.longitude > lngMax) lngMax = latLng.longitude
            }

            val centerLat = (latMin + latMax) / 2
            val centerLng = (lngMin + lngMax) / 2
            val center = LatLng(centerLat, centerLng)

            cameraPosition = CameraPosition(center, 15.0)
            cameraUpdate = CameraUpdate.toCameraPosition(cameraPosition)
            naverMap.moveCamera(cameraUpdate)
            Log.d("setCameraOnPolyLine", "Camera moved to $center")
        } else {
            Log.d("setCameraOnPolyLine", "Location list is empty, cannot set camera")
        }
    }
}