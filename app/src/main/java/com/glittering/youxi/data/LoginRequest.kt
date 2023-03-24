package com.glittering.youxi.data

data class LoginRequest(
    val username: String,
    val password: String,
    val code: String
)