package com.glittering.youxi.manager

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import com.glittering.youxi.data.bean.LoginUser
import com.glittering.youxi.ui.activity.LoginActivity
import com.glittering.youxi.utils.ToastInfo
import com.glittering.youxi.utils.applicationContext
import com.glittering.youxi.utils.rmToken
import com.google.gson.Gson

class UserStateManager {

    private var loggedInUser: LoginUser? = null

    companion object {
        private var instance: UserStateManager? = null
        fun getInstance(): UserStateManager {
            if (instance == null) {
                instance = UserStateManager()
                instance!!.loadLoggedInUser()
            }
            return instance!!
        }
    }

    fun isLogin(): Boolean {
        return loggedInUser != null
    }

    fun checkLogin(ctx: Context): Boolean {
        if (loggedInUser == null) {
            ToastInfo("请先登录")
            ctx.startActivity(Intent(ctx, LoginActivity::class.java))
            return false
        }
        return true
    }

    fun isAdmin(): Boolean {
        return loggedInUser?.type == "admin"
    }

    fun setLoggedInUser(user: LoginUser) {
        loggedInUser = user
        val sp =
            applicationContext.getSharedPreferences("common", AppCompatActivity.MODE_PRIVATE).edit()
        sp.putString("loggedInUser", Gson().toJson(user)).apply()
    }

    private fun loadLoggedInUser() {
        val sp = applicationContext.getSharedPreferences("common", AppCompatActivity.MODE_PRIVATE)
        val userStr = sp.getString("loggedInUser", "")
        if (userStr != "") {
            loggedInUser = Gson().fromJson(userStr, LoginUser::class.java)
        }
    }

    fun getUserId(): Long? {
        return loggedInUser?.id
    }

    fun logout() {
        loggedInUser = null
        rmToken()
        val sp =
            applicationContext.getSharedPreferences("common", AppCompatActivity.MODE_PRIVATE).edit()
        sp.remove("loggedInUser").apply()
    }

}