package com.nbcfinalteam2.ddaraogae.presentation.ui.walk

import android.content.pm.PackageManager
import android.graphics.Color
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
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.CameraUpdate
import com.naver.maps.map.CameraUpdate.scrollTo
import com.naver.maps.map.LocationTrackingMode
import com.naver.maps.map.MapFragment
import com.naver.maps.map.NaverMap
import com.naver.maps.map.OnMapReadyCallback
import com.naver.maps.map.overlay.PolylineOverlay
import com.naver.maps.map.util.FusedLocationSource
import com.nbcfinalteam2.ddaraogae.R
import com.nbcfinalteam2.ddaraogae.databinding.FragmentWalkBinding
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

    private val latLngList = mutableListOf<LatLng>()
    private var polyline = PolylineOverlay()

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


        // 현재 위치 추적 시작
        startTracking()

        // Polyline 초기화
        polyline = PolylineOverlay().apply {
            width = 10
            color = resources.getColor(R.color.red)
            map = naverMap
        }
    }

    private fun startTracking() {
        // 실제로는 사용자의 위치를 받아오는 로직을 추가해야 합니다.
        // 여기서는 임의의 위치로 테스트합니다.
        val timer = java.util.Timer()
        val handler = Handler(Looper.getMainLooper())
        val task = object : TimerTask() {
            override fun run() {
                val lat = 37.5666102 + Math.random() * 0.01 // 임의의 위도 생성
                val lng = 126.9783881 + Math.random() * 0.01 // 임의의 경도 생성
                val newLatLng = LatLng(lat, lng)

                // 현재 위치를 지도 중심으로 이동
                val cameraUpdate = CameraUpdate.scrollTo(newLatLng)
                handler.post {
                    naverMap.moveCamera(cameraUpdate)
                }

                // 현재 위치를 리스트에 추가
                latLngList.add(newLatLng)

                // Polyline 갱신
                // 좌표가 2개 이상인 경우에만 Polyline을 그림
                if (latLngList.size >= 2) {
                    handler.post {
                        polyline.coords = latLngList
                    }
                }
            }
        }
        // TimerTask를 스케줄링
        timer.schedule(task, 0, 3000) // 3초마다 위치 업데이트
    }
}