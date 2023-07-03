package com.glittering.youxi.data.response

data class BaseDataResponse<T>(
    val code: Int,
    val data: T,
    val message: String
)