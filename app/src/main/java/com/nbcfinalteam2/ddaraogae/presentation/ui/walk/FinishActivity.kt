package com.nbcfinalteam2.ddaraogae.presentation.ui.walk

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Build
import android.os.Bundle
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
import com.naver.maps.map.OnMapReadyCallback
import com.naver.maps.map.util.FusedLocationSource
import com.naver.maps.map.overlay.PolylineOverlay
import com.nbcfinalteam2.ddaraogae.R
import com.nbcfinalteam2.ddaraogae.databinding.ActivityFinishBinding
import java.io.ByteArrayOutputStream
import com.nbcfinalteam2.ddaraogae.presentation.util.DistanceCalculator
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class FinishActivity : FragmentActivity(), OnMapReadyCallback {

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

    private lateinit var locationList: List<LatLng>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        locationList = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableArrayExtra("locationList", LatLng::class.java)?.toList().orEmpty()
        } else {
            (intent.getParcelableArrayExtra("locationList") as? Array<LatLng>)?.toList().orEmpty()
        }

        if (!hasPermission()) {
            ActivityCompat.requestPermissions(this, PERMISSIONS, LOCATION_PERMISSION_REQUEST_CODE)
        } else {
            initMapView()
        }

        buttonListener()
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

            drawPolyLine()
            setCameraOnPolyLine()
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

    private fun drawPolyLine() {
        if (locationList.isNotEmpty()) {
            Log.d("drawPolyLine", "Invoked with ${locationList.size} locations")
            if (locationList.size >= 2) {
                lifecycleScope.launch(Dispatchers.Main) {
                    polyline.apply {
                        width = 10
                        color = resources.getColor(R.color.red, null)
                        coords = locationList
                        map = naverMap
                        Log.d("drawPolyLine", "Polyline drawn with ${coords.size} points")
                    }
                }
            } else {
                Log.d("drawPolyLine", "Not enough points to draw polyline")
            }
        } else {
            Log.d("drawPolyLine", "Location list is empty")
        }
    }

    private fun setCameraOnPolyLine() {
        /** 위도 경도의 최대값과 최소값을 통해 중심점을 찾아 카메라포지션을 잡기
         * '위치 정보를 불러왔을때' 조건을 추가해주면 되겠다. */

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

            val distanceDiff = DistanceCalculator.getDistance(latMin, latMax, lngMin, lngMax)
            // maxDiff를 기준으로 줌 레벨 조정
            Log.d("distanceDiff", "$distanceDiff")
            val zoomLevel = when {
                distanceDiff > 2500.0 -> 5.0
                distanceDiff > 1500.0 -> 10.0
                distanceDiff > 500.0 -> 15.0
                else -> 3.0 /** 500부터 2500으로 했었는데 거꾸로 바꿔주니까 when문을 잘 탄다!
                다만 거리마다 적합한 줌 배율을 정해야 하는데 이건 테스트가 필요하다 */
            }

            val centerLat = (latMin + latMax) / 2
            val centerLng = (lngMin + lngMax) / 2
            val center = LatLng(centerLat, centerLng)

            cameraPosition = CameraPosition(center, zoomLevel)
            Log.d("cameraPosition", "$zoomLevel")
            cameraUpdate = CameraUpdate.toCameraPosition(cameraPosition)
            naverMap.moveCamera(cameraUpdate)
            Log.d("setCameraOnPolyLine", "Camera moved to $center")
        } else {
            Log.d("setCameraOnPolyLine", "Location list is empty, cannot set camera")
        }
    }

    private fun buttonListener() {
        binding.btnFinishDone.setOnClickListener {
            if (::naverMap.isInitialized) {
                naverMap.takeSnapshot {
                    /**
                     * 아래는 예시 코드입니다.
                     * : 산책 끝 버튼을 클릭할 때 산책 entity에 값을 담아 insert를 요청합니다.
                     *
                     * 지도 이미지를 캡쳐하는 기능의 takeSnapshot은 버튼 클릭 시 처리되도록 하는 것이 좋습니다.
                     * 별도의 트리거 없이 작동시키려고 하니 이미지가 정상적으로 저장되지 않는 문제가 있었습니다.
                     */
                    val mapImage = bitmapToByteArray(it)
//                    val start = Date()
//
//                    val entity = WalkingEntity(
//                        id = "",
//                        dogId = "temp",
//                        timeTaken = 100,
//                        distance = 200.0,
//                        startDateTime = start,
//                        endDateTime = start,
//                        walkingImage = ""
//                    )
//                    viewModel.insertWalkingData(entity, mapImage!!)
                }
            }
        }
    }

    private fun bitmapToByteArray(bitmap: Bitmap?): ByteArray? {
        return bitmap?.let {
            val byteArrayOutputStream = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream)
            byteArrayOutputStream.toByteArray()
        }
    }
}