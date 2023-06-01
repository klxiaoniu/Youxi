package com.glittering.youxi.data

data class BidInfoResponse(
    val code: Int,
    val data: List<BidInfo>,
    val message: String
)