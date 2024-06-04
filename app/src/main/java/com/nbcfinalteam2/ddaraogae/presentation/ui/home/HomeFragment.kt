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
        val lineChart = binding.lcArea

        // 차트 데이터 ( x축과 y축 )
        val entries = ArrayList<Entry>()
        entries.add(Entry(1f, 3f))
        entries.add(Entry(2f, 7f))
        entries.add(Entry(3f, 6f))
        entries.add(Entry(4f, 10f))
        entries.add(Entry(5f, 4f))
        entries.add(Entry(6f, 8f))
        entries.add(Entry(7f, 6f))

        val dataSet = LineDataSet(entries, "").apply {
            axisDependency = YAxis.AxisDependency.LEFT
            color = resources.getColor(androidx.constraintlayout.widget.R.color.material_blue_grey_950, null)
            valueTextColor = resources.getColor(R.color.black, null)
            lineWidth = 2f // 라인 두께
            circleRadius = 4f // 원 크기
            setDrawCircleHole(false)
            setDrawValues(true)
        }

        val lineData = LineData(dataSet)
        lineChart.data = lineData

        // x축 ( 아래쪽 )
        val xAxis: XAxis = lineChart.xAxis
        xAxis.position = XAxis.XAxisPosition.BOTTOM
        xAxis.granularity = 1f
        xAxis.setLabelCount(7, true)
        xAxis.valueFormatter = object : ValueFormatter() {
            override fun getFormattedValue(value: Float): String {
                return "${value.toInt()}일"
            }
        }

        // y축 ( 왼쪽 )
        val yAxisLeft: YAxis = lineChart.axisLeft
        yAxisLeft.axisMinimum = 0f
        yAxisLeft.granularity = 0.5f
        yAxisLeft.valueFormatter = object : ValueFormatter() {
            override fun getFormattedValue(value: Float): String {
                return when (value) {
                    0.5f -> "0.5km"
                    1f -> "1km"
                    2f -> "2km"
                    3f -> "3km"
                    5f -> "5km"
                    else -> "${value.toInt()}km"
                }
            }
        }

        val yAxisRight: YAxis = lineChart.axisRight
        yAxisRight.isEnabled = false

        // 범례 ( 차트 아래에 범례를 표시할지 말지 )
        val legend = lineChart.legend
        legend.isEnabled = false

        lineChart.description.isEnabled = false
        lineChart.invalidate() // 차트 새로고침
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}