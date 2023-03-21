package com.glittering.youxi.data

import com.xiaoniu.fund.ToastLong
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

object ServiceCreator {
    const val BASE_URL = "http://202.182.125.24:20626/"
    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
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