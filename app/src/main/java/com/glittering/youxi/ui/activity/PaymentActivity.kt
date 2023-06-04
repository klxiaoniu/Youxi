package com.glittering.youxi.ui.activity

import android.os.Bundle
import com.glittering.youxi.R
import com.glittering.youxi.databinding.ActivityPaymentBinding
import com.gyf.immersionbar.ktx.fitsTitleBar

class PaymentActivity : BaseActivity<ActivityPaymentBinding>() {
    override val fitSystemWindows: Boolean
        get() = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        intent.getIntExtra("type", -1).let {
            when (it) {
                0 -> binding.toolbar.title = "充值"
                1 -> binding.toolbar.title = "提现"
                else -> {
                    finish()
                    return
                }
            }
        }
        binding.toolbar.let {
            it.setNavigationIcon(R.drawable.ic_back)
            it.setNavigationOnClickListener { finish() }
            fitsTitleBar(it)
        }


    }
}