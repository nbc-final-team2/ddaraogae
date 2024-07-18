package com.nbcfinalteam2.ddaraogae.presentation.util

import java.text.SimpleDateFormat
import java.time.DayOfWeek
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.temporal.TemporalAdjusters
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.TimeZone

object DateFormatter {

    private val timeZone = TimeZone.getTimeZone("Asia/Seoul")
    val dateFormat = SimpleDateFormat("MM/dd", Locale.getDefault()).apply {
        timeZone = this@DateFormatter.timeZone
    }
    private val todayDateFormat = SimpleDateFormat("yyyy.MM.dd HH:mm", Locale.getDefault())

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

    fun getTodayDate(): String {
        return todayDateFormat.format(Date())
    }

    fun getDateFormatter(walkEndTime: Date): String {
        val format = SimpleDateFormat("yyyy.MM.dd HH:mm", Locale.getDefault())
        return format.format(walkEndTime)
    }

    fun getAFewHoursAgo(date: Date): Int {
        val now: Long = System.currentTimeMillis()
        val nowDate = Date(now)
        val timeDifference = nowDate.time - date.time

        val seconds = timeDifference / 1000
        val minutes = seconds / 60
        val hours = minutes / 60

        return hours.toInt()
    }

    fun getCurrentMonday() : Date{
        val now = LocalDateTime.now()
        val monday = now.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))
        val mondayMidnight = monday.toLocalDate().atStartOfDay()
        val seoulZoneId = ZoneId.of("Asia/Seoul")
        val instant = mondayMidnight.atZone(seoulZoneId).toInstant()
        return Date.from(instant)
    }
}