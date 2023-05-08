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

}