package com.nbcfinalteam2.ddaraogae.presentation.ui.home

import android.Manifest
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
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
import com.nbcfinalteam2.ddaraogae.domain.bus.ItemChangedEventBus
import com.nbcfinalteam2.ddaraogae.presentation.model.DefaultEvent
import com.nbcfinalteam2.ddaraogae.presentation.model.DogInfo
import com.nbcfinalteam2.ddaraogae.presentation.model.WalkingInfo
import com.nbcfinalteam2.ddaraogae.presentation.model.WeatherInfo
import com.nbcfinalteam2.ddaraogae.presentation.ui.add.AddActivity
import com.nbcfinalteam2.ddaraogae.presentation.ui.history.HistoryActivity
import com.nbcfinalteam2.ddaraogae.presentation.ui.stamp.StampActivity
import com.nbcfinalteam2.ddaraogae.presentation.util.DateFormatter
import com.nbcfinalteam2.ddaraogae.presentation.util.GraphUtils
import com.nbcfinalteam2.ddaraogae.presentation.util.ToastMaker
import dagger.hilt.android.AndroidEntryPoint
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class HomeFragment : Fragment() {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private val dogProfileAdapter: DogProfileAdapter by lazy {
        DogProfileAdapter { item ->
            onItemClick(item)
        }
    }
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private val homeViewModel: HomeViewModel by viewModels()
    @Inject
    lateinit var itemChangedEventBus: ItemChangedEventBus

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
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupWalkGraphForEmptyData()
        setupListener()
        setupAdapter()
        initViewModels()
        checkLocationPermissions()
    }

    private fun initViewModels() {

        viewLifecycleOwner.lifecycleScope.launch {
            homeViewModel.dogListState.flowWithLifecycle(viewLifecycleOwner.lifecycle)
                .collectLatest { dogList ->
                    dogProfileAdapter.submitList(dogList)
                    changeDogPortrait(dogList)
                }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            combine(
                homeViewModel.selectDogState,
                homeViewModel.selectDogWithTimeState
            ) { dogInfo, endDateTime ->
                Pair(dogInfo, endDateTime)
            }.flowWithLifecycle(viewLifecycleOwner.lifecycle).collectLatest { (dogInfo, endDateTime) ->
                if (dogInfo == null) {
                    binding.tvBeforeTime.text = getString(R.string.home_walk_add_dog)
                    binding.tvDogGraph.text = getString(R.string.home_walk_graph_title)
                } else {
                    binding.tvDogGraph.text = "${dogInfo.name}의 산책 그래프"
                    homeViewModel.loadSelectedDogWalkGraph()
                    if (endDateTime == null) {
                        binding.tvBeforeTime.text = getString(R.string.home_time_none)
                    } else if (endDateTime == 0) {
                        binding.tvBeforeTime.text = getString(R.string.home_time_just_now)
                    } else if (endDateTime < 24) {
                        binding.tvBeforeTime.text = "$endDateTime ${getString(R.string.home_a_few_hours_ago)}"
                    } else if (endDateTime > 24) {
                        binding.tvBeforeTime.text = getString(R.string.home_time_more_than_a_day)
                    }
                }
            }
        }


//        lifecycleScope.launch {
//            homeViewModel.selectDogState.flowWithLifecycle(viewLifecycleOwner.lifecycle)
//                .collectLatest { dogData ->
//                    if (dogData != null) {
//                        binding.tvDogGraph.text = "${dogData.name}의 산책 그래프"
//                        homeViewModel.loadSelectedDogWalkGraph()
//                    } else {
//                        binding.tvDogGraph.text = getString(R.string.home_walk_graph_title)
//                    }
//                }
//        }

        viewLifecycleOwner.lifecycleScope.launch {
            homeViewModel.walkListState.flowWithLifecycle(viewLifecycleOwner.lifecycle)
                .collectLatest { walkData ->
                    if (walkData.isEmpty()) {
                        setupWalkGraphForEmptyData()
                        binding.tvWalkData.visibility = View.VISIBLE
                    } else {
                        setupWalkGraphForHaveData(walkData)
                        binding.tvWalkData.visibility = View.GONE
                    }
                }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            homeViewModel.weatherInfoState.flowWithLifecycle(viewLifecycleOwner.lifecycle)
                .collectLatest { weatherInfo ->
                    updateWeatherUI(weatherInfo)
                }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            itemChangedEventBus.itemChangedEvent.flowWithLifecycle(viewLifecycleOwner.lifecycle)
                .collectLatest {
                    homeViewModel.refreshDogList()
                }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            homeViewModel.stampProgressState.flowWithLifecycle(viewLifecycleOwner.lifecycle)
                .collectLatest { progress ->
                    binding.progressbarWalkStampRate.progress = progress
                    binding.tvWalkStampRate.text = "14개 중 ${progress}개 흭득"
                }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            homeViewModel.loadDogEvent.flowWithLifecycle(viewLifecycleOwner.lifecycle)
                .collectLatest { event ->
                    when (event) {
                        is DefaultEvent.Failure -> ToastMaker.make(requireContext(), event.msg)
                        DefaultEvent.Success -> {}
                    }
                }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            homeViewModel.updateDogEvent.flowWithLifecycle(viewLifecycleOwner.lifecycle)
                .collectLatest { event ->
                    when (event) {
                        is DefaultEvent.Failure -> ToastMaker.make(requireContext(), event.msg)
                        DefaultEvent.Success -> {}
                    }
                }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            homeViewModel.loadWalkDataEvent.flowWithLifecycle(viewLifecycleOwner.lifecycle)
                .collectLatest { event ->
                    when (event) {
                        is DefaultEvent.Failure -> ToastMaker.make(requireContext(), event.msg)
                        DefaultEvent.Success -> {}
                    }
                }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            homeViewModel.loadWeatherEvent.flowWithLifecycle(viewLifecycleOwner.lifecycle)
                .collectLatest { event ->
                    when (event) {
                        is DefaultEvent.Failure -> {
                            ToastMaker.make(requireContext(), event.msg)
                            toggleWeatherInvisible()
                        }

                        DefaultEvent.Success -> {
                            toggleWeatherVisible()
                        }
                    }
                }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            homeViewModel.loadStampEvent.flowWithLifecycle(viewLifecycleOwner.lifecycle)
                .collectLatest { event ->
                    when (event) {
                        is DefaultEvent.Failure -> ToastMaker.make(requireContext(), event.msg)
                        DefaultEvent.Success -> {}
                    }
                }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            itemChangedEventBus.stampChangedEvent.flowWithLifecycle(viewLifecycleOwner.lifecycle)
                .collectLatest {
                    homeViewModel.loadStampProgress()
                }
        }
    }

    private fun changeDogPortrait(dogList: List<DogInfo>) {
        if (dogList.isEmpty()) {
            binding.ivDogAdd.visibility = CircleImageView.VISIBLE
            binding.rvDogArea.visibility = RecyclerView.INVISIBLE
        } else {
            binding.ivDogAdd.visibility = CircleImageView.INVISIBLE
            binding.rvDogArea.visibility = RecyclerView.VISIBLE
        }
    }

    private fun setupAdapter() {
        binding.rvDogArea.adapter = dogProfileAdapter
    }

    private fun onItemClick(dogData: DogInfo) {
        homeViewModel.selectDog(dogData)
    }

    private fun setupListener() {
        moveToHistory()
        /** 위치 권한 설정 이동 다이얼로그 */
        //checkForMoveToLocationSettingsDialog()
        weatherRefreshClickListener()

        binding.ivDogAdd.setOnClickListener {
            moveToAdd()
        }

        binding.tvMoveToAllStamp.setOnClickListener {
            val intent = Intent(context, StampActivity::class.java)
            startActivity(intent)
        }
    }

    private fun weatherRefreshClickListener() {
        binding.tvTodayWeatherTime.setOnClickListener {
            getLastLocation()
            binding.tvTodayWeatherTime.text = DateFormatter.getTodayDate()
        }

        binding.ivWeatherRenewal.setOnClickListener {
            getLastLocation()
            binding.tvTodayWeatherTime.text = DateFormatter.getTodayDate()
        }
    }

    private fun updateWeatherUI(weatherInfo: WeatherInfo?) {
        with(binding) {
            if (weatherInfo != null) {
                val weatherCondition = weatherInfo.condition
                ivWeatherIcon.setImageResource(getWeatherIconResource(getString(weatherCondition)))
                tvLocation.text = weatherInfo.city
                tvLocationTemperature.text = weatherInfo.temperature
                tvLocationConditions.text = getString(weatherInfo.condition)
                ivFineDustIcon.setImageResource(weatherInfo.fineDustStatusIcon)
                ivUltraFineDustIcon.setImageResource(weatherInfo.ultraFineDustStatusIcon)
                tvFineDustConditions.text = getString(weatherInfo.fineDustStatus)
                tvUltraFineDustConditions.text = getString(weatherInfo.ultraFineDustStatus)
            }
        }
    }

    private fun getWeatherIconResource(condition: String): Int {
        return when (condition) {
            getString(R.string.weather_status_thunder) -> R.drawable.ic_weather_thunder
            getString(R.string.weather_status_rain) -> R.drawable.ic_weather_rain
            getString(R.string.weather_status_slight_rain) -> R.drawable.ic_weather_rain
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
            tvFineDust.text = getString(R.string.home_weather_fine_dust)
            tvFineDustConditions.visibility = View.VISIBLE
            tvUltraFineDust.visibility = View.VISIBLE
            tvUltraFineDust.text = getString(R.string.home_weather_ultra_fine_dust)
            ivUltraFineDustIcon.visibility = View.VISIBLE
            tvUltraFineDustConditions.visibility = View.VISIBLE
            ivWeatherRenewal.visibility = View.VISIBLE
            tvTodayWeatherTime.visibility = View.VISIBLE
            tvWeatherData.visibility = View.GONE
            tvTodayWeatherTime.text = DateFormatter.getTodayDate()

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
        when (PackageManager.PERMISSION_GRANTED) {
            ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) -> {
                getLastLocation()
                toggleWeatherVisible()
            }

            ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) -> {
                getLastLocation()
                toggleWeatherVisible()
            }

            else -> {
                locationPermissionRequest.launch(
                    arrayOf(
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    )
                )
            }
        }
    }

    private fun checkForMoveToLocationSettingsDialog() {
        binding.tvWeatherData.setOnClickListener {
            AlertDialog.Builder(context)
                .setTitle(getString(R.string.home_dialog_location_title))
                .setMessage(getString(R.string.home_dialog_location_message))
                .setPositiveButton(getString(R.string.home_dialog_location_positive_button)) { _, _ ->
                    val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                    context?.startActivity(intent)
                }
                .setNegativeButton(
                    getString(R.string.home_dialog_location_negative_button)
                ) { p0, _ -> p0?.dismiss() }
                .setNeutralButton(
                    getString(R.string.home_dialog_location_neutral_button)
                ) { p0, _ -> p0?.dismiss() }
                .create()
                .show()
        }
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
            Toast.makeText(
                context,
                getString(R.string.home_toast_location_no_permission),
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun moveToAdd() {
        val intent = Intent(context, AddActivity::class.java)
        startActivity(intent)
    }

    private fun moveToHistory() {
        binding.tvMoveToHistoryGraph.setOnClickListener {
            val dogInfo = homeViewModel.selectDogState.value
            if (dogInfo != null) {
                val intent = Intent(context, HistoryActivity::class.java)
                intent.putExtra("DOG_INFO", dogInfo)
                startActivity(intent)
            } else {
                Toast.makeText(
                    context,
                    getString(R.string.home_no_selected_dog),
                    Toast.LENGTH_SHORT
                ).show()
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

        val dataSet = LineDataSet(entries, "최근 1주일 산책 그래프").apply {
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
            legend.isEnabled = true
            description.isEnabled = false
            setDrawGridBackground(true)
            setGridBackgroundColor(resources.getColor(R.color.white, null))
            setTouchEnabled(true)
            setPinchZoom(true)
            setScaleEnabled(true)
            isDoubleTapToZoomEnabled = true
            isDragXEnabled = true
            isDragYEnabled = true
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
            setLabelCount(dates.size, false)
            axisMinimum = 0f
            axisMaximum = 6f
            granularity = 1f
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
                    return String.format("%.1fkm", value)
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