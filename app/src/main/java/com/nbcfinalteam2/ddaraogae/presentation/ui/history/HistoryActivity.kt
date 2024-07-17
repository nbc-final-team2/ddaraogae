package com.nbcfinalteam2.ddaraogae.presentation.ui.history

import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.ValueFormatter
import com.nbcfinalteam2.ddaraogae.R
import com.nbcfinalteam2.ddaraogae.databinding.ActivityHistoryBinding
import com.nbcfinalteam2.ddaraogae.presentation.model.DefaultEvent
import com.nbcfinalteam2.ddaraogae.presentation.model.DogInfo
import com.nbcfinalteam2.ddaraogae.presentation.model.WalkingInfo
import com.nbcfinalteam2.ddaraogae.presentation.util.DateFormatter
import com.nbcfinalteam2.ddaraogae.presentation.util.GraphUtils
import com.nbcfinalteam2.ddaraogae.presentation.util.ToastMaker
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.util.Calendar
import kotlin.math.floor

@AndroidEntryPoint
class HistoryActivity : AppCompatActivity(), HistoryOnClickListener {

    private lateinit var binding: ActivityHistoryBinding
    private val historyViewModel: HistoryViewModel by viewModels()
    private lateinit var dogInfo: DogInfo
    private lateinit var walkHistoryAdapter: WalkHistoryAdapter

    override fun onMonthClick(year: Int, monthNumber: Int) {
        Toast.makeText(this, "선택한 연도: $year, 월: $monthNumber", Toast.LENGTH_SHORT).show()
        historyViewModel.setSelectedDate(year, monthNumber)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityHistoryBinding.inflate(layoutInflater)
        setContentView(binding.root)
        uiSetting()
        getDogInfo()
        if (savedInstanceState == null) {
            initData()
        }
        setupWalkGraph()
        setupAdapter()
        setupListener()
        setupViewModels()
    }

    private fun uiSetting() {
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { view, windowInsets ->
            val insets = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.updatePadding(insets.left, insets.top, insets.right, insets.bottom)
            WindowInsetsCompat.CONSUMED
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

    private fun initData() {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH) + 1
        historyViewModel.setSelectedDate(year, month)
    }

    private fun setupWalkGraph() {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH) + 1
        setupWalkGraphForEmptyData(year, month)
    }

    private fun setupAdapter() {
        walkHistoryAdapter = WalkHistoryAdapter(
            onMapClick = { walkingInfo ->
                val dialog = WalkHistoryMapDialog()
                dialog.setInfo(walkingInfo, dogInfo.name ?: "")
                dialog.show(supportFragmentManager, "")
            }
        )
        binding.rvWalkHistoryArea.adapter = walkHistoryAdapter
        binding.rvWalkHistoryArea.layoutManager = LinearLayoutManager(this)
        binding.rvWalkHistoryArea.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if (!binding.rvWalkHistoryArea.canScrollVertically(1)) {
                    historyViewModel.loadMoreWalkData()
                }
            }
        })
    }

    private fun setupListener() {
        setupDatePicker()
        moveToBack()
    }

    private fun setupDatePicker() {
        binding.tvSelectedCalendar.setOnClickListener {
            val dialog = CalendarDialog()
            dialog.setOnMonthClickListener(this)
            dialog.show(supportFragmentManager, "")
        }
    }

    private fun setupViewModels() {
        lifecycleScope.launch {
            historyViewModel.selectedDateState.flowWithLifecycle(lifecycle).collectLatest { date ->
                binding.tvSelectedCalendar.text = date
            }
        }
        lifecycleScope.launch {
            historyViewModel.selectDogState.flowWithLifecycle(lifecycle).collectLatest { dog ->
                binding.tvWalkGraphDogName.text = "${dog?.name}의 산책 그래프"
            }
        }
        lifecycleScope.launch {
            historyViewModel.walkListState.flowWithLifecycle(lifecycle).collectLatest { walkData ->
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

        lifecycleScope.launch {
            historyViewModel.loadWalkEvent.flowWithLifecycle(lifecycle).collectLatest { event ->
                when(event) {
                    is DefaultEvent.Failure -> ToastMaker.make(this@HistoryActivity, event.msg)
                    DefaultEvent.Success -> {}
                }
            }
        }
    }

    private fun moveToBack() {
        binding.ivBack.setOnClickListener {
            finish()
        }
    }

    private fun setupWalkGraphForEmptyData(year: Int, month: Int) {
        val lineChart = binding.lcArea
        GraphUtils.historySetupWalkGraphSettingsForEmptyData(lineChart, this)
        GraphUtils.historySetupWalkGraphXAxisForEmptyData(lineChart.xAxis, year, month)
        GraphUtils.historySetupWalkGraphYAxisForEmptyData(lineChart.axisLeft)
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
            setColor(resources.getColor(R.color.brown, null))
            lineWidth = 2f
            setDrawCircles(true)
            setCircleColor(R.color.light_blue)
            setDrawCircleHole(true)
            setDrawValues(true)
            mode = LineDataSet.Mode.CUBIC_BEZIER
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
            setGridBackgroundColor(resources.getColor(R.color.white, null))
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
            textColor = resources.getColor(R.color.black, null)
        }
    }

    private fun walkGraphYAxisForHaveData(yAxis: YAxis, maxDistance: Float) {
        yAxis.apply {
            axisMinimum = 0f

            val adjustedMax = (floor(maxDistance + 10).toInt() / 10) * 10 // 10을 더하고 1의 자리 버림
            val unit = adjustedMax / 5f // 5개 범위로 나눔

            axisMaximum = if (adjustedMax > 0) {
                adjustedMax.toFloat()
            } else {
                unit
            }

            valueFormatter = object : ValueFormatter() {
                override fun getFormattedValue(value: Float): String {
                    return "${value}km"
                }
            }

            textColor = resources.getColor(R.color.black, null)
        }
    }

    companion object {
        const val VISIBLE_THRESHOLD = 2
    }
}