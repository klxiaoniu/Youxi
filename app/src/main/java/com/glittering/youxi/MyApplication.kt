package com.glittering.youxi

import android.app.Application
import com.glittering.youxi.data.LoginUser
import com.hjq.toast.Toaster

class MyApplication : Application() {
    companion object {
        lateinit var application: Application
        var loggedInUser: LoginUser? = null
    }

    init {
        application = this
        Toaster.init(this)
        //setDefaultNightMode(MODE_NIGHT_NO)  //暂未适配深色模式，不允许开启
        //DynamicColors.applyToActivitiesIfAvailable(application)     //动态取色
    }

}