package com.nbcfinalteam2.ddaraogae.presentation.ui.home

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.ValueFormatter
import com.nbcfinalteam2.ddaraogae.R
import com.nbcfinalteam2.ddaraogae.databinding.FragmentHomeBinding
import com.nbcfinalteam2.ddaraogae.presentation.model.WalkingInfo
import com.nbcfinalteam2.ddaraogae.presentation.util.DateFormatter
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private lateinit var dogProfileAdapter: DogProfileAdapter
    private val homeViewModel: HomeViewModel by viewModels()

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
    }

    override fun onResume() {
        super.onResume()
        homeViewModel.loadDogs()
    }

    private fun setupAdapter() {
        dogProfileAdapter = DogProfileAdapter(
            onDogClick = { dogData -> homeViewModel.selectDog(dogData) },
            onAddClick = { moveToAdd() }
        )
        binding.rvDogArea.adapter = dogProfileAdapter
    }

    private fun observeViewModel() {
        homeViewModel.dogList.observe(viewLifecycleOwner) { dogs ->
            dogProfileAdapter.submitList(dogs)
        }

        homeViewModel.dogName.observe(viewLifecycleOwner) {dogName ->
            binding.tvDogGraph.text = "${dogName}의 산책 그래프"
        }
        /** 옵저빙 확인할것 */
        homeViewModel.walkData.observe(viewLifecycleOwner) { walkData ->
            if (walkData.isEmpty()) {
                setupWalkGraphForEmptyData()
                binding.tvWalkData.visibility = View.VISIBLE
            } else {
                setupWalkGraphForHaveData(walkData)
                binding.tvWalkData.visibility = View.GONE
            }
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

    /** 경로값 EmptyList로 주고 경로값 아무거나 */
    private fun setupWalkGraphForHaveData(walkData: List<WalkingInfo>) {
        val lineChart = binding.lcArea
        walkGraphSettingsForHaveData(lineChart)
        walkGraphXAxisForHaveData(lineChart.xAxis)

        val entries = ArrayList<Entry>()
        val dateDistanceMap = walkData.groupBy { DateFormatter.formatDate(it.startDateTime) }
            .mapValues { entry -> entry.value.sumOf { it.distance ?: 0.0 }}

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
        DateFormatter.generateLast7Days() // 날짜 생성

        /* 차트의 X축 설정 */
        xAxis.apply {
            position = XAxis.XAxisPosition.BOTTOM // X축의 위치 설정
            setLabelCount(7, true) // X축에 표시될 레이블의 개수 설정, force = 어떠한 변화가 있어도 강제로 7개만 보이도록
            axisMinimum = 0f // X축의 최솟값 설정
            axisMaximum = 6f // X축의 최댓값 설정
            valueFormatter = DateFormatter // X축 실시간 날짜 설정
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
        DateFormatter.generateLast7Days() // 날짜 생성

        /* 차트의 x축 설정 */
        xAxis.apply {
            position = XAxis.XAxisPosition.BOTTOM // X축의 위치 설정
            setLabelCount(7, true) // x축에 표시될 레이블의 갯수 설정, force = 어떠한 변화가 있어도 강제로 7개만 보이도록
            axisMinimum = 0f // x축의 최솟값 설정
            axisMaximum = 6f // x축의 최댓값 설정
            valueFormatter = DateFormatter // x축 실시간 날짜 설정
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

    private fun todayWeather() {
        with(binding) {
            val weatherIcon = ivWeatherIcon.setImageResource(R.drawable.ic_launcher_background)
            val location = tvLocation.text.toString()
            val fineDust = tvFineDust.text.toString()
            val ultraFineDust = tvUltraFineDust.text.toString()
            val locationTemperature = tvLocationTemperature.text.toString()
            val locationConditions = tvLocationConditions.text.toString()
            val fineDustStatusIcon = ivFineDustIcon.setImageResource(R.drawable.ic_launcher_background)
            val ultraFineDustStatusIcon = ivUltraFineDustIcon.setImageResource(R.drawable.ic_launcher_background)
        }
    }
}