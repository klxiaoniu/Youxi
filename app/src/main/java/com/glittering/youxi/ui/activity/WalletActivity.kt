package com.glittering.youxi.ui.activity

import android.content.Intent
import android.os.Bundle
import com.glittering.youxi.R
import com.glittering.youxi.databinding.ActivityWalletBinding
import com.gyf.immersionbar.ktx.fitsTitleBar

class WalletActivity : BaseActivity<ActivityWalletBinding>() {
    override val fitSystemWindows: Boolean
        get() = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding.toolbar.let {
            it.setNavigationIcon(R.drawable.ic_back)
            it.setNavigationOnClickListener { finish() }
            fitsTitleBar(it)
        }
        binding.itemRecharge.setOnClickListener {
            Intent(this, PaymentActivity::class.java)
                .putExtra("type", 0)
                .let { startActivity(it) }
        }
        binding.itemWithdraw.setOnClickListener {
            Intent(this, PaymentActivity::class.java)
                .putExtra("type", 1)
                .let { startActivity(it) }
        }
    }
}