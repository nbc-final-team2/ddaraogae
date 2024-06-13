package com.nbcfinalteam2.ddaraogae.presentation.ui.home

import android.graphics.Color
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

@AndroidEntryPoint
class HistoryActivity : AppCompatActivity(), HistoryOnClickListener {

    private lateinit var binding: ActivityHistoryBinding
    private val historyViewModel: HistoryViewModel by viewModels()
    private lateinit var dogInfo: DogInfo

    override fun onMonthClick(year: Int, monthNumber: Int) {
        Toast.makeText(this, "선택한 연도: $year, 월: $monthNumber", Toast.LENGTH_SHORT).show()
        historyViewModel.setSelectedDate(year, monthNumber)
        DateFormatter.generateDatesForMonth(year, monthNumber)
        setupWalkGraphForEmptyData(year, monthNumber)
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
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH) + 1
        setupWalkGraphForEmptyData(year, month)
    }

    private fun setupWalkGraphForEmptyData(year: Int, month: Int) {
        val lineChart = binding.lcArea
        walkGraphSettingsForEmptyData(lineChart)
        walkGraphXAxisForEmptyData(lineChart.xAxis, year, month)
        walkGraphYAxisForEmptyData(lineChart.axisLeft)
    }

    private fun walkGraphSettingsForEmptyData(lineChart: LineChart) {
        lineChart.data = LineData()

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

    private fun walkGraphXAxisForEmptyData(xAxis: XAxis, year: Int, month: Int) {
        DateFormatter.generateDatesForMonth(year, month)
        xAxis.apply {
            position = XAxis.XAxisPosition.BOTTOM
            setLabelCount(7, true) // x축 레이블 개수 7로 고정
            axisMinimum = 1f // 해당 월의 첫날
            axisMaximum = DateFormatter.getDaysInMonth(year, month).toFloat() // 해당 월의 말일
            valueFormatter = object : ValueFormatter() {
                override fun getFormattedValue(value: Float): String {
                    val index = value.toInt() - 1
                    return if (index >= 0 && index < DateFormatter.getDatesForMonth().size) DateFormatter.getDatesForMonth()[index] else ""
                }
            }
        }
    }

    private fun walkGraphYAxisForEmptyData(yAxis: YAxis) {
        yAxis.apply {
            setLabelCount(5, true)
            axisMinimum = 1f
            axisMaximum = 5f
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

        historyViewModel.walkData.observe(this) { walkData ->
            val (year, month) = historyViewModel.getSelectedYearMonth()
            if (walkData.isEmpty()) {
                setupWalkGraphForEmptyData(year, month)
            } else {
                setupWalkGraphForHaveData(walkData, year, month)
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
        val dateDistanceMap = walkData.groupBy { DateFormatter.formatDate(it.startDateTime) }
            .mapValues { entry -> entry.value.sumOf { it.distance ?: 0.0 } }

        DateFormatter.getDatesForMonth().forEachIndexed { index, date ->
            val distance = dateDistanceMap[date] ?: 0.0
            entries.add(Entry((index + 1).toFloat(), distance.toFloat()))
        }

        val dataSet = LineDataSet(entries, "").apply {
            axisDependency = YAxis.AxisDependency.LEFT
            color = Color.parseColor("#7598c9")
            valueTextColor = resources.getColor(R.color.black, null)
            lineWidth = 2f
            setDrawCircles(true)
            setCircleColor(Color.parseColor("#7598c9"))
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
            setTouchEnabled(true)
            setPinchZoom(true)
            setScaleEnabled(true)
            isDragXEnabled = true
            isDragYEnabled = true
        }
        lineChart.invalidate()
    }

    private fun walkGraphXAxisForHaveData(xAxis: XAxis, year: Int, month: Int) {
        val dates = DateFormatter.getDatesForMonth()
        xAxis.apply {
            position = XAxis.XAxisPosition.BOTTOM
            setLabelCount(dates.size, true)
            axisMinimum = 1f
            axisMaximum = dates.size.toFloat()
            valueFormatter = object : ValueFormatter() {
                override fun getFormattedValue(value: Float): String {
                    val index = value.toInt() - 1
                    return if (index >= 0 && index < dates.size) dates[index] else ""
                }
            }
        }
    }

    private fun walkGraphYAxisForHaveData(yAxis: YAxis) {
        yAxis.apply {
            setLabelCount(5, true)
            axisMinimum = 0f
            axisMaximum = 10f
            valueFormatter = object : ValueFormatter() {
                override fun getFormattedValue(value: Float): String {
                    return "${value}km"
                }
            }
        }
    }
}