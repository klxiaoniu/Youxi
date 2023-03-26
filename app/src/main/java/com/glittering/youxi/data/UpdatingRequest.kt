package com.glittering.youxi.data

import java.io.File

data class UpdatingRequest(
    val photo: File,
    val name: String,
    val real_name: String,
    val email: String,
    val ecode: String
)
