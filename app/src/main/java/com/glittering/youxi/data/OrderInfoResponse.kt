package com.glittering.youxi.data

data class OrderInfoResponse(
    val code: Int,
    val data: List<Order>,
    val message: String
)
