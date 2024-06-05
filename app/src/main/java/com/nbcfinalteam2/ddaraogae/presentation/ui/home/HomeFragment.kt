package com.nbcfinalteam2.ddaraogae.presentation.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
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

    private fun setupWalkGraphForEmptyData() {
        /* 산책 데이터가 없을시 초기 화면 */

        val lineChart = binding.lcArea

        // 차트 데이터를 넣지 않으면  차트 그림이 사라짐
        val entries = ArrayList<Entry>()
        for (i in 1..7) {
            entries.add(Entry(i.toFloat(), 0f))
        }

        val dataSet = LineDataSet(entries, "").apply {
            axisDependency = YAxis.AxisDependency.LEFT
            color = resources.getColor(R.color.grey, null)
            valueTextColor = resources.getColor(R.color.black, null)
            lineWidth = 2f // 라인 두께
            setDrawCircles(false) // 마커 표시 여부
            setDrawCircleHole(false)
            setDrawValues(false) // 데이터 값 표시 여부 ( 차트 안에 그릴것인지 )
        }

        val lineData = LineData(dataSet)
        lineChart.data = lineData

        // x축 (아래쪽)
        val xAxis: XAxis = lineChart.xAxis
        xAxis.position = XAxis.XAxisPosition.BOTTOM
        xAxis.granularity = 1f
        xAxis.setLabelCount(7, true)
        xAxis.axisMinimum = 1f
        xAxis.axisMaximum = 7f
        xAxis.valueFormatter = object : ValueFormatter() {
            override fun getFormattedValue(value: Float): String {
                return "${value.toInt()}"
            }
        }

        // y축 (왼쪽)
        val yAxisLeft: YAxis = lineChart.axisLeft
        yAxisLeft.axisMinimum = 0f
        yAxisLeft.axisMaximum = 12f
        yAxisLeft.granularity = 3f
        yAxisLeft.setLabelCount(5, true)
        yAxisLeft.valueFormatter = object : ValueFormatter() {
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

        val yAxisRight: YAxis = lineChart.axisRight
        yAxisRight.isEnabled = false

        val legend = lineChart.legend
        legend.isEnabled = false // 범례 여부

        lineChart.setDrawGridBackground(true) // 차트(그리드) 배경색 설정
        lineChart.setGridBackgroundColor(resources.getColor(R.color.grey, null))

        lineChart.description.isEnabled = false // 차트 설명

        lineChart.invalidate() // 차트 새로고침
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}