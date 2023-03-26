package com.glittering.youxi.ui.activity

import android.os.Bundle
import android.util.Log
import androidx.lifecycle.ViewModelProvider
import com.glittering.youxi.MyApplication
import com.glittering.youxi.R
import com.glittering.youxi.data.*
import com.glittering.youxi.databinding.ActivityLoginBinding
import com.glittering.youxi.utils.DrawableUtil
import com.glittering.youxi.utils.setToken
import com.google.gson.Gson
import com.xiaoniu.fund.ToastLong
import com.xiaoniu.fund.ToastShort
import okhttp3.FormBody
import okhttp3.MediaType
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginActivity : BaseActivity<ActivityLoginBinding>() {

    lateinit var viewModel: LoginViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(this).get(LoginViewModel::class.java)

        val username = binding.username
        val password = binding.password
        val ivCode = binding.ivCode
        val code = binding.code

        ivCode.setOnClickListener { getCode() }
        if (viewModel.codeImg == null) {
            getCode()
        } else {
            try {
                ivCode.setImageDrawable(DrawableUtil().byteToDrawable(viewModel.codeImg!!))
            } catch (e: Exception) {
                e.printStackTrace()
                ivCode.setImageResource(R.drawable.error)
            }
        }
        if (isDarkTheme(this)) {
            binding.close.setColorFilter(getColor(R.color.white))
        }
        binding.close.setOnClickListener { finish() }
        binding.login.setOnClickListener {
            if (!binding.checkAgree.isChecked) {
                ToastShort("请先阅读并同意《用户协议》和《隐私政策》")
                return@setOnClickListener
            }
            val userService = ServiceCreator.create<UserService>()

            val loginRequest = LoginRequest(
                username.text.toString(),
                password.text.toString(),
                code.text.toString()
            )
            val json = FormBody.create(
                MediaType.parse("application/json; charset=utf-8"),
                Gson().toJson(loginRequest)
            )
            userService.login(json).enqueue(object : Callback<LoginResponse> {
                override fun onResponse(
                    call: Call<LoginResponse>,
                    response: Response<LoginResponse>
                ) {
                    Log.d("body", response.body().toString())
                    val code = response.body()?.code
                    if (code == 200) {
                        val data = response.body()?.data
                        Log.d("body", data.toString())
                        val token = data?.token!!
                        setToken(token)
                        Log.d("token", token)
                        MyApplication.loggedInUser = data
                        ToastShort("登录成功")
                        finish()
                    } else {
                        ToastShort(response.body()?.message.toString())
                        getCode()
                    }
                }

                override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                    t.printStackTrace()
                    ToastLong(t.toString())
                    getCode()
                }
            })

        }
    }

    fun getCode() {
        binding.ivCode.setImageResource(R.drawable.loading)
        val userService = ServiceCreator.create<UserService>()
        userService.queryCode().enqueue(object : Callback<CodeResponse> {
            override fun onResponse(
                call: Call<CodeResponse>,
                response: Response<CodeResponse>
            ) {
                try {
                    val img = DrawableUtil().byteToDrawable(response.body()?.image!!)
                    binding.ivCode.setImageDrawable(img)
                    viewModel.codeImg = response.body()?.image!!
                } catch (e: Exception) {
                    e.printStackTrace()
                    binding.ivCode.setImageResource(R.drawable.error)
                }
            }

            override fun onFailure(call: Call<CodeResponse>, t: Throwable) {
                t.printStackTrace()
                ToastLong(t.toString())
                binding.ivCode.setImageResource(R.drawable.error)
            }
        })
    }
}