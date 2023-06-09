package com.glittering.youxi.ui.activity

import android.content.Intent
import android.os.Bundle
import com.glittering.youxi.R
import com.glittering.youxi.data.bean.MoneyData
import com.glittering.youxi.data.response.BaseDataResponse
import com.glittering.youxi.data.service.ServiceCreator
import com.glittering.youxi.data.service.UserService
import com.glittering.youxi.databinding.ActivityWalletBinding
import com.glittering.youxi.utils.ToastFail
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

    override fun onStart() {
        super.onStart()

        val userService = ServiceCreator.create<UserService>()
        userService.getMoney().enqueue(object : retrofit2.Callback<BaseDataResponse<MoneyData>> {
            override fun onResponse(
                call: retrofit2.Call<BaseDataResponse<MoneyData>>,
                response: retrofit2.Response<BaseDataResponse<MoneyData>>
            ) {
                if (response.body() != null) {
                    if (response.body()!!.code == 200) {
                        binding.tvUsable.text = response.body()!!.data.money.toString()
                    } else ToastFail(response.body()!!.message)
                } else {
                    ToastFail(getString(R.string.toast_response_error))
                }
            }

            override fun onFailure(
                call: retrofit2.Call<BaseDataResponse<MoneyData>>,
                t: Throwable
            ) {
                t.printStackTrace()
                ToastFail(getString(R.string.toast_response_error))
            }
        })
    }
}