package com.glittering.youxi.data

data class MyOrderResponse(
    val code: Int,
    val data: List<MyOrderData>,
    val message: String
)