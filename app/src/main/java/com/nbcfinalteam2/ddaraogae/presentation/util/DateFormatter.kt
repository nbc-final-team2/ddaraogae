package com.nbcfinalteam2.ddaraogae.presentation.util

import android.content.Context
import android.provider.Settings.Global.getString
import android.util.Log
import com.nbcfinalteam2.ddaraogae.R
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

    fun getHistoryDate(walkEndTime: Date): String {
        val format = SimpleDateFormat("yyyy.MM.dd HH:mm", Locale.getDefault())
        return format.format(walkEndTime)
    }

    fun getCurrentTimeAgo(date: Date?): String {
        if (date == null) {
            return "최근에 산책한 기록이 없어요!" // 시간에 대한 정보가 없을때는 아무것도 보여주지 않기 위함.
        }
        val now: Long = System.currentTimeMillis()
        val nowDate = Date(now) // 현재 시간을 Date 타입으로 변환
        val timeDifference = nowDate.time - date.time // 현재 시간 - 특정 시간

        val seconds = timeDifference / 1000
        val minutes = seconds / 60
        val hours = minutes / 60

        return when {
            hours < 1 -> {
                "금방 산책을 다녀왔어요!"
            }
            hours < 24 -> {
                "$hours 시간 전"
            }
            else -> {
                "산책한지 24시간이 넘었습니다"
            }
        }
    }
}