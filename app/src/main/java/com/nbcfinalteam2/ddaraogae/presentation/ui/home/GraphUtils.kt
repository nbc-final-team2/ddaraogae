package com.nbcfinalteam2.ddaraogae.presentation.ui.home

import android.content.Context
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.formatter.ValueFormatter
import com.nbcfinalteam2.ddaraogae.R
import com.nbcfinalteam2.ddaraogae.presentation.util.DateFormatter

object GraphUtils {

    // Home 관련 Empty 함수
    fun homeSetupWalkGraphSettingsForEmptyData(lineChart: LineChart, context: Context) {
        lineChart.data = LineData()

        lineChart.apply {
            axisRight.isEnabled = false
            legend.isEnabled = false
            description.isEnabled = false
            setDrawGridBackground(true)
            setGridBackgroundColor(context.getColor(R.color.grey))
            setTouchEnabled(false)
            setPinchZoom(false)
            setScaleEnabled(false)
            isDragXEnabled = false
            isDragYEnabled = false
        }
        lineChart.invalidate()
    }

    fun homeSetupWalkGraphXAxisForEmptyData(xAxis: XAxis, valueFormatter: ValueFormatter) {
        xAxis.apply {
            position = XAxis.XAxisPosition.BOTTOM
            setLabelCount(7, true)
            axisMinimum = 0f
            axisMaximum = 6f
            this.valueFormatter = valueFormatter
        }
    }

    fun homeSetupWalkGraphYAxisForEmptyData(yAxis: YAxis) {
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

    // History 관련 Empty 함수
    fun historySetupWalkGraphSettingsForEmptyData(lineChart: LineChart, context: Context) {
        lineChart.data = LineData()

        lineChart.apply {
            axisRight.isEnabled = false
            legend.isEnabled = false
            description.isEnabled = false
            setDrawGridBackground(true)
            setGridBackgroundColor(context.getColor(R.color.grey))
            setTouchEnabled(false)
            setPinchZoom(false)
            setScaleEnabled(false)
            isDragXEnabled = false
            isDragYEnabled = false
        }
        lineChart.invalidate()
    }

    fun historySetupWalkGraphXAxisForEmptyData(xAxis: XAxis, year: Int, month: Int) {
        val dates = DateFormatter.generateDatesForMonth(year, month)
        xAxis.apply {
            position = XAxis.XAxisPosition.BOTTOM
            setLabelCount(7, true) // x축 레이블 개수 7로 고정
            axisMinimum = 0f
            axisMaximum = (dates.size - 1).toFloat()
            valueFormatter = object : ValueFormatter() {
                override fun getFormattedValue(value: Float): String {
                    val index = value.toInt()
                    return if (index >= 0 && index < dates.size) dates[index] else ""
                }
            }
        }
    }

    fun historySetupWalkGraphYAxisForEmptyData(yAxis: YAxis) {
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
}