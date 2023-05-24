package com.glittering.youxi.data

data class VerifyingOrder(
    val description: String,
    val game: String,
    val order_id: Int,
    val picture: String,
    val price: Double,
    val seller_id: Int,
    val status: String,
    val title: String
)