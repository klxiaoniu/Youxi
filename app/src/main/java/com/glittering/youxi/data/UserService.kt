package com.glittering.youxi.data

import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.*

interface UserService {

    @POST("login")
    fun login(
        @Body body: RequestBody
    ): Call<LoginResponse>

    @GET("code")
    fun queryCode(
    ): Call<CodeResponse>

    @PUT("information/updating")
    fun updating(
        @Body body: RequestBody
    ): Call<UpdatingResponse>

    @POST("email")
    fun queryEmailCode(
        @Body body: RequestBody
    ): Call<EmailResponse>
}