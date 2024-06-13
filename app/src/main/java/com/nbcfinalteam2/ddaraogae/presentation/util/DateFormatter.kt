package com.nbcfinalteam2.ddaraogae.presentation.util

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.TimeZone

object DateFormatter {

    private val timeZone = TimeZone.getTimeZone("Asia/Seoul")
    private val dateFormat = SimpleDateFormat("MM/dd", Locale.getDefault()).apply {
        timeZone = this@DateFormatter.timeZone
    }

    fun generateLast7Days(): List<String> {
        val calendar = Calendar.getInstance()
        return (0 until 7).map {
            dateFormat.format(calendar.time).also {
                calendar.add(Calendar.DATE, -1)
            }
        }.reversed()
    }

    fun formatDate(date: Date?): String {
        return date?.let { dateFormat.format(it) } ?: ""
    }

    fun getStartDateForWeek(): Date {
        val calendar = Calendar.getInstance()
        calendar.timeZone = timeZone
        calendar.add(Calendar.DAY_OF_YEAR, -6)
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        return calendar.time
    }

    fun getEndDateForWeek(): Date {
        val calendar = Calendar.getInstance()
        calendar.timeZone = timeZone
        return calendar.time
    }

    fun getStartDateForAllDay(year: Int, month: Int): Date {
        val calendar = Calendar.getInstance()
        calendar.timeZone = timeZone
        calendar.set(year, month - 1, 1, 0, 0, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        return calendar.time
    }

    fun getEndDateForAllDay(year: Int, month: Int): Date {
        val calendar = Calendar.getInstance()
        calendar.timeZone = timeZone
        calendar.set(year, month - 1, 1, 0, 0, 0)
        calendar.add(Calendar.MONTH, 1)
        calendar.add(Calendar.MILLISECOND, -1)
        return calendar.time
    }

    fun testDate(dateStr: String): Date? {
        return dateFormat.parse(dateStr)
    }

    fun generateDatesForMonth(year: Int, month: Int): List<String> {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.YEAR, year)
        calendar.set(Calendar.MONTH, month - 1)
        calendar.set(Calendar.DAY_OF_MONTH, 1)

        val daysInMonth = calendar.getActualMaximum(Calendar.DAY_OF_MONTH)
        return (1..daysInMonth).map { day ->
            calendar.set(Calendar.DAY_OF_MONTH, day)
            dateFormat.format(calendar.time)
        }
    }
}
