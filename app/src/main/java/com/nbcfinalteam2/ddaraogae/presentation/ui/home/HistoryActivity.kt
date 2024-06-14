package com.nbcfinalteam2.ddaraogae.presentation.ui.home

import android.os.Build
import android.os.Bundle
import android.view.View
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
    private lateinit var walkHistoryAdapter: WalkHistoryAdapter

    override fun onMonthClick(year: Int, monthNumber: Int) {
        Toast.makeText(this, "선택한 연도: $year, 월: $monthNumber", Toast.LENGTH_SHORT).show()
        historyViewModel.setSelectedDate(year, monthNumber)
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
        setupAdapter()
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
        GraphUtils.historySetupWalkGraphSettingsForEmptyData(lineChart, this)
        GraphUtils.historySetupWalkGraphXAxisForEmptyData(lineChart.xAxis, year, month)
        GraphUtils.historySetupWalkGraphYAxisForEmptyData(lineChart.axisLeft)
    }

    private fun setupAdapter() {
        walkHistoryAdapter = WalkHistoryAdapter(
            onPolyLineClick = { /* 이미지 클릭 이벤트 넣을것 */ }
        )
        binding.rvWalkHistoryArea.adapter = walkHistoryAdapter
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
                binding.tvWalkData.visibility = View.VISIBLE
                binding.tvWalkHistoryData.visibility = View.VISIBLE
                binding.rvWalkHistoryArea.visibility = View.GONE

            } else {
                setupWalkGraphForHaveData(walkData, year, month)
                binding.tvWalkData.visibility = View.GONE
                walkHistoryAdapter.submitList(walkData)
                binding.tvWalkHistoryData.visibility = View.GONE
                binding.rvWalkHistoryArea.visibility = View.VISIBLE
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

        val entries = ArrayList<Entry>()
        val dateDistanceMap = walkData.groupBy { DateFormatter.formatDate(it.startDateTime) }
            .mapValues { entry -> entry.value.sumOf { it.distance ?: 0.0 } }

        val dates = DateFormatter.generateDatesForMonth(year, month)

        for (i in dates.indices) {
            val date = dates[i]
            val distance = dateDistanceMap[date] ?: 0.0
            entries.add(Entry(i.toFloat(), distance.toFloat()))
        }

        val maxDistance = entries.maxOfOrNull { it.y } ?: 0f

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

        walkGraphYAxisForHaveData(lineChart.axisLeft, maxDistance)

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
            setPinchZoom(false)
            setScaleEnabled(false)
            isDragXEnabled = true
            isDragYEnabled = false
            setVisibleXRange(0f, 7f)
        }
        lineChart.invalidate()
    }

    private fun walkGraphXAxisForHaveData(xAxis: XAxis, year: Int, month: Int) {
        val dates = DateFormatter.generateDatesForMonth(year, month)
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
            axisMaximum = (dates.size - 1).toFloat()
            valueFormatter = formatter
            isGranularityEnabled = true
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
}