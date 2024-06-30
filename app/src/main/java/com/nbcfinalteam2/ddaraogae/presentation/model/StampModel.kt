package com.nbcfinalteam2.ddaraogae.presentation.model

import java.util.Date

data class StampModel (
    val id: String?, // Stamp 아이디
    val stampNum: Int?, // Stamp 얻은 총 갯수 ( 각 도장마다 )
    val getDateTime: Date?, // 언제 도장을 얻었는지
    val name: String?, // 도장 이름
    val num: Int, // 도장 고유번호
    val title: String, // 도장 이름 (?) 이거 써야함
    val description: String // 도장 설명
)