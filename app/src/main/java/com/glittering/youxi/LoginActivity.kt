package com.glittering.youxi

import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.glittering.youxi.MyApplication.Companion.loggedInUser
import com.glittering.youxi.data.LoginRequest
import com.glittering.youxi.data.LoginResponse
import com.glittering.youxi.data.ServiceCreator
import com.glittering.youxi.data.UserService
import com.glittering.youxi.databinding.ActivityLoginBinding
import com.glittering.youxi.utils.setToken
import com.google.gson.Gson
import com.xiaoniu.fund.ToastLong
import com.xiaoniu.fund.ToastShort
import okhttp3.FormBody
import okhttp3.MediaType
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val username = binding.username
        val password = binding.password
        binding.login.setOnClickListener {
            val userService = ServiceCreator.create<UserService>()
//            val jsonData = JsonObject().apply {
//                addProperty("username", username.text.toString())
//                addProperty("password", password.text.toString())
//            }
            val loginRequest = LoginRequest(username.text.toString(), password.text.toString())
            val json = FormBody.create(
                MediaType.parse("application/json; charset=utf-8"),
                Gson().toJson(loginRequest)
            )
            userService.login(json).enqueue(object : Callback<LoginResponse> {
                override fun onResponse(
                    call: Call<LoginResponse>,
                    response: Response<LoginResponse>
                ) {
                    val code = response.body()?.code
                    if (code == 200) {
                        val data = response.body()?.data
                        Log.d("body", data.toString())
                        val token = data?.token!!
                        setToken(token)
                        Log.d("token", token)
                        loggedInUser = data
                    } else ToastShort("登录失败")
                    //binding.homeSwipe.isRefreshing = false
                }

                override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                    t.printStackTrace()
                    ToastLong(t.toString())
//                            adapter.setOnFootViewClickListener { getData(page) }   //重新获取当前页
//                            binding.homeSwipe.isRefreshing = false
                }
            })

        }
    }
}