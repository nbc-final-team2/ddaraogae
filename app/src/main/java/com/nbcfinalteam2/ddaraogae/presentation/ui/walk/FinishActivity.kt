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
}