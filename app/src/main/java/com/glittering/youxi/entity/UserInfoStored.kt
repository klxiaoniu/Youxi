package com.glittering.youxi.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class UserInfoStored(
    @PrimaryKey
    val userId: Long,
    val name: String,
    val orders: Int,
    val photo: String
)