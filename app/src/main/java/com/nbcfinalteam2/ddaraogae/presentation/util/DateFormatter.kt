package com.nbcfinalteam2.ddaraogae.presentation.util

import com.github.mikephil.charting.formatter.ValueFormatter
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.TimeZone

object DateFormatter : ValueFormatter() {

    private lateinit var dates: List<String>
    private val timeZone = TimeZone.getTimeZone("Asia/Seoul")
    private val dateFormat = SimpleDateFormat("MM/dd", Locale.getDefault()).apply {
        timeZone = this@DateFormatter.timeZone
    }

    fun generateLast7Days() {
        val calendar = Calendar.getInstance()
        dates = (0 until 7).map {
            dateFormat.format(calendar.time).also {
                calendar.add(Calendar.DATE, -1)
            }
        }.reversed()
    }

    fun getDates(): List<String> {
        if (!this::dates.isInitialized) {
            generateLast7Days()
        }
        return dates
    }

    override fun getFormattedValue(value: Float): String {
        val index = value.toInt()
        return if (index >= 0 && index < dates.size) dates[index] else ""
    }

    fun getStartDate(): Date {
        val calendar = Calendar.getInstance()
        calendar.timeZone = timeZone
        calendar.add(Calendar.DAY_OF_YEAR, -6)
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        return calendar.time
    }

    fun getEndDate(): Date {
        val calendar = Calendar.getInstance()
        calendar.timeZone = timeZone
        return calendar.time
    }
}