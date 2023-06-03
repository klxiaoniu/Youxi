package com.glittering.youxi.data

import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.*

interface UserService {

    @POST("login")
    fun login(
        @Body body: RequestBody
    ): Call<LoginResponse>

    @POST("register")
    fun register(
        @Body body: RequestBody
    ): Call<RegisterResponse>

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

    @GET("information")
    fun getPersonalInfo(
    ): Call<PersonalInfoResponse>

    @Headers("Cache-Control:public ,max-age=60")
    @GET("user/information")
    fun getUserInfo(
        @Query("id") id: Long
    ): Call<UserInfoResponse>

    @POST("temp/login")
    fun loginWithToken(
    ): Call<LoginResponse>

    @GET("message")
    fun getSysMsg(
        @Query("page") page: Int
    ): Call<SysMsgResponse>

    @GET("order/view/collection")
    fun getCollection(
        @Query("page") page: Int
    ): Call<CollectionResponse>
}