package com.glittering.youxi

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_NO
import androidx.appcompat.app.AppCompatDelegate.setDefaultNightMode
import com.glittering.youxi.data.LoginUser
import com.glittering.youxi.data.ServiceCreator
import com.glittering.youxi.data.UserService
import com.hjq.toast.Toaster
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class MyApplication : Application() {
    companion object {
        lateinit var application: Application

        var loggedInUser: LoginUser? = null

        fun updateUser() {
            GlobalScope.launch(Dispatchers.Main) {
                val userService = ServiceCreator.create<UserService>()

//                var list = userService.login(getToken()).await()
//                when (list["code"].toString()) {
//                    "1" -> {
//                        try {
//                            loggedInUser = JSON.parseObject(
//                                JSON.toJSONString(list["message"]),
//                                User::class.java
//                            )
//                        } catch (e: java.lang.Exception) {
//                            ToastLong(e.toString())
//                            loggedInUser = null
//                        }
//                    }
//                    "0" -> {
//                        ToastLong("Token无效，请重新登录：" + list["message"].toString())
//                        rmToken()
//                    }
//                }

            }
        }


    }

    init {
        application = this
        Toaster.init(this)
        setDefaultNightMode(MODE_NIGHT_NO)  //暂未适配深色模式，不允许开启
        //DynamicColors.applyToActivitiesIfAvailable(application)     //动态取色
    }

}