package com.nbcfinalteam2.ddaraogae.presentation.ui.walk

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
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
    private val latLngList = mutableListOf<LatLng>()
    private lateinit var cameraPosition: CameraPosition
    private lateinit var cameraUpdate: CameraUpdate

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        if (!hasPermission()) {
            ActivityCompat.requestPermissions(this, PERMISSIONS, LOCATION_PERMISSION_REQUEST_CODE)
        } else {
            initMapView()
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
                drawPolyLine(latLngList)
                Log.d("drawPolyLine", drawPolyLine(latLngList).toString())
                setCameraOnPolyLine(latLngList)
                Log.d("setCameraOn", setCameraOnPolyLine(latLngList).toString())
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

    private fun drawPolyLine(latLngList: List<LatLng>) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val latLngList: Array<LatLng>? =
                intent.getParcelableArrayExtra("latLngList", LatLng::class.java)
            latLngList?.let {
                drawPolyLine(it.toList())
            }
        } else {
            val latLngList: Array<Parcelable>? = intent.getParcelableArrayExtra("latLngList")
            latLngList?.let {
                val latLngArrayList = it.filterIsInstance<LatLng>()
                drawPolyLine(latLngArrayList)
            }
        }

        Log.d("drawPolyLine()", "invoked()")
        // 좌표가 2개 이상인 경우에만 Polyline을 그립니다.
        if (latLngList.size >= 2) {
            // Main 코루틴 컨텍스트를 사용하여 UI 업데이트를 수행합니다.
            lifecycleScope.launch(Dispatchers.Main) {
                polyline.apply {
                    // UI 업데이트는 여기서 수행됩니다.
                    width = 10
                    color = resources.getColor(R.color.red)
                    coords = latLngList
                    map = naverMap
                    Log.d("drawPolyLine()", "size efficient")
                }
            }
        }
    }
    private fun setCameraOnPolyLine(latLngList: List<LatLng>) {
        /*
        TODO: 위도 경도의 최대값과 최소값을 통해 중심점을 찾아 카메라포지션을 잡을 수 있을까?
        TODO: '위치 정보를 불러왔을때' 조건을 추가해주면 되겠다.
        */
        var latMin = latLngList[0].latitude
        var latMax = latLngList[0].latitude
        var lngMin = latLngList[0].longitude
        var lngMax = latLngList[0].longitude

        // 모든 위치를 순회하며 최대/최소값 업데이트
        for (latLng in latLngList) {
            if (latLng.latitude < latMin) latMin = latLng.latitude
            if (latLng.latitude > latMax) latMax = latLng.latitude
            if (latLng.longitude < lngMin) lngMin = latLng.longitude
            if (latLng.longitude > lngMax) lngMax = latLng.longitude
        }

        // 최대값과 최소값의 평균을 계산하여 중심점 찾기
        val centerLat = (latMin + latMax) / 2
        val centerLng = (lngMin + lngMax) / 2
        val center = LatLng(centerLat, centerLng)

        // 중심점을 사용하여 카메라 포지션 설정 (줌 레벨은 15로 설정)
        cameraPosition = CameraPosition(center, 15.0)

        // 카메라 업데이트 생성
        cameraUpdate = CameraUpdate.toCameraPosition(cameraPosition)

        // NaverMap에 카메라 업데이트 적용
        naverMap.moveCamera(cameraUpdate)
    }
}