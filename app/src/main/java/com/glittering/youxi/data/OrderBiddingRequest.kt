package com.glittering.youxi.data

data class OrderBiddingRequest(
    val order_id: Int,
    val price: Double,
    val message: String
)
