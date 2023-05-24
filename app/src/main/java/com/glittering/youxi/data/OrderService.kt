package com.glittering.youxi.data

import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.*

interface OrderService {

    @GET("order/information")
    fun getOrderInfo(@Query("order_id") orderId: Int): Call<OrderInfoResponse>

    @POST("order/bidding")
    fun bid(
        @Body body: RequestBody
    ): Call<OrderBiddingResponse>

    @POST("neworder")
    fun newOrder(
        @Body body: RequestBody
    ): Call<NewOrderResponse>

    @PUT("order/updating")
    fun updateOrder(
        @Body body: RequestBody
    ): Call<UpdateOrderResponse>

    @GET("order/view/verifying")
    fun getVerifyingOrder(
        @Body body: RequestBody
    ): Call<VerifyingOrderResponse>

    @PUT("order/verifying")
    fun verify(
        @Body body: RequestBody
    ): Call<VerifyResponse>
}