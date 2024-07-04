package com.nbcfinalteam2.ddaraogae.presentation.ui.finish

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.Parcelable
import android.util.Log
import androidx.activity.OnBackPressedCallback
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import com.naver.maps.geometry.LatLng
import com.naver.maps.geometry.LatLngBounds
import com.naver.maps.map.CameraUpdate
import com.naver.maps.map.MapFragment
import com.naver.maps.map.NaverMap
import com.naver.maps.map.overlay.PolylineOverlay
import com.nbcfinalteam2.ddaraogae.R
import com.nbcfinalteam2.ddaraogae.databinding.ActivityFinishBinding
import com.nbcfinalteam2.ddaraogae.domain.bus.ItemChangedEventBus
import com.nbcfinalteam2.ddaraogae.presentation.model.DefaultEvent
import com.nbcfinalteam2.ddaraogae.presentation.model.DogInfo
import com.nbcfinalteam2.ddaraogae.presentation.model.WalkingInfo
import com.nbcfinalteam2.ddaraogae.presentation.ui.finish.StampDialogFragment.Companion.ARG_STAMP_LIST
import com.nbcfinalteam2.ddaraogae.presentation.ui.loading.LoadingDialog
import com.nbcfinalteam2.ddaraogae.presentation.util.ImageConverter.bitmapToByteArray
import com.nbcfinalteam2.ddaraogae.presentation.util.TextConverter.dateDateToString
import com.nbcfinalteam2.ddaraogae.presentation.util.TextConverter.distanceDoubleToString
import com.nbcfinalteam2.ddaraogae.presentation.util.TextConverter.timeIntToStringForWalk
import com.nbcfinalteam2.ddaraogae.presentation.util.ToastMaker
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

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
    private var distance = 0.0

    private var loadingDialog: LoadingDialog? = null

    @Inject lateinit var itemChangedEventBus: ItemChangedEventBus

    private val onBackPressedCallback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            AlertDialog.Builder(this@FinishActivity)
                .setMessage(getString(R.string.finish_dialog_msg))
                .setPositiveButton(getString(R.string.finish_dialog_confirm)) { _, _ ->
                    finish()
                }.setNegativeButton(getString(R.string.finish_dialog_cancel)) { _, _ -> }
                .show()
        }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)
        uiSetting()
        getDataForInitView()
    }

    private fun uiSetting() {
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { view, windowInsets ->
            val insets = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.updatePadding(insets.left, insets.top, insets.right, insets.bottom)
            WindowInsetsCompat.CONSUMED
        }

        onBackPressedDispatcher.addCallback(this, onBackPressedCallback)
    }

    private fun getDataForInitView() {
        requestPermissionForMap()

        locationList = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableArrayExtra(LOCATIONLIST, LatLng::class.java)?.toList().orEmpty()
        } else {
            (intent.getParcelableArrayExtra(LOCATIONLIST) as? Array<Parcelable>)?.mapNotNull {
                it as? LatLng
            }.orEmpty()
        }

        val walkingUiModel = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableExtra(WALKINGUIMODEL, WalkingInfo::class.java)
        } else {
            intent.getParcelableExtra(WALKINGUIMODEL)
        }

        distance = walkingUiModel?.distance ?: 0.0

        val walkingDogs = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableArrayListExtra(WALKINGDOGS, DogInfo::class.java).orEmpty()
        } else {
            intent.getParcelableArrayListExtra<DogInfo?>(WALKINGDOGS).orEmpty()
        }

        initView(walkingUiModel, walkingDogs)

        lifecycleScope.launch {
            viewModel.insertEvent.flowWithLifecycle(lifecycle).collectLatest { event ->
                when(event) {
                    is DefaultEvent.Failure -> ToastMaker.make(this@FinishActivity, event.msg)
                    DefaultEvent.Success -> viewModel.checkStampCondition(walkingUiModel?.startDateTime!!)
                }
            }
        }

        lifecycleScope.launch {
            viewModel.stampEvent.flowWithLifecycle(lifecycle).collectLatest { event ->
                when(event) {
                    is DefaultEvent.Failure -> ToastMaker.make(this@FinishActivity, event.msg)
                    DefaultEvent.Success -> {}
                }
            }
        }

        lifecycleScope.launch {
            viewModel.stampList.collectLatest { list ->
                if (list.isNotEmpty()) {
                    // 받을 스탬프가 있을 때
                    itemChangedEventBus.notifyStampChanged()
                    val dialogFragment = StampDialogFragment.newInstance(ArrayList(list))
                    dialogFragment.isCancelable = false
                    dialogFragment.show(supportFragmentManager, ARG_STAMP_LIST)
                } else {
                    // 받을 스탬프가 없을 때
                    finish()
                }
            }
        }

        lifecycleScope.launch {
            viewModel.finishUiState.flowWithLifecycle(lifecycle).collectLatest { state ->
                if (state.isLoading) {
                    loadingDialog = LoadingDialog()
                    loadingDialog?.show(supportFragmentManager, null)
                } else {
                    loadingDialog?.dismiss()
                    loadingDialog = null
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

    private fun initView(walkingUiModel: WalkingInfo?, walkingDogs: List<DogInfo>?) =
        with(binding) {
            val dogsAdapter = walkingDogs?.let { FinishDogAdapter(it) }

            //산책 시간
            tvFinishWalkingTime.text = walkingUiModel?.timeTaken?.let {
                timeIntToStringForWalk(it)
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
                if (walkingUiModel != null && walkingDogs != null) {
                    finishWalking(walkingUiModel, walkingDogs)
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

            val padding = if (distance >= 1.0) {
                100
            } else {
                250
            }

            val cameraUpdate = CameraUpdate.fitBounds(bounds, padding)
            naverMap.moveCamera(cameraUpdate)
            Log.d("setCameraOnPolyLine", "Camera moved to fit bounds")
        } else {
            Log.d("setCameraOnPolyLine", "Location list is empty, cannot set camera")
        }
    }

    private fun finishWalking(walkingInfo: WalkingInfo, walkingDogs: List<DogInfo>) {
        if (::naverMap.isInitialized) {
            naverMap.takeSnapshot {
                val mapImage = bitmapToByteArray(it)
                viewModel.insertWalkingData(
                    walkingInfo = walkingInfo,
                    dogIdList = walkingDogs.map { dog -> dog.id?:"" },
                    imageByteArray = mapImage!!
                )
            }
        }
    }

    companion object {
        const val LOCATIONLIST = "LOCATIONLIST"
        const val WALKINGUIMODEL = "WALKINGUIMODEL"
        const val WALKINGDOGS = "WALKINGDOGS"
    }
}