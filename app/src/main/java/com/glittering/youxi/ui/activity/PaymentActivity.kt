package com.glittering.youxi.ui.activity

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import com.glittering.youxi.R
import com.glittering.youxi.data.bean.RechargeData
import com.glittering.youxi.data.request.MoneyOperateRequest
import com.glittering.youxi.data.response.BaseDataResponse
import com.glittering.youxi.data.service.ServiceCreator
import com.glittering.youxi.data.service.UserService
import com.glittering.youxi.databinding.ActivityPaymentBinding
import com.glittering.youxi.utils.RequestUtil
import com.glittering.youxi.utils.ToastFail
import com.gyf.immersionbar.ktx.fitsTitleBar
import kotlin.properties.Delegates

class PaymentActivity : BaseActivity<ActivityPaymentBinding>() {
    override val fitSystemWindows: Boolean
        get() = false

    var operationType by Delegates.notNull<Int>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        intent.getIntExtra("type", -1).let {
            operationType = it
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

//        binding.efabYes.backgroundTintList=null
        binding.efabYes.setOnClickListener {
            if (binding.etPrice.text.isEmpty()) return@setOnClickListener
            val userService = ServiceCreator.create<UserService>()
            val data = MoneyOperateRequest(
                binding.etPrice.text.toString().toDouble(),
                if (operationType == 0) "in" else "out"
            )
            userService.operateMoney(RequestUtil.generateJson(data))
                .enqueue(object : retrofit2.Callback<BaseDataResponse<RechargeData>> {
                    override fun onResponse(
                        call: retrofit2.Call<BaseDataResponse<RechargeData>>,
                        response: retrofit2.Response<BaseDataResponse<RechargeData>>
                    ) {
                        if (response.body() != null) {
                            if (response.body()!!.code == 200) {
                                //ToastSuccess(response.body()!!.message)
                                val intent = Intent(
                                    Intent.ACTION_VIEW,
                                    Uri.parse(response.body()!!.data.pay_url)
                                )
                                startActivity(intent)
                                finish()
                            } else {
                                ToastFail(response.body()!!.message)
                            }
                        } else {
                            ToastFail(getString(R.string.toast_response_error))
                        }
                    }

                    override fun onFailure(
                        call: retrofit2.Call<BaseDataResponse<RechargeData>>, t: Throwable
                    ) {
                        t.printStackTrace()
                        ToastFail(getString(R.string.toast_response_error))
                    }
                })
        }

    }
}