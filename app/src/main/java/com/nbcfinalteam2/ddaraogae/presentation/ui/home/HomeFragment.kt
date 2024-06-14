package com.nbcfinalteam2.ddaraogae.presentation.ui.home

import android.Manifest
import android.content.Intent
import android.location.Location
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
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

    private val locationPermissionRequest = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        when {
            permissions.getOrDefault(Manifest.permission.ACCESS_FINE_LOCATION, false) -> {
                getLastLocation()
                toggleWeatherVisible()
            }
            permissions.getOrDefault(Manifest.permission.ACCESS_COARSE_LOCATION, false) -> {
                getLastLocation()
                toggleWeatherVisible()
            }
            else -> {
                toggleWeatherInvisible()
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
            ivWeatherIcon.setImageResource(getWeatherIconResource(weatherCondition))
            tvLocation.text = weatherInfo.city
            tvLocationTemperature.text = weatherInfo.temperature
            tvLocationConditions.text = weatherInfo.condition
            ivFineDustIcon.setImageResource(weatherInfo.fineDustStatusIcon)
            ivUltraFineDustIcon.setImageResource(weatherInfo.ultraFineDustStatusIcon)
            tvFineDustConditions.text = weatherInfo.fineDustStatus
            tvUltraFineDustConditions.text = weatherInfo.ultraFineDustStatus
        }
    }

    private fun getWeatherIconResource(condition: String): Int {
        return when (condition) {
            getString(R.string.weather_status_thunder) -> R.drawable.ic_weather_thunder
            getString(R.string.weather_status_rain) -> R.drawable.ic_weather_rain
            getString(R.string.weather_status_slight_rain) -> R.drawable.ic_weather_slight_rain
            getString(R.string.weather_status_snow) -> R.drawable.ic_weather_snow
            getString(R.string.weather_status_typoon) -> R.drawable.ic_weather_typoon_dust_fog
            getString(R.string.weather_status_dust) -> R.drawable.ic_weather_typoon_dust_fog
            getString(R.string.weather_status_fog) -> R.drawable.ic_weather_typoon_dust_fog
            getString(R.string.weather_status_sunny) -> R.drawable.ic_weather_sunny
            getString(R.string.weather_status_slightly_cloudy) -> R.drawable.ic_weather_slightly_cloudy
            getString(R.string.weather_status_cloudy) -> R.drawable.ic_weather_cloudy
            getString(R.string.weather_status_very_cloudy) -> R.drawable.ic_weather_very_cloudy
            else -> R.drawable.ic_x
        }
    }

    private fun toggleWeatherVisible() {
        with(binding) {
            ivWeatherIcon.visibility = View.VISIBLE
            tvLocation.visibility = View.VISIBLE
            tvLocationTemperature.visibility = View.VISIBLE
            tvLocationConditions.visibility = View.VISIBLE
            tvFineDust.visibility = View.VISIBLE
            ivFineDustIcon.visibility = View.VISIBLE
            tvFineDustConditions.visibility = View.VISIBLE
            tvUltraFineDust.visibility = View.VISIBLE
            ivUltraFineDustIcon.visibility = View.VISIBLE
            tvUltraFineDustConditions.visibility = View.VISIBLE
            ivWeatherRenewal.visibility = View.VISIBLE
            tvTodayWeatherTime.visibility = View.VISIBLE
            tvWeatherData.visibility = View.GONE
        }
    }

    private fun toggleWeatherInvisible() {
        with(binding) {
            ivWeatherIcon.visibility = View.INVISIBLE
            tvLocation.visibility = View.INVISIBLE
            tvLocationTemperature.visibility = View.INVISIBLE
            tvLocationConditions.visibility = View.INVISIBLE
            tvFineDust.visibility = View.INVISIBLE
            ivFineDustIcon.visibility = View.INVISIBLE
            tvFineDustConditions.visibility = View.INVISIBLE
            tvUltraFineDust.visibility = View.INVISIBLE
            ivUltraFineDustIcon.visibility = View.INVISIBLE
            tvUltraFineDustConditions.visibility = View.INVISIBLE
            ivWeatherRenewal.visibility = View.INVISIBLE
            tvTodayWeatherTime.visibility = View.INVISIBLE
            tvWeatherData.visibility = View.VISIBLE
        }
    }

    private fun checkLocationPermissions() {
        // 여기서 체크 셀프 펄미션
        // isgranted가 안되어있으면 locationPermissionRequest.launch 얘 실행
        // requireActivity = CONTEXT
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
            Toast.makeText(context, "위치 권한이 없습니다.", Toast.LENGTH_SHORT).show()
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
        walkGraphXAxisForHaveData(lineChart.xAxis, DateFormatter.generateLast7Days())

        val entries = ArrayList<Entry>()
        val dateDistanceMap = walkData.groupBy { DateFormatter.formatDate(it.startDateTime) }
            .mapValues { entry -> entry.value.sumOf { it.distance ?: 0.0 } }

        val dates = DateFormatter.generateLast7Days()
        dates.forEachIndexed { index, date ->
            val distance = dateDistanceMap[date] ?: 0.0
            entries.add(Entry(index.toFloat(), distance.toFloat()))
        }

        val maxDistance = entries.maxOfOrNull { it.y } ?: 0f
        walkGraphYAxisForHaveData(lineChart.axisLeft, maxDistance)

        val dataSet = LineDataSet(entries, "").apply {
            axisDependency = YAxis.AxisDependency.LEFT
            color = R.color.light_blue
            valueTextColor = resources.getColor(R.color.black, null)
            lineWidth = 2f
            setDrawCircles(true)
            setCircleColor(R.color.light_blue)
            setDrawCircleHole(true)
            setDrawValues(true)
            mode = LineDataSet.Mode.LINEAR
        }
        val lineData = LineData(dataSet)
        lineChart.data = lineData

        lineChart.invalidate()
    }

    private fun walkGraphSettingsForHaveData(lineChart: LineChart) {
        lineChart.apply {
            axisRight.isEnabled = false
            legend.isEnabled = false
            description.isEnabled = false
            setDrawGridBackground(true)
            setGridBackgroundColor(resources.getColor(R.color.grey, null))
            setTouchEnabled(false)
            setPinchZoom(false)
            setScaleEnabled(false)
            isDragXEnabled = false
            isDragYEnabled = false
        }
        lineChart.invalidate()
    }

    private fun walkGraphXAxisForHaveData(xAxis: XAxis, dates: List<String>) {
        val formatter = object : ValueFormatter() {
            override fun getFormattedValue(value: Float): String {
                val index = value.toInt()
                return if (index >= 0 && index < dates.size) dates[index] else ""
            }
        }

        xAxis.apply {
            position = XAxis.XAxisPosition.BOTTOM
            setLabelCount(7, true)
            axisMinimum = 0f
            axisMaximum = 6f
            valueFormatter = formatter
        }
    }

    private fun walkGraphYAxisForHaveData(yAxis: YAxis, maxDistance: Float) {
        yAxis.apply {
            axisMinimum = 0f
            axisMaximum = when {
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
        val lineChart = binding.lcArea
        GraphUtils.homeSetupWalkGraphSettingsForEmptyData(lineChart, requireContext())
        GraphUtils.homeSetupWalkGraphXAxisForEmptyData(lineChart.xAxis, object : ValueFormatter() {
            override fun getFormattedValue(value: Float): String {
                val dates = DateFormatter.generateLast7Days()
                val index = value.toInt()
                return if (index >= 0 && index < dates.size) dates[index] else ""
            }
        })
        GraphUtils.homeSetupWalkGraphYAxisForEmptyData(lineChart.axisLeft)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}