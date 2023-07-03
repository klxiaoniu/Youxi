package com.glittering.youxi.data.service

import com.glittering.youxi.data.bean.Banner
import com.glittering.youxi.data.response.BaseDataResponse
import retrofit2.Call
import retrofit2.http.*

interface MainpageService {

    @GET("home")
    fun getBanner(): Call<BaseDataResponse<List<Banner>>>

}