package com.glittering.youxi.data

import com.glittering.youxi.utils.getToken
import com.xiaoniu.fund.ToastLong
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Request
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.IOException
import java.util.concurrent.TimeUnit
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine


object ServiceCreator {
    //在请求头里添加token的拦截器处理
    class TokenHeaderInterceptor : Interceptor {
        @Throws(IOException::class)
        override fun intercept(chain: Interceptor.Chain): okhttp3.Response {
            val token: String = getToken()
            return if (token == "") {
                val originalRequest: Request = chain.request()
                chain.proceed(originalRequest)
            } else {
                val originalRequest: Request = chain.request()
                val updateRequest: Request =
                    originalRequest.newBuilder().header("Authorization", token).build()
                chain.proceed(updateRequest)
            }
        }
    }
    private fun getClient(): OkHttpClient.Builder {
        val httpClientBuilder = OkHttpClient.Builder()
        httpClientBuilder.connectTimeout(15, TimeUnit.SECONDS)
        httpClientBuilder.addNetworkInterceptor(TokenHeaderInterceptor())
        return httpClientBuilder
    }

    const val BASE_URL = "https://7f1192d863.imdo.co/"
    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(getClient().build())
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    fun <T> create(serviceClass: Class<T>): T = retrofit.create(serviceClass)
    inline fun <reified T> create(): T = create(T::class.java)


    //扩展Retrofit.Call类，为其扩展一个await方法，并标识为挂起函数
    suspend fun <T> Call<T>.await(): T {
        return suspendCoroutine {
            enqueue(object : Callback<T> {
                override fun onFailure(call: Call<T>, t: Throwable) {
                    //请求失败，抛出异常，手动结束当前协程
                    //it.resumeWithException(t)
                    ToastLong(t.toString())
                }

                override fun onResponse(call: Call<T>, response: Response<T>) {
                    if (response.isSuccessful) {
                        //请求成功，将请求结果拿到并手动恢复所在协程
                        it.resume(response.body()!!)
                    } else {
                        //请求状态异常，抛出异常，手动结束当前协程
                        //it.resumeWithException(Throwable(response.toString()))
                        ToastLong(response.toString())
                    }
                }
            })
        }
    }

}