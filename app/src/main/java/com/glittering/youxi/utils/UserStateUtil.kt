package com.glittering.youxi.utils

import android.content.Context
import android.content.Intent
import com.glittering.youxi.data.LoginUser
import com.glittering.youxi.ui.activity.LoginActivity

class UserStateUtil {

    private var loggedInUser: LoginUser? = null

    companion object {
        private var instance: UserStateUtil? = null
        fun getInstance(): UserStateUtil {
            if (instance == null) {
                instance = UserStateUtil()
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
    }

    fun getUserId(): Long? {
        return loggedInUser?.id
    }

    fun logout() {
        loggedInUser = null
        rmToken()
    }

}