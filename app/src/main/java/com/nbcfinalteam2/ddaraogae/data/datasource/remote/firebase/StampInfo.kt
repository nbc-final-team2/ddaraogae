package com.nbcfinalteam2.ddaraogae.data.datasource.remote.firebase

import com.nbcfinalteam2.ddaraogae.data.dto.StampDto
import com.nbcfinalteam2.ddaraogae.domain.entity.StampInfoEntity
import java.util.Date

data class StampInfo(
    val num: Int,
    val title: String,
    val description: String
) {
    fun toStampDto(getDateTime: Date) = StampDto(
        stampNum = num,
        getDateTime = getDateTime,
        name = title
    )

    fun toEntity() = StampInfoEntity(
        num = num,
        title = title,
        description = description
    )

    companion object {
        val STAMP_0 = StampInfo(0, "UNKNOWN", "")
        val STAMP_1 = StampInfo(1, "오늘의 산책 완료", "")
        val STAMP_2 = StampInfo(2, "3일 연속 산책", "")
        val STAMP_3 = StampInfo(3, "5일 연속 산책", "")
        val STAMP_4 = StampInfo(4, "7일 연속 산책", "")
        val STAMP_5 = StampInfo(5, "다다익선", "")
        val STAMP_6 = StampInfo(6, "단거리 마라토너", "")
        val STAMP_7 = StampInfo(7, "중거리 마라토너", "")
        val STAMP_8 = StampInfo(8, "장거리 마라토너", "")

        val stampInfoList = listOf(
            STAMP_1,
            STAMP_2,
            STAMP_3,
            STAMP_4,
            STAMP_5,
            STAMP_6,
            STAMP_7,
            STAMP_8,
        )
    }
}