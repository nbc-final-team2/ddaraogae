package com.nbcfinalteam2.ddaraogae.presentation.util

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object TextConverter {
    fun dateDateToString(date: Date) : String {
        val format = SimpleDateFormat("yyyy년 MM월 dd일 EEEE", Locale.KOREA)
        return format.format(date)
    }

    fun timeIntToStringForWalk(time: Int) : String {
        //시간 구하기
        val hour = time / 3600
        val min = (time / 60) % 60
        val sec = time % 60

        //한자리 수 일땐 앞에 0을 붙이기 위함
        val tHour = "%02d".format(hour)
        val tMin = "%02d".format(min)
        val tSec = "%02d".format(sec)

        //1시간 이상 측정 시 시간 단위로 표현
        val totalTimeText = if (hour >= 1) "${tHour}시간  ${tMin}분" else "${tMin}분 ${tSec}초"

        return totalTimeText
    }

    fun distanceDoubleToString(distance: Double) : String {
        return if (distance >= 1.0) {
            String.format("%.1f km", distance / 1.0)
        } else {
            String.format("%d m", distance.toInt())
        }
    }

    fun timeIntToStringForHistory(timeTaken: Int) : String {
        val hours = timeTaken / 3600
        val minutes = (timeTaken % 3600) / 60
        val seconds = timeTaken % 60

        return when {
            hours > 0 -> String.format("%d시간 %d분 %d초", hours, minutes, seconds)
            minutes > 0 -> String.format("%d분 %d초", minutes, seconds)
            else -> String.format("%d초", seconds)
        }
    }
}