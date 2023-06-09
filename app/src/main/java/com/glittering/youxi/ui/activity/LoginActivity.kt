package com.glittering.youxi.ui.activity

import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.glittering.youxi.R
import com.glittering.youxi.data.bean.Code
import com.glittering.youxi.data.bean.LoginUser
import com.glittering.youxi.data.request.LoginRequest
import com.glittering.youxi.data.request.RegisterRequest
import com.glittering.youxi.data.response.BaseDataResponse
import com.glittering.youxi.data.response.BaseResponse
import com.glittering.youxi.data.service.ServiceCreator
import com.glittering.youxi.data.service.UserService
import com.glittering.youxi.databinding.ActivityLoginBinding
import com.glittering.youxi.manager.UserStateManager
import com.glittering.youxi.utils.DarkUtil.Companion.reverseColorIfDark
import com.glittering.youxi.utils.RequestUtil
import com.glittering.youxi.utils.ToastFail
import com.glittering.youxi.utils.ToastInfo
import com.glittering.youxi.utils.ToastSuccess
import com.glittering.youxi.utils.setToken
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.security.MessageDigest

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
                ivCode.setImageDrawable(viewModel.codeImg!!)
            } catch (e: Exception) {
                e.printStackTrace()
                ivCode.setImageResource(R.drawable.error)
            }
        }
        reverseColorIfDark(listOf(binding.close))
        binding.close.setOnClickListener { finish() }
        binding.login.setOnClickListener {
            if (!binding.checkAgree.isChecked) {
                ToastFail("请先阅读并同意《用户协议》和《隐私政策》")
                return@setOnClickListener
            }
            val userService = ServiceCreator.create<UserService>()
            val loginRequest = LoginRequest(
                username.text.toString(),
                toHexStr(
                    MessageDigest.getInstance("SHA256")
                        .digest(password.text.toString().toByteArray())
                ),
                code.text.toString()
            )
            val json = RequestUtil.generateJson(loginRequest)
            userService.login(json).enqueue(object : Callback<BaseDataResponse<LoginUser>> {
                override fun onResponse(
                    call: Call<BaseDataResponse<LoginUser>>,
                    response: Response<BaseDataResponse<LoginUser>>
                ) {
                    val code = response.body()?.code
                    if (code == 200) {
                        val data = response.body()?.data
                        val token = data?.token!!
                        setToken(token)
                        UserStateManager.getInstance().setLoggedInUser(data)
                        ToastSuccess("登录成功")
                        finish()
                    } else {
                        ToastFail(response.body()?.message.toString())
                        getCode()
                    }
                }

                override fun onFailure(call: Call<BaseDataResponse<LoginUser>>, t: Throwable) {
                    t.printStackTrace()
                    ToastFail(t.toString())
                    getCode()
                }
            })

        }
        binding.toRegister.setOnClickListener {
            when (binding.register.visibility) {
                android.view.View.VISIBLE -> {
                    binding.register.visibility = android.view.View.INVISIBLE
                    binding.login.visibility = android.view.View.VISIBLE
                    binding.toRegister.text = "注册"
                }

                android.view.View.INVISIBLE -> {
                    binding.register.visibility = android.view.View.VISIBLE
                    binding.login.visibility = android.view.View.INVISIBLE
                    binding.toRegister.text = "登录"
                }
            }
        }
        binding.register.setOnClickListener {
            if (!binding.checkAgree.isChecked) {
                ToastFail("请先阅读并同意《用户协议》和《隐私政策》")
                return@setOnClickListener
            }
            val userService = ServiceCreator.create<UserService>()
            val request = RegisterRequest(
                username.text.toString(),
                toHexStr(
                    MessageDigest.getInstance("SHA256")
                        .digest(password.text.toString().toByteArray())
                )
            )
            userService.register(RequestUtil.generateJson(request)).enqueue(object : Callback<BaseResponse> {
                override fun onResponse(
                    call: Call<BaseResponse>,
                    response: Response<BaseResponse>
                ) {
                    val code = response.body()?.code
                    if (code == 200) {
                        ToastSuccess("注册成功，请登录")
                        binding.toRegister.performClick()
                    } else {
                        ToastFail(response.body()?.message.toString())
                    }
                    getCode()
                }

                override fun onFailure(call: Call<BaseResponse>, t: Throwable) {
                    t.printStackTrace()
                    ToastFail(t.toString())
                    getCode()
                }
            })
        }
        binding.phoneLogin.setOnClickListener {
            ToastInfo("敬请期待！")
        }
    }

    fun getCode() {
        binding.ivCode.setImageResource(R.drawable.loading)
        val userService = ServiceCreator.create<UserService>()
        userService.queryCode().enqueue(object : Callback<Code> {
            override fun onResponse(
                call: Call<Code>,
                response: Response<Code>
            ) {
//                try {
//                    val img = DrawableUtil().byteToDrawable(response.body()?.image!!)
//                    binding.ivCode.setImageDrawable(img)
//                    viewModel.codeImg = response.body()?.image!!
//                } catch (e: Exception) {
//                    e.printStackTrace()
//                    binding.ivCode.setImageResource(R.drawable.error)
//                }

                val options = RequestOptions()
                    .placeholder(R.drawable.loading)
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .skipMemoryCache(true)
                    .error(R.drawable.error)
                Glide.with(applicationContext)
                    .load(response.body()?.image)
                    .apply(options)
                    .into(binding.ivCode)
                //save to viewmodel
                viewModel.codeImg = binding.ivCode.drawable
            }

            override fun onFailure(call: Call<Code>, t: Throwable) {
                t.printStackTrace()
                ToastFail(t.toString())
                binding.ivCode.setImageResource(R.drawable.error)
            }
        })
    }

    private fun toHexStr(byteArray: ByteArray) =
        with(StringBuilder()) {
            byteArray.forEach {
                val hex = it.toInt() and (0xFF)
                val hexStr = Integer.toHexString(hex)
                if (hexStr.length == 1) append("0").append(hexStr)
                else append(hexStr)
            }
            toString()
        }

}