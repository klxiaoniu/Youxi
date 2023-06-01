package com.glittering.youxi.data

data class UserInfoResponse(
    val code: Int,
    val data: List<UserInfo>,
    val message: String
)