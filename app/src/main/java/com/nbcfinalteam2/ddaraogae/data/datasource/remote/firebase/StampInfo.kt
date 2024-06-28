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
        val STAMP_0 = StampInfo(0, "UNKNOWN", "UNKNOWN")
        val STAMP_1 = StampInfo(1, "오늘의 산책 완료", "매일 얻을 수 있으며 1.5km이상 산책을 하면 얻을 수 있습니다.")
        val STAMP_2 = StampInfo(2, "3일 연속 산책", "1주일 안에 3일 연속으로 산책을 하면 얻을 수 있습니다.")
        val STAMP_3 = StampInfo(3, "5일 연속 산책", "1주일 안에 5일 연속으로 산책을 하면 얻을 수 있습니다.")
        val STAMP_4 = StampInfo(4, "7일 연속 산책", "1주일 연속으로 산책을 하면 얻을 수 있습니다.")
        val STAMP_5 = StampInfo(5, "다다익선", "1주일안에 일 수에 상관없이 7번 산책을 하면 얻을 수 있습니다.")
        val STAMP_6 = StampInfo(6, "단거리 마라토너", "1주일 안에 10km이상 산책을하면 얻을 수 있습니다.")
        val STAMP_7 = StampInfo(7, "중거리 마라토너", "1주일 안에 15km이상 산책을하면 얻을 수 있습니다.")
        val STAMP_8 = StampInfo(8, "장거리 마라토너", "1주일 안에 20km이상 산책을하면 얻을 수 있습니다.")

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