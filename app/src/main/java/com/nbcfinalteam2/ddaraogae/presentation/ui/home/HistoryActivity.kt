package com.nbcfinalteam2.ddaraogae.presentation.ui.home

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.formatter.ValueFormatter
import com.nbcfinalteam2.ddaraogae.R
import com.nbcfinalteam2.ddaraogae.databinding.ActivityHistoryBinding

class HistoryActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHistoryBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityHistoryBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        setupWalkGraph()
        setupListener()
    }

    private fun setupWalkGraph() {
        setupWalkGraphForEmptyData()
    }

    private fun setupListener() {
        moveToBack()
    }

    private fun moveToBack() {
        binding.ivBack.setOnClickListener {
            finish()
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