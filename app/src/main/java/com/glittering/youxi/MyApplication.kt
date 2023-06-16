package com.glittering.youxi

import android.app.Application
import com.hjq.toast.Toaster

class MyApplication : Application() {
    companion object {
        lateinit var application: Application
    }

    init {
        application = this
        Toaster.init(this)
    }

}