package com.nbcfinalteam2.ddaraogae.presentation.ui.walk

import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Bundle
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import com.naver.maps.map.MapFragment
import com.naver.maps.map.NaverMap
import com.naver.maps.map.OnMapReadyCallback
import com.naver.maps.map.util.FusedLocationSource
import com.nbcfinalteam2.ddaraogae.R
import com.nbcfinalteam2.ddaraogae.databinding.ActivityFinishBinding
import java.io.ByteArrayOutputStream

class FinishActivity : FragmentActivity(), OnMapReadyCallback {

    private val binding by lazy { ActivityFinishBinding.inflate(layoutInflater) }

    private val LOCATION_PERMISSION_REQUEST_CODE = 5000

    private val PERMISSIONS = arrayOf(
        android.Manifest.permission.ACCESS_FINE_LOCATION,
        android.Manifest.permission.ACCESS_COARSE_LOCATION
    )

    private lateinit var naverMap: NaverMap
    private lateinit var locationSource: FusedLocationSource

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

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