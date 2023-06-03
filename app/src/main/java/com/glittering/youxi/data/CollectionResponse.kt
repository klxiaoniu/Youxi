package com.glittering.youxi.data

data class CollectionResponse (
    val code: Int,
    val data: List<CollectionData>,
    val message: String
)