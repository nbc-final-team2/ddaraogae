package com.nbcfinalteam2.ddaraogae.presentation.ui.home

import android.content.Context
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.formatter.ValueFormatter
import com.nbcfinalteam2.ddaraogae.R

object GraphUtils {

    fun setupWalkGraphSettingsForEmptyData(lineChart: LineChart, context: Context) {
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

    fun setupWalkGraphXAxisForEmptyData(xAxis: XAxis, valueFormatter: ValueFormatter) {
        xAxis.apply {
            position = XAxis.XAxisPosition.BOTTOM
            setLabelCount(7, true)
            axisMinimum = 0f
            axisMaximum = 6f
            this.valueFormatter = valueFormatter
        }
    }

    fun setupWalkGraphYAxisForEmptyData(yAxis: YAxis) {
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