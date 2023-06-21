package com.glittering.youxi.ui.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.viewbinding.ViewBinding
import com.dylanc.loadingstateview.LoadingState
import com.dylanc.loadingstateview.LoadingStateDelegate
import com.dylanc.viewbinding.base.ActivityBinding
import com.dylanc.viewbinding.base.ActivityBindingDelegate
import com.glittering.youxi.utils.DarkUtil.Companion.isDarkTheme
import com.gyf.immersionbar.ImmersionBar

abstract class BaseActivity<VB : ViewBinding> : AppCompatActivity(),
    LoadingState by LoadingStateDelegate(),
    ActivityBinding<VB> by ActivityBindingDelegate() {

    open val fitSystemWindows = true
    open val keyboardEnable = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentViewWithBinding()
        binding.root.decorate(this)

        ImmersionBar.with(this)
            .transparentBar()
            .statusBarDarkFont(!isDarkTheme(this))   //状态栏字体是深色，不写默认为亮色
            .navigationBarDarkIcon(true)
            .fitsSystemWindows(fitSystemWindows)
            .keyboardEnable(keyboardEnable)
            .init()

    }
}