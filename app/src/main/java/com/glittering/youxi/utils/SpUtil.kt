package com.glittering.youxi.utils

import androidx.appcompat.app.AppCompatActivity

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
}
fun setFirstEnter(firstEnter: Boolean) {
    val sp = applicationContext.getSharedPreferences("common", AppCompatActivity.MODE_PRIVATE).edit()
    sp.putBoolean("firstEnter", firstEnter).apply()
}

fun isFirstEnter(): Boolean {
    val sp = applicationContext.getSharedPreferences("common", AppCompatActivity.MODE_PRIVATE)
    return sp.getBoolean("firstEnter", true)
}