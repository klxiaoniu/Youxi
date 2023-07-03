package com.glittering.youxi.data.bean

data class LoginUser(
    var id: Long,
    var type: String,
    var token: String,
    var username: String
)