package com.glittering.youxi.ui.activity

import android.os.Bundle
import com.glittering.youxi.R
import com.glittering.youxi.databinding.ActivityMyOrderBinding
import com.gyf.immersionbar.ktx.fitsTitleBar

class MyOrderActivity : BaseActivity<ActivityMyOrderBinding>() {
    override val fitSystemWindows: Boolean
        get() = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding.toolbar.let {
            it.setNavigationIcon(R.drawable.ic_back)
            it.setNavigationOnClickListener { finish() }
            it.inflateMenu(R.menu.myorder_menu)
        }
        fitsTitleBar(binding.toolbar)

    }
}