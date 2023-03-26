package com.glittering.youxi.data

data class LoginResponse (
    val code: Int,
    val data: LoginUser,
    val message: String
)