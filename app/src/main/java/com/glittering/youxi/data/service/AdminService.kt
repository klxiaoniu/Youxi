package com.glittering.youxi.data.service

import com.glittering.youxi.data.bean.ExceptionOrder
import com.glittering.youxi.data.bean.ReportUserData
import com.glittering.youxi.data.bean.VerifyingOrder
import com.glittering.youxi.data.response.BaseDataResponse
import com.glittering.youxi.data.response.BaseResponse
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Query

interface AdminService {

    @GET("order/view/verifying")
    fun getVerifyingOrder(
        @Query("page") page: Int
    ): Call<BaseDataResponse<List<VerifyingOrder>>>

    @PUT("order/verifying")
    fun verify(
        @Body body: RequestBody
    ): Call<BaseResponse>

    @GET("exception/1000")
    fun getExceptionList(
        @Query("page") page: Int
    ): Call<BaseDataResponse<List<ExceptionOrder>>>

    @POST("exception/110")
    fun handleException1(
        @Body body: RequestBody
    ): Call<BaseResponse>

    @POST("exception/210")
    fun handleException2(
        @Body body: RequestBody
    ): Call<BaseResponse>

    @GET("report/view/verifying")
    fun getReportList(
        @Query("page") page:Int
    ): Call<BaseDataResponse<List<ReportUserData>>>

    @PUT("report/verifying")
    fun handleReport(
        @Body body: RequestBody
    ): Call<BaseResponse>
}