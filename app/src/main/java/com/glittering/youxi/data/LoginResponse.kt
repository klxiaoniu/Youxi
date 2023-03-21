package com.glittering.youxi.data

data class LoginResponse (
    val code: Int,
    val data: User,
    val message: String
)