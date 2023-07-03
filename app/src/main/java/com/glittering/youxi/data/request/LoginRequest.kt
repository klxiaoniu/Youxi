package com.glittering.youxi.data.request

data class LoginRequest(
    val username: String,
    val password: String,
    val code: String
)