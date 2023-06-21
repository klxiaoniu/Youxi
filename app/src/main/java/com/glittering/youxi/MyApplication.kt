package com.glittering.youxi

import android.app.Application
import com.dylanc.loadingstateview.LoadingStateView
import com.glittering.youxi.ui.delegate.EmptyViewDelegate
import com.glittering.youxi.ui.delegate.ErrorViewDelegate
import com.glittering.youxi.ui.delegate.LoadingViewDelegate
import com.hjq.toast.Toaster

class MyApplication : Application() {
    companion object {
        lateinit var application: Application
    }

    init {
        application = this
        Toaster.init(this)
        LoadingStateView.setViewDelegatePool {
            register(LoadingViewDelegate(), ErrorViewDelegate(), EmptyViewDelegate())
        }

    }

}