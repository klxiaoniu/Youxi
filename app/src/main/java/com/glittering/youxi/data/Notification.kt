package com.glittering.youxi.data

data class Notification(
    val id: Long,
    val title: String,
    val message: String,
    val time: Long,
    val avatar: Int //TODO: 头像地址
)
