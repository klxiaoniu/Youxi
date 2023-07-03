package com.glittering.youxi.data.service

import com.glittering.youxi.data.bean.Code
import com.glittering.youxi.data.bean.CollectionData
import com.glittering.youxi.data.bean.LoginUser
import com.glittering.youxi.data.bean.MoneyData
import com.glittering.youxi.data.bean.MyOrderData
import com.glittering.youxi.data.bean.PersonalInfo
import com.glittering.youxi.data.bean.RechargeData
import com.glittering.youxi.data.bean.SysMsg
import com.glittering.youxi.data.bean.UserInfo
import com.glittering.youxi.data.response.BaseDataResponse
import com.glittering.youxi.data.response.BaseResponse
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
    ): Call<Code>

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

    @GET("money")
    fun getMoney(
    ): Call<BaseDataResponse<MoneyData>>

    @PUT("money")
    fun operateMoney(
        @Body body: RequestBody
    ): Call<BaseDataResponse<RechargeData>>

    @POST("reporting")
    fun reportUser(
        @Body body: RequestBody
    ): Call<BaseResponse>
}