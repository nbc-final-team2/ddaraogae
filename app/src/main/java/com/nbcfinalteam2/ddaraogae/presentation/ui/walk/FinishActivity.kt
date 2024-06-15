package com.nbcfinalteam2.ddaraogae.presentation.ui.walk

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import com.naver.maps.geometry.LatLng
import com.naver.maps.geometry.LatLngBounds
import com.naver.maps.map.CameraUpdate
import com.naver.maps.map.MapFragment
import com.naver.maps.map.NaverMap
import com.naver.maps.map.overlay.PolylineOverlay
import com.nbcfinalteam2.ddaraogae.R
import com.nbcfinalteam2.ddaraogae.databinding.ActivityFinishBinding
import com.nbcfinalteam2.ddaraogae.presentation.model.DogInfo
import com.nbcfinalteam2.ddaraogae.presentation.model.WalkingUiModel
import com.nbcfinalteam2.ddaraogae.presentation.ui.walk.StampDialogFragment.Companion.ARG_STAMP_LIST
import com.nbcfinalteam2.ddaraogae.presentation.util.ImageConverter.bitmapToByteArray
import com.nbcfinalteam2.ddaraogae.presentation.util.TextConverter.dateDateToString
import com.nbcfinalteam2.ddaraogae.presentation.util.TextConverter.distanceDoubleToString
import com.nbcfinalteam2.ddaraogae.presentation.util.TextConverter.timeIntToString
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class FinishActivity : FragmentActivity() {
    private val binding by lazy { ActivityFinishBinding.inflate(layoutInflater) }
    private val viewModel: FinishViewModel by viewModels()

    private val LOCATION_PERMISSION_REQUEST_CODE = 5000
    private val PERMISSIONS = arrayOf(
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_COARSE_LOCATION
    )

    private lateinit var naverMap: NaverMap
    private var polyline = PolylineOverlay()
    private lateinit var locationList: List<LatLng>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        getDataForInitView()
    }

    private fun getDataForInitView() {
        requestPermissionForMap()

        locationList = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableArrayExtra("locationList", LatLng::class.java)?.toList().orEmpty()
        } else {
            (intent.getParcelableArrayExtra("locationList") as? Array<LatLng>)?.toList().orEmpty()
        }

        val walkingUiModel: WalkingUiModel? = intent.getParcelableExtra("wakingInfo")
        val walkingDogs: List<DogInfo>? = intent.getParcelableArrayListExtra("walkingDogs")

        initView(walkingUiModel, walkingDogs)

        lifecycleScope.launch {
            viewModel.taskState.collectLatest { state ->
                when (state) {
                    InsertTaskState.Idle -> binding.btnFinishDone.isEnabled = true
                    InsertTaskState.Loading -> binding.btnFinishDone.isEnabled = false
                    InsertTaskState.Success -> {
                        viewModel.checkStampCondition(walkingUiModel?.startDateTime!!)
                    }

                    is InsertTaskState.Error -> binding.btnFinishDone.isEnabled = true
                }
            }
        }

        lifecycleScope.launch {
            viewModel.stampState.collectLatest { state ->
                when (state) {
                    StampTaskState.Idle -> binding.btnFinishDone.isEnabled = true
                    StampTaskState.Loading -> binding.btnFinishDone.isEnabled = false
                    StampTaskState.Success -> {
                    }

                    is StampTaskState.Error -> binding.btnFinishDone.isEnabled = true
                }
            }
        }

        lifecycleScope.launch {
            viewModel.stampList.collectLatest { list ->
                if (list.isNotEmpty()) {
                    // 받을 스탬프가 있을 때
                    val dialogFragment = StampDialogFragment.newInstance(ArrayList(list))
                    dialogFragment.isCancelable = false
                    dialogFragment.lifecycle.addObserver(object : DefaultLifecycleObserver {
                        override fun onDestroy(owner: LifecycleOwner) {
                            super.onDestroy(owner)
                            finish()
                        }
                    })
                    dialogFragment.show(supportFragmentManager, ARG_STAMP_LIST)
                } else {
                    // 받을 스탬프가 없을 때
                    finish()
                }
            }
        }
    }

    private fun requestPermissionForMap() {
        if (!hasPermission()) {
            ActivityCompat.requestPermissions(
                this@FinishActivity,
                PERMISSIONS,
                LOCATION_PERMISSION_REQUEST_CODE
            )
        } else {
            initMapView()
        }
    }

    private fun initView(walkingUiModel: WalkingUiModel?, walkingDogs: List<DogInfo>?) =
        with(binding) {
            val dogsAdapter = walkingDogs?.let { FinishDogAdapter(it) }

            //산책 시간
            tvFinishWalkingTime.text = walkingUiModel?.timeTaken?.let {
                timeIntToString(it)
            }

            //산책 거리
            tvFinishWalkingDistance.text = walkingUiModel?.distance?.let {
                distanceDoubleToString(it)
            }

            //반려견 목록
            rvFinishDogs.adapter = dogsAdapter

            //날짜
            tvFinishDate.text = walkingUiModel?.startDateTime?.let {
                dateDateToString(it)
            }

            //산책 끝 버튼
            btnFinishDone.setOnClickListener {
                if (walkingUiModel != null) {
                    finishWalking(walkingUiModel)
                }
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

            drawPolyLine()
            setCameraOnPolyLine()
        }
    }

    private fun hasPermission(): Boolean {
        for (permission in PERMISSIONS) {
            if (ContextCompat.checkSelfPermission(
                    this,
                    permission
                ) != PackageManager.PERMISSION_GRANTED
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
        if (locationList.isNotEmpty()) {
            val boundsBuilder = LatLngBounds.Builder()
            for (latLng in locationList) {
                boundsBuilder.include(latLng)
            }
            val bounds = boundsBuilder.build()

            val padding = 100

            val cameraUpdate = CameraUpdate.fitBounds(bounds, padding)
            naverMap.moveCamera(cameraUpdate)
            Log.d("setCameraOnPolyLine", "Camera moved to fit bounds")
        } else {
            Log.d("setCameraOnPolyLine", "Location list is empty, cannot set camera")
        }
    }

    private fun finishWalking(walkingUiModel: WalkingUiModel) {
        if (::naverMap.isInitialized) {
            naverMap.takeSnapshot {
                val mapImage = bitmapToByteArray(it)
                viewModel.insertWalkingData(walkingUiModel, mapImage!!)
            }
        }
    }
}