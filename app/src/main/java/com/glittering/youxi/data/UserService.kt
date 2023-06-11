package com.glittering.youxi.data

import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.*

interface UserService {

    @POST("login")
    fun login(
        @Body body: RequestBody
    ): Call<BaseDataResponse<LoginUser>>

    @POST("register")
    fun register(
        @Body body: RequestBody
    ): Call<BaseResponse>

    @GET("code")
    fun queryCode(
    ): Call<CodeResponse>

    @PUT("information/updating")
    fun updating(
        @Body body: RequestBody
    ): Call<BaseResponse>

    @POST("email")
    fun queryEmailCode(
        @Body body: RequestBody
    ): Call<BaseResponse>

    @GET("information")
    fun getPersonalInfo(
    ): Call<BaseDataResponse<List<PersonalInfo>>>

    @Headers("Cache-Control:public ,max-age=60")
    @GET("user/information")
    fun getUserInfo(
        @Query("id") id: Long
    ): Call<BaseDataResponse<List<UserInfo>>>

    @POST("temp/login")
    fun loginWithToken(
    ): Call<BaseDataResponse<LoginUser>>

    @GET("message")
    fun getSysMsg(
        @Query("page") page: Int
    ): Call<BaseDataResponse<List<SysMsg>>>

    @GET("order/view/collection")
    fun getCollection(
        @Query("page") page: Int
    ): Call<BaseDataResponse<List<CollectionData>>>

    @GET("order/view/mine")
    fun getMyOrder(
        @Query("type") type: String,
        @Query("page") page: Int
    ): Call<BaseDataResponse<List<MyOrderData>>>
}