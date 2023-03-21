package com.glittering.youxi.utils

import androidx.appcompat.app.AppCompatActivity
import com.xiaoniu.fund.applicationContext

fun setToken(token: String) {
    val sp = applicationContext.getSharedPreferences("common", AppCompatActivity.MODE_PRIVATE).edit()
    sp.putString("token", token).apply()
}

fun getToken(): String {
    val sp = applicationContext.getSharedPreferences("common", AppCompatActivity.MODE_PRIVATE)
    return sp.getString("token", "")!!
}

fun rmToken() {
    val sp = applicationContext.getSharedPreferences("common", AppCompatActivity.MODE_PRIVATE).edit()
    sp.remove("token").apply()
    //loggedInUser = null
}