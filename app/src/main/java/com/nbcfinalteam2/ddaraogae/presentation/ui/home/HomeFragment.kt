package com.nbcfinalteam2.ddaraogae.presentation.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.ValueFormatter
import com.nbcfinalteam2.ddaraogae.R
import com.nbcfinalteam2.ddaraogae.databinding.FragmentHomeBinding

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

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
        setupWalkGraph()
    }

    private fun setupWalkGraph() {
        setupWalkGraphForEmptyData()
    }

    private fun walkGraphSettingsForEmptyData(lineChart: LineChart) {
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

    private fun walkGraphChartDataForEmptyData(lineChart: LineChart) {
        val walkData = ArrayList<Entry>().apply {
            for (i in 1..7) {
                add(Entry(i.toFloat(), 0f))
            }
        }

        val dataSet = LineDataSet(walkData, "").apply {
            axisDependency = YAxis.AxisDependency.LEFT
            color = resources.getColor(R.color.grey, null)
            valueTextColor = resources.getColor(R.color.black, null)
            setDrawCircles(false)
            setDrawCircleHole(false)
            setDrawValues(false)
        }
        lineChart.data = LineData(dataSet)
    }

    private fun walkGraphXAxisForEmptyData(xAxis: XAxis) {
        xAxis.apply {
            position = XAxis.XAxisPosition.BOTTOM
            granularity = 1f
            setLabelCount(7, true)
            axisMinimum = 1f
            axisMaximum = 7f
            valueFormatter = object : ValueFormatter() {
                override fun getFormattedValue(value: Float): String {
                    return "${value.toInt()}"
                }
            }
        }
    }

    private fun walkGraphYAxisForEmptyData(yAxis: YAxis) {
        yAxis.apply {
            axisMinimum = 0f
            axisMaximum = 12f
            granularity = 3f
            setLabelCount(5, true)
            valueFormatter = object : ValueFormatter() {
                override fun getFormattedValue(value: Float): String {
                    return when (value) {
                        1f -> "1km"
                        3f -> "3km"
                        6f -> "6km"
                        9f -> "9km"
                        12f -> "12km"
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
        walkGraphChartDataForEmptyData(lineChart)
        walkGraphXAxisForEmptyData(lineChart.xAxis)
        walkGraphYAxisForEmptyData(lineChart.axisLeft)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}