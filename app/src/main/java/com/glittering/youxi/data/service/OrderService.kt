package com.glittering.youxi.data.service

import com.glittering.youxi.data.bean.BidInfo
import com.glittering.youxi.data.bean.MyOrderData
import com.glittering.youxi.data.bean.Order
import com.glittering.youxi.data.bean.SearchOrder
import com.glittering.youxi.data.response.BaseDataResponse
import com.glittering.youxi.data.response.BaseResponse
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.*

interface OrderService {

    @GET("order/information")
    fun getOrderInfo(@Query("order_id") orderId: Int): Call<BaseDataResponse<List<Order>>>

    @POST("order/bidding")
    fun bid(
        @Body body: RequestBody
    ): Call<BaseResponse>

    @PUT("order/collecting")
    fun addFavorite(
        @Body body: RequestBody
    ): Call<BaseResponse>

    @GET("bid/searching")
    fun getBidInfo(
        @Query("order_id") orderId: Int,
        @Query("page") page: Int
    ): Call<BaseDataResponse<List<BidInfo>>>

    @POST("neworder")
    fun newOrder(
        @Body body: RequestBody
    ): Call<BaseResponse>

    @PUT("order/updating")
    fun updateOrder(
        @Body body: RequestBody
    ): Call<BaseResponse>

    @DELETE("order/deleting")
    fun deleteOrder(
        @Query("order_id") id: Int
    ): Call<BaseResponse>

    @PUT("order/confirming")
    fun confirmOrder(
        @Body body: RequestBody
    ): Call<BaseDataResponse<MyOrderData>>

    @PUT("order/delivering")
    fun deliverOrder(
        @Body body: RequestBody
    ): Call<BaseDataResponse<MyOrderData>>

    @GET("order/searching")
    fun search(
        @Query("keyword") key: String, @Query("page") page: Int
    ): Call<BaseDataResponse<List<SearchOrder>>>

    @PUT("order/paying")
    fun pay(
        @Body body: RequestBody
    ): Call<BaseResponse>

    @POST("exception/100")
    fun reportException1(
        @Body body: RequestBody
    ): Call<BaseResponse>

    @POST("exception/200")
    fun reportException2(
        @Body body: RequestBody
    ): Call<BaseResponse>
}