package com.nbcfinalteam2.ddaraogae.presentation.ui.home

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.ValueFormatter
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.nbcfinalteam2.ddaraogae.R
import com.nbcfinalteam2.ddaraogae.databinding.FragmentHomeBinding
import com.nbcfinalteam2.ddaraogae.presentation.model.WalkingInfo
import com.nbcfinalteam2.ddaraogae.presentation.model.WeatherInfo
import com.nbcfinalteam2.ddaraogae.presentation.util.DateFormatter
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private lateinit var dogProfileAdapter: DogProfileAdapter
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private val homeViewModel: HomeViewModel by viewModels()
    val locationPermissionRequest = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        when {
            permissions.getOrDefault(Manifest.permission.ACCESS_FINE_LOCATION, false) -> {
                // GPS 위치정보 가능할 때
                    getLastLocation()
            }
            permissions.getOrDefault(Manifest.permission.ACCESS_COARSE_LOCATION, false) -> {
                // 네트워크 위치정보 가능할 때
                    getLastLocation()
            }
            else -> {
                // 둘 다 권한 설정 안 했을 때
                Toast.makeText(context, "위치 권한이 필요합니다.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupWalkGraphForEmptyData()
        moveToHistory()
        setupAdapter()
        observeViewModel()
        checkLocationPermissions()
    }

    override fun onResume() {
        super.onResume()
        homeViewModel.loadDogs()
    }

    private fun setupAdapter() {
        dogProfileAdapter = DogProfileAdapter(
            onDogClick = { dogData -> homeViewModel.selectDog(dogData) },
            onAddClick = { moveToAdd() },
        )
        binding.rvDogArea.adapter = dogProfileAdapter
    }

    private fun observeViewModel() {
        homeViewModel.dogList.observe(viewLifecycleOwner) { dogs ->
            dogProfileAdapter.submitList(dogs)
        }

        homeViewModel.dogName.observe(viewLifecycleOwner) { dogName ->
            binding.tvDogGraph.text = "${dogName}의 산책 그래프"
        }

        homeViewModel.walkData.observe(viewLifecycleOwner) { walkData ->
            if (walkData.isEmpty()) {
                setupWalkGraphForEmptyData()
                binding.tvWalkData.visibility = View.VISIBLE
            } else {
                setupWalkGraphForHaveData(walkData)
                binding.tvWalkData.visibility = View.GONE
            }
        }

        homeViewModel.weatherInfo.observe(viewLifecycleOwner) { weatherInfo ->
            updateWeatherUI(weatherInfo)
        }
    }


    private fun updateWeatherUI(weatherInfo: WeatherInfo) {
        with(binding) {
            val weatherCondition = weatherInfo.condition
            ivWeatherIcon.setImageResource(getWeatherIconResource(weatherCondition)) // 실제 날씨 아이콘으로 변경 필요
            tvLocation.text = weatherInfo.city
            tvLocationTemperature.text = weatherInfo.temperature
            tvLocationConditions.text = weatherInfo.condition
            ivFineDustIcon.setImageResource(weatherInfo.fineDustStatusIcon)
            ivUltraFineDustIcon.setImageResource(weatherInfo.ultraFineDustStatusIcon)
        }
    }

    private fun getWeatherIconResource(condition: String): Int {
        return when (condition) {
            "천둥번개" -> R.drawable.ic_weather_thunder
            "비" -> R.drawable.ic_weather_rain
            "약간 비" -> R.drawable.ic_weather_slight_rain
            "눈" -> R.drawable.ic_weather_snow
            "안개", "황사", "태풍" -> R.drawable.ic_weather_typoon_dust_fog
            "맑음" -> R.drawable.ic_weather_sunny
            "약간 흐림" -> R.drawable.ic_weather_slightly_cloudy
            "흐림" -> R.drawable.ic_weather_cloudy
            "많이 흐림" -> R.drawable.ic_weather_very_cloudy
            else -> R.drawable.ic_x
        }
    }

    private fun checkLocationPermissions() {
        locationPermissionRequest.launch(arrayOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ))
    }

    private fun getLastLocation() {
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())
        try {
            fusedLocationClient.lastLocation
                .addOnSuccessListener { location: Location? ->
                    location?.let {
                        val lat = it.latitude.toString()
                        val lon = it.longitude.toString()
                        homeViewModel.loadWeather(lat, lon)
                   }
                }
        } catch (e: SecurityException) {
            e.printStackTrace()
        }
    }

    private fun moveToAdd() {
        val intent = Intent(context, AddActivity::class.java)
        startActivity(intent)
    }

    private fun moveToHistory() {
        binding.cvGraph.setOnClickListener {
            val dogInfo = homeViewModel.selectedDogInfo.value
            if (dogInfo != null) {
                val intent = Intent(context, HistoryActivity::class.java)
                intent.putExtra("DOG_INFO", dogInfo)
                startActivity(intent)
            } else {
                Toast.makeText(context, "선택된 반려견이 없습니다.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setupWalkGraphForHaveData(walkData: List<WalkingInfo>) {
        val lineChart = binding.lcArea
        walkGraphSettingsForHaveData(lineChart)
        walkGraphXAxisForHaveData(lineChart.xAxis)

        val entries = ArrayList<Entry>()
        val dateDistanceMap = walkData.groupBy { DateFormatter.formatDate(it.startDateTime) }
            .mapValues { entry -> entry.value.sumOf { it.distance ?: 0.0 } }

        DateFormatter.getLast7Days().forEachIndexed { index, date ->
            val distance = dateDistanceMap[date] ?: 0.0
            Log.d("WalkGraph", "Date: $date, Distance: $distance")
            entries.add(Entry(index.toFloat(), distance.toFloat()))
        }

        val maxDistance = entries.maxOfOrNull { it.y } ?: 0f
        walkGraphYAxisForHaveData(lineChart.axisLeft, maxDistance)

        val dataSet = LineDataSet(entries, "").apply {
            axisDependency = YAxis.AxisDependency.LEFT
            color = Color.parseColor("#7598c9") // 라인 색상
            valueTextColor = resources.getColor(R.color.black, null)
            lineWidth = 2f
            setDrawCircles(true)
            setCircleColor(Color.parseColor("#7598c9")) // 라인 꼭짓점 색상
            setDrawCircleHole(true)
            setDrawValues(true)
            mode = LineDataSet.Mode.LINEAR
        }
        val lineData = LineData(dataSet)
        lineChart.data = lineData

        lineChart.invalidate()
    }

    private fun walkGraphSettingsForHaveData(lineChart: LineChart) {
        /* 라인차트 생성 및 화면 설정 */
        lineChart.apply {
            axisRight.isEnabled = false // 차트의 오른쪽 Y축 표시 여부
            legend.isEnabled = false // 범례 표시 여부
            description.isEnabled = false // 범례 옆에 표시되는 차트 설명 사용 여부
            setDrawGridBackground(true) // 차트의 안쪽 색깔 지정 여부
            setGridBackgroundColor(resources.getColor(R.color.grey, null)) // 차트의 안쪽 색깔 지정
            setTouchEnabled(false) // 차트 터치 여부
            setPinchZoom(false) // 차트 확대, 축소 여부 (손가락으로 확대 축소)
            setScaleEnabled(false) // 차트 확대 여부
            isDragXEnabled = false // 차트의 X축 드래그 여부
            isDragYEnabled = false // 차트의 Y축 드래그 여부
        }
        lineChart.invalidate() // 차트 갱신
    }

    private fun walkGraphXAxisForHaveData(xAxis: XAxis) {
        val dates = DateFormatter.getLast7Days()
        val formatter = object : ValueFormatter() {
            override fun getFormattedValue(value: Float): String {
                val index = value.toInt()
                return if (index >= 0 && index < dates.size) dates[index] else ""
            }
        }

        /* 차트의 X축 설정 */
        xAxis.apply {
            position = XAxis.XAxisPosition.BOTTOM // X축의 위치 설정
            setLabelCount(7, true) // X축에 표시될 레이블의 개수 설정, force = 어떠한 변화가 있어도 강제로 7개만 보이도록
            axisMinimum = 0f // X축의 최솟값 설정
            axisMaximum = 6f // X축의 최댓값 설정
            valueFormatter = formatter // X축 실시간 날짜 설정
        }
    }

    private fun walkGraphYAxisForHaveData(yAxis: YAxis, maxDistance: Float) {
        yAxis.apply {
            axisMinimum = 0f // y축의 최솟값 설정
            axisMaximum = when { // y축의 최댓값 설정
                // 최대 거리 + 1을 해서 y축의 최댓값을 지정함 ( Int로 변환 )
                maxDistance >= 3 -> (maxDistance / 1).toInt() * 1 + 1f
                maxDistance >= 1 -> (maxDistance / 0.5).toInt() * 0.5f + 0.5f
                else -> (maxDistance / 0.1).toInt() * 0.1f + 0.1f
            }

            valueFormatter = object : ValueFormatter() {
                override fun getFormattedValue(value: Float): String {
                    return "${value}km"
                }
            }
        }
    }

    private fun setupWalkGraphForEmptyData() {
        /* 산책 데이터가 없을시 초기 화면 */
        val lineChart = binding.lcArea
        walkGraphSettingsForEmptyData(lineChart)
        walkGraphXAxisForEmptyData(lineChart.xAxis)
        walkGraphYAxisForEmptyData(lineChart.axisLeft)
    }

    private fun walkGraphSettingsForEmptyData(lineChart: LineChart) {
        /* 라인차트 생성및 화면설정*/
        lineChart.data = LineData()

        lineChart.apply {
            axisRight.isEnabled = false // 차트의 오른쪽 Y축 표시 여부
            legend.isEnabled = false // 범례 표시 여부
            description.isEnabled = false // 범례 옆에 표시되는 차트 설명 사용 여부
            setDrawGridBackground(true) // 차트의 안쪽 색깔 지정 여부
            setGridBackgroundColor(resources.getColor(R.color.grey, null)) // 차트의 안쪽 색깔 지정
            setTouchEnabled(false) // 차트 터치 여부
            setPinchZoom(false) // 차트 확대,축소 여부 (손가락으로 확대 축소)
            setScaleEnabled(false) // 차트 확대 여부
            isDragXEnabled = false // 차트의 x축 드래그 여부
            isDragYEnabled = false // 차트의 y축 드래그 여부
        }
        lineChart.invalidate() // 차트 갱신
    }

    private fun walkGraphXAxisForEmptyData(xAxis: XAxis) {
        val dates = DateFormatter.getLast7Days()
        val formatter = object : ValueFormatter() {
            override fun getFormattedValue(value: Float): String {
                val index = value.toInt()
                return if (index >= 0 && index < dates.size) dates[index] else ""
            }
        }

        /* 차트의 X축 설정 */
        xAxis.apply {
            position = XAxis.XAxisPosition.BOTTOM // X축의 위치 설정
            setLabelCount(7, true) // X축에 표시될 레이블의 개수 설정, force = 어떠한 변화가 있어도 강제로 7개만 보이도록
            axisMinimum = 0f // X축의 최솟값 설정
            axisMaximum = 6f // X축의 최댓값 설정
            valueFormatter = formatter // X축 실시간 날짜 설정
        }
    }

    private fun walkGraphYAxisForEmptyData(yAxis: YAxis) {
        /* 차트의 y축 설정 */
        yAxis.apply {
            setLabelCount(5, true) // y축에 표시될 레이블의 갯수 설정, force = 어떠한 변화가 있어도 강제로 5개만 보이도록
            axisMinimum = 1f // y축의 최솟값
            axisMaximum = 5f // y축의 최댓값
            // (y축)에 km를 붙이기 위한 작업
            valueFormatter = object : ValueFormatter() {
                override fun getFormattedValue(value: Float): String {
                    return when (value) {
                        1f -> "1km"
                        2f -> "3km"
                        3f -> "6km"
                        4f -> "9km"
                        5f -> "12km"
                        else -> ""
                    }
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}