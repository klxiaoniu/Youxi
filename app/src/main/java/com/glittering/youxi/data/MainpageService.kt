package com.glittering.youxi.data

import retrofit2.Call
import retrofit2.http.*

interface MainpageService {

    @GET("home")
    fun getBanner(): Call<BaseDataResponse<List<BannerBean>>>

}