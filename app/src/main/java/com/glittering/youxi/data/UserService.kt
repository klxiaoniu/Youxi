package com.glittering.youxi.data

import com.google.gson.JsonObject
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.*

interface UserService {

    @POST("login")
    fun login(
        @Body body: RequestBody
    ): Call<LoginResponse>

}