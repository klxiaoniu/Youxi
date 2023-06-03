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

    @PUT("order/collecting")
    fun addFavorite(
        @Body body: RequestBody
    ): Call<AddFavoriteResponse>

    @GET("bid/searching")
    fun getBidInfo(
        @Query("order_id") orderId: Int,
        @Query("page") page: Int
    ): Call<BidInfoResponse>

    @POST("neworder")
    fun newOrder(
        @Body body: RequestBody
    ): Call<NewOrderResponse>

    @PUT("order/updating")
    fun updateOrder(
        @Body body: RequestBody
    ): Call<UpdateOrderResponse>

    @DELETE("order/deleting")
    fun deleteOrder(
        @Query("order_id") id: Int
    ): Call<DeleteOrderResponse>

    @GET("order/view/verifying")
    fun getVerifyingOrder(
        @Query("page") page: Int
    ): Call<VerifyingOrderResponse>

    @PUT("order/verifying")
    fun verify(
        @Body body: RequestBody
    ): Call<VerifyResponse>

    @GET("order/searching")
    fun search(
        @Query("keyword") key: String, @Query("page") page: Int
    ): Call<SearchResponse>

    @PUT("order/paying")
    fun pay(
        @Body body: RequestBody
    ): Call<PayResponse>
}