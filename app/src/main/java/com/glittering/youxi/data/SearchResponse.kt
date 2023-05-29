package com.glittering.youxi.data

data class SearchResponse(
    val code: Int,
    val data: List<SearchOrder>,
    val message: String
)