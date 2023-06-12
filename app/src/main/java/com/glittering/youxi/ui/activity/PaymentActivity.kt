package com.glittering.youxi.ui.activity

import android.os.Bundle
import com.glittering.youxi.R
import com.glittering.youxi.data.BaseResponse
import com.glittering.youxi.data.MoneyOperateRequest
import com.glittering.youxi.data.ServiceCreator
import com.glittering.youxi.data.UserService
import com.glittering.youxi.databinding.ActivityPaymentBinding
import com.glittering.youxi.utils.ToastFail
import com.glittering.youxi.utils.ToastSuccess
import com.google.gson.Gson
import com.gyf.immersionbar.ktx.fitsTitleBar
import okhttp3.FormBody
import okhttp3.MediaType
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
            val json = FormBody.create(
                MediaType.parse("application/json; charset=utf-8"),
                Gson().toJson(data)
            )
            userService.operateMoney(json)
                .enqueue(object : retrofit2.Callback<BaseResponse> {
                    override fun onResponse(
                        call: retrofit2.Call<BaseResponse>,
                        response: retrofit2.Response<BaseResponse>
                    ) {
                        if (response.body() != null) {
                            if (response.body()!!.code == 200) {
                                ToastSuccess(response.body()!!.message)
                            } else {
                                ToastFail(response.body()!!.message)
                            }
                        } else {
                            ToastFail(getString(R.string.toast_response_error))
                        }
                    }

                    override fun onFailure(
                        call: retrofit2.Call<BaseResponse>, t: Throwable
                    ) {
                        t.printStackTrace()
                        ToastFail(getString(R.string.toast_response_error))
                    }
                })
        }

    }
}