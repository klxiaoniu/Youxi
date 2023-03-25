package com.glittering.youxi.ui

import android.content.res.ColorStateList
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.PixelFormat
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Base64
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.glittering.youxi.MyApplication.Companion.loggedInUser
import com.glittering.youxi.R
import com.glittering.youxi.data.*
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
import java.io.ByteArrayOutputStream


class LoginActivity : BaseActivity<ActivityLoginBinding>() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val username = binding.username
        val password = binding.password
        val ivCode = binding.ivCode
        val code = binding.code

        ivCode.setOnClickListener { getCode() }
        getCode()
        if (isDarkTheme(this)) {
            binding.close.setColorFilter(getColor(R.color.white))
        }
        binding.close.setOnClickListener { finish() }
        binding.login.setOnClickListener {
            if (!binding.checkAgree.isChecked){
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
                        loggedInUser = data
                        ToastShort("登录成功")
                        finish()
                    } else {
                        ToastShort(response.body()?.message.toString())
                        getCode()
                    }
                    //binding.homeSwipe.isRefreshing = false
                }

                override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                    t.printStackTrace()
                    ToastLong(t.toString())
                    getCode()
//                            adapter.setOnFootViewClickListener { getData(page) }   //重新获取当前页
//                            binding.homeSwipe.isRefreshing = false
                }
            })

        }
    }

    @Synchronized
    fun byteToDrawable(icon: String): Drawable? {
        val img: ByteArray = Base64.decode(icon.toByteArray(), Base64.DEFAULT)
        val bitmap: Bitmap
        if (img != null) {
            bitmap = BitmapFactory.decodeByteArray(img, 0, img.size)
            return BitmapDrawable(bitmap)
        }
        return null
    }

    @Synchronized
    fun drawableToByte(drawable: Drawable?): String? {
        if (drawable != null) {
            val bitmap = Bitmap
                .createBitmap(
                    drawable.intrinsicWidth,
                    drawable.intrinsicHeight,
                    if (drawable.opacity != PixelFormat.OPAQUE) Bitmap.Config.ARGB_8888 else Bitmap.Config.RGB_565
                )
            val canvas = Canvas(bitmap)
            drawable.setBounds(
                0, 0, drawable.intrinsicWidth,
                drawable.intrinsicHeight
            )
            drawable.draw(canvas)
            val size = bitmap.width * bitmap.height * 4

// 创建一个字节数组输出流,流的大小为size
            val baos = ByteArrayOutputStream(size)

// 设置位图的压缩格式，质量为100%，并放入字节数组输出流中
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos)

// 将字节数组输出流转化为字节数组byte[]
            val imagedata: ByteArray = baos.toByteArray()
            return Base64.encodeToString(imagedata, Base64.DEFAULT)
        }
        return null
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
                    val img = byteToDrawable(response.body()?.image!!)
                    binding.ivCode.setImageDrawable(img)
                } catch (e: Exception) {
                    e.printStackTrace()
                    binding.ivCode.setImageResource(R.drawable.error)
                }

                //binding.homeSwipe.isRefreshing = false
            }

            override fun onFailure(call: Call<CodeResponse>, t: Throwable) {
                t.printStackTrace()
                ToastLong(t.toString())
                binding.ivCode.setImageResource(R.drawable.error)
//                            adapter.setOnFootViewClickListener { getData(page) }   //重新获取当前页
//                            binding.homeSwipe.isRefreshing = false
            }
        })
    }
}