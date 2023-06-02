package com.glittering.youxi.data

import com.glittering.youxi.utils.ToastFail
import com.glittering.youxi.utils.applicationContext
import com.glittering.youxi.utils.getToken
import okhttp3.Cache
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Request
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File
import java.io.IOException
import java.util.concurrent.TimeUnit
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine


object ServiceCreator {
    class HeaderInterceptor : Interceptor {
        @Throws(IOException::class)
        override fun intercept(chain: Interceptor.Chain): okhttp3.Response {
            //在请求头里添加token的拦截器处理
            val token: String = getToken()
            val request: Request = chain.request()
            val response = if (token == "") {
                val originalRequest: Request = chain.request()
                chain.proceed(originalRequest)
            } else {
                val originalRequest: Request = chain.request()
                val updateRequest: Request =
                    originalRequest.newBuilder().header("Authorization", token).build()
                chain.proceed(updateRequest)
            }
            //缓存设置
            val cacheControl: String = request.cacheControl().toString()
            return response.newBuilder()
                .removeHeader("Pragma") //清除头信息，因为服务器如果不支持，会返回一些干扰信息，不清除下面无法生效
                .header("Cache-Control", cacheControl)
                .build()
        }
    }

    private fun getClient(): OkHttpClient.Builder {
        val httpCacheDirectory = File(applicationContext.cacheDir, "HttpCache")
        val cacheSize = 30 * 1024 * 1024
        val cache = Cache(httpCacheDirectory, cacheSize.toLong())
        return OkHttpClient.Builder()
            .connectTimeout(10, TimeUnit.SECONDS)
            .addNetworkInterceptor(HeaderInterceptor())
            .cache(cache)
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
                    ToastFail(t.toString())
                }

                override fun onResponse(call: Call<T>, response: Response<T>) {
                    if (response.isSuccessful) {
                        //请求成功，将请求结果拿到并手动恢复所在协程
                        it.resume(response.body()!!)
                    } else {
                        //请求状态异常，抛出异常，手动结束当前协程
                        //it.resumeWithException(Throwable(response.toString()))
                        ToastFail(response.toString())
                    }
                }
            })
        }
    }

}