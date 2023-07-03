package com.glittering.youxi.data.bean

data class ReportUserData(
    val message: String,
    val report_id: Int,
    val suspected_id: Long,
    val user_id: Long
)