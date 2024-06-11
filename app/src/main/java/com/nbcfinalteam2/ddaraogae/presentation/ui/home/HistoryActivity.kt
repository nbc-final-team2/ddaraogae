package com.nbcfinalteam2.ddaraogae.presentation.ui.home

import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.ValueFormatter
import com.nbcfinalteam2.ddaraogae.R
import com.nbcfinalteam2.ddaraogae.databinding.ActivityHistoryBinding
import com.nbcfinalteam2.ddaraogae.presentation.model.DogInfo
import com.nbcfinalteam2.ddaraogae.presentation.model.WalkingInfo
import com.nbcfinalteam2.ddaraogae.presentation.util.DateFormatter
import dagger.hilt.android.AndroidEntryPoint
import java.util.Calendar
import java.util.Date

@AndroidEntryPoint
class HistoryActivity : AppCompatActivity(), HistoryOnClickListener {

    private lateinit var binding: ActivityHistoryBinding
    private val historyViewModel: HistoryViewModel by viewModels()
    private lateinit var dogInfo: DogInfo

    override fun onMonthClick(year: Int, monthNumber: Int) {
        Toast.makeText(this, "선택한 연도: $year, 월: $monthNumber", Toast.LENGTH_SHORT).show()
        historyViewModel.setSelectedDate(year, monthNumber)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHistoryBinding.inflate(layoutInflater)
        setContentView(binding.root)
        enableEdgeToEdge()
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        setupWalkGraph()
        setupListener()
        setupObserve()
        getDogInfo()
    }

    private fun setupWalkGraph() {
        setupWalkGraphForEmptyData()
    }

    private fun setupListener() {
        setupDatePicker()
        moveToBack()
    }

    private fun setupDatePicker() {
        binding.tvSelectedCalendar.setOnClickListener {
            val dialog = DialogFragment()
            dialog.setOnMonthClickListener(this)
            dialog.show(supportFragmentManager, "")
        }
    }

    private fun setupObserve() {
        historyViewModel.selectedDate.observe(this) { date ->
            binding.tvSelectedCalendar.text = date
        }

        historyViewModel.dogInfo.observe(this) { dog ->
            binding.tvWalkGraphDogName.text = "${dog.name}의 산책 그래프"
        }

        historyViewModel.walkData.observe(this) { walkDate ->
            if (walkDate.isEmpty()) {
                setupWalkGraphForEmptyData()
            } else {
                val yearOfMonth = historyViewModel.selectedDate.value?.split("년", "월") ?: listOf()
                val year = yearOfMonth.getOrNull(0)?.toIntOrNull() ?: ""
                val month = yearOfMonth.getOrNull(1)?.toIntOrNull() ?: ""
                // 데이터 있는 산책그래프 함수 넣기
            }
        }
    }

    private fun getDogInfo() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableExtra("DOG_INFO", DogInfo::class.java)?.let {
                dogInfo = it
                historyViewModel.setDogInfo(dogInfo)
            }
        } else {
            intent.getParcelableExtra<DogInfo>("DOG_INFO")?.let {
                dogInfo = it
                historyViewModel.setDogInfo(dogInfo)
            }
        }
    }

    private fun moveToBack() {
        binding.ivBack.setOnClickListener {
            finish()
        }
    }

    private fun setupWalkGraphForHaveData(walkData: List<WalkingInfo>, year: Int, month: Int) {
        val lineChart = binding.lcArea
        walkGraphSettingsForHaveData(lineChart)
        walkGraphXAxisForHaveData(lineChart.xAxis, year, month)
        walkGraphYAxisForHaveData(lineChart.axisLeft)

        val entries = ArrayList<Entry>()

        val sortedData = walkData.sortedBy { it.startDateTime }
        sortedData.forEach { walkingEntity ->
            val dayOfMonth = walkingEntity.startDateTime?.let { getDayOfMonth(it) } ?: 0
            entries.add(Entry(dayOfMonth.toFloat(), walkingEntity.distance?.toFloat() ?: 0f)) // nullable 처리
        }

        val dataSet = LineDataSet(entries, "").apply {
            axisDependency = YAxis.AxisDependency.LEFT
            color = resources.getColor(R.color.grey, null)
            valueTextColor = resources.getColor(R.color.black, null)
            lineWidth = 2f
            setDrawCircles(true)
            setDrawCircleHole(false)
            setDrawValues(true)
        }
        val lineData = LineData(dataSet)
        lineChart.data = lineData
        lineChart.invalidate()
    }

    private fun getDayOfMonth(date: Date): Int {
        val calendar = Calendar.getInstance()
        calendar.time = date
        return calendar.get(Calendar.DAY_OF_MONTH)
    }

    private fun walkGraphSettingsForHaveData(lineChart: LineChart) {
        /* 라인차트 생성 및 화면 설정 */
        lineChart.apply {
            axisRight.isEnabled = false // 차트의 오른쪽 Y축 표시 여부
            legend.isEnabled = false // 범례 표시 여부
            description.isEnabled = false // 범례 옆에 표시되는 차트 설명 사용 여부
            setDrawGridBackground(true) // 차트의 안쪽 색깔 지정 여부
            setGridBackgroundColor(resources.getColor(R.color.grey, null)) // 차트의 안쪽 색깔 지정
            setTouchEnabled(true) // 차트 터치 여부
            setPinchZoom(true) // 차트 확대, 축소 여부 (손가락으로 확대 축소)
            setScaleEnabled(true) // 차트 확대 여부
            isDragXEnabled = true // 차트의 X축 드래그 여부
            isDragYEnabled = true // 차트의 Y축 드래그 여부
        }
        lineChart.invalidate() // 차트 갱신
    }

    private fun walkGraphXAxisForHaveData(xAxis: XAxis, year: Int, month: Int) {
        val daysInMonth = DateFormatter.getDaysInMonth(year, month)
        /* 차트의 X축 설정 */
        xAxis.apply {
            position = XAxis.XAxisPosition.BOTTOM // X축의 위치 설정
            setLabelCount(daysInMonth, true) // X축에 표시될 레이블의 개수 설정
            axisMinimum = 1f // X축의 최솟값 설정
            axisMaximum = daysInMonth.toFloat() // X축의 최댓값 설정
        }
    }

    private fun walkGraphYAxisForHaveData(yAxis: YAxis) {
        /* 차트의 Y축 설정 */
        yAxis.apply {
            setLabelCount(5, true) // Y축에 표시될 레이블의 개수 설정, force = 강제로 5개만 보이도록
            axisMinimum = 1f // Y축의 최솟값
            axisMaximum = 5f // Y축의 최댓값
            // (Y축)에 km를 붙이기 위한 작업
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
    }

    private fun walkGraphXAxisForEmptyData(xAxis: XAxis) {
        /* 차트의 x축 설정 */
        xAxis.apply {
            position = XAxis.XAxisPosition.BOTTOM // X축의 위치 설정
            setLabelCount(7, true) // x축에 표시될 레이블의 갯수 설정, force = 어떠한 변화가 있어도 강제로 7개만 보이도록
            axisMinimum = 1f // x축의 최솟값 설정
            axisMaximum = 7f // x축의 최댓값 설정
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
}