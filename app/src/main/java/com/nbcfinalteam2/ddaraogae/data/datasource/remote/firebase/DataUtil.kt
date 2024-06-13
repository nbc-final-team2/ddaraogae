package com.nbcfinalteam2.ddaraogae.data.datasource.remote.firebase

import com.nbcfinalteam2.ddaraogae.data.dto.WalkingDto
import java.time.LocalDate
import java.time.ZoneId
import java.util.Calendar
import java.util.Date
import kotlin.math.max

object DataUtil {

    // 기준일 동안 진행된 산책 데이터의 거리 총합
    fun getTotalDistanceInADay(date: Date, walkingList: List<WalkingDto>): Double {
        val (startDate, endDate) = date.getDayStartAndEnd()

        return walkingList.filter { it.startDateTime!! in startDate..endDate }.sumOf { it.distance!! }
    }

    // 주어진 산책 데이터 리스트에서 연속된 최대 산책 일수 계산
    fun getLongestConsecutiveWalkingDays(walkingList: List<WalkingDto>): Int {
        val convertedList = walkingList.map { it.startDateTime!!.toLocalDate() }
        if(convertedList.isEmpty()) return 0
        var totalMaxNum = 1
        var curMaxNum = 1
        var prevDate = convertedList.first()

        for(localDate in convertedList) {
            if(localDate.isEqual(prevDate.plusDays(1))) {
                prevDate = localDate
                curMaxNum++
            }else if(localDate.isAfter(prevDate)) {
                prevDate = localDate
                totalMaxNum = max(curMaxNum, totalMaxNum)
                curMaxNum = 1
            }
        }
        totalMaxNum = max(curMaxNum, totalMaxNum)
        return totalMaxNum
    }
}

fun Date.toLocalDate(): LocalDate {
    return this.toInstant()
        .atZone(ZoneId.systemDefault())
        .toLocalDate()
}

fun Date.getDayStartAndEnd(): Pair<Date, Date> {
    val cal = Calendar.getInstance()
    cal.time = this

    val startDate = cal.apply {
        set(Calendar.HOUR_OF_DAY, 0)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
    }.time

    val endDate = cal.apply {
        set(Calendar.HOUR_OF_DAY, 23)
        set(Calendar.MINUTE, 59)
        set(Calendar.SECOND, 59)
        set(Calendar.MILLISECOND, 999)
    }.time

    return startDate to endDate
}

fun Date.getWeekStartAndEnd(): Pair<Date, Date> {
    val cal = Calendar.getInstance()
    cal.time = this
    cal.firstDayOfWeek = Calendar.MONDAY

    val mondayStart = cal.apply {
        set(Calendar.DAY_OF_WEEK, Calendar.MONDAY)
        set(Calendar.HOUR_OF_DAY, 0)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
    }.time

    val sundayEnd = cal.apply {
        set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY)
        set(Calendar.HOUR_OF_DAY, 23)
        set(Calendar.MINUTE, 59)
        set(Calendar.SECOND, 59)
        set(Calendar.MILLISECOND, 999)
    }.time

    return mondayStart to sundayEnd
}