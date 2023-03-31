package com.glittering.youxi.data

import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.http.*

interface FundService {

    @GET("funds")
    fun getFunds(@Query("page") page: Int): Call<List<Good>>

    @GET("fundstocheck")
    fun getFundsToCheck(@Query("token") token: String): Call<List<Good>>

    @GET("onefunds")
    fun getOneFunds(@Query("id") id: Long): Call<List<Good>>

    @GET("searchfunds")
    fun searchFunds(@Query("key") key: String): Call<List<Good>>

    @GET("funds/{id}")
    fun getFund(@Path("id") id: Long): Call<Good>

    @GET("users/{id}")
    fun getUser(@Path("id") id: Long): Call<LoginUser>

    @POST("admin/setpass")
    fun setPass(
        @Query("token") token: String,
        @Query("id") id: String,
        @Query("isPass") isPass: String
    ): Call<Map<String, Object>>

    @POST("delfund")
    fun delFundById(
        @Query("token") token: String,
        @Query("id") id: Long,
    ): Call<Map<String, Object>>

    @POST("newfund")
    fun newFund(
        @Query("token") token: String,  //TODO:更好的实现
        @Query("title") title: String,
        @Query("desc") desc: String,
        @Query("pic") pic: String,
        @Query("total") total: String
    ): Call<Map<String, Object>>

    @POST("fundaddpay")
    fun fundAddPay(
        @Query("token") token: String,  //TODO:更好的实现
        @Query("id") id: Long,
        @Query("pay") pay: Int
    ): Call<Map<String, Object>>

    @Multipart
    @POST("upload")
    fun uploadFile(
        @Query("token") token: String,
        @Part file: MultipartBody.Part
    ): Call<Map<String, Object>>
}