package com.glittering.youxi.ui.dialog

import android.content.Context
import android.os.Bundle
import android.view.inputmethod.EditorInfo.IME_ACTION_DONE
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import com.glittering.youxi.R
import com.glittering.youxi.data.BaseResponse
import com.glittering.youxi.data.OrderBiddingRequest
import com.glittering.youxi.data.OrderService
import com.glittering.youxi.data.ServiceCreator
import com.glittering.youxi.utils.ToastFail
import com.glittering.youxi.utils.ToastSuccess
import com.google.gson.Gson
import okhttp3.FormBody
import okhttp3.MediaType
import retrofit2.Call
import retrofit2.Response
import kotlin.properties.Delegates

class BottomBiddingDialog(context: Context, orderid: Int) : CustomBottomDialog(context) {
    private var orderid by Delegates.notNull<Int>()

    init {
        this.orderid = orderid
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        setContentView(R.layout.dialog_bidding)
        super.onCreate(savedInstanceState)
    }

    override fun onStart() {
        super.onStart()
        val et = findViewById<EditText>(R.id.et_price)
        et.requestFocus()
        val inputManager: InputMethodManager =
            context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputManager.showSoftInput(et, InputMethodManager.SHOW_IMPLICIT);

        et.setOnEditorActionListener { v, actionId, event ->
            if (actionId == IME_ACTION_DONE && et.text.isNotEmpty()) {


                val orderService = ServiceCreator.create<OrderService>()
                val orderBiddingRequest = OrderBiddingRequest(
                    orderid,
                    et.text.toString().toDouble(),
                    "便宜点"
                )
                val json = FormBody.create(
                    MediaType.parse("application/json; charset=utf-8"),
                    Gson().toJson(orderBiddingRequest)
                )
                orderService.bid(json).enqueue(object : retrofit2.Callback<BaseResponse> {
                    override fun onResponse(
                        call: Call<BaseResponse>,
                        response: Response<BaseResponse>
                    ) {
                        if (response.body() != null) {
                            if (response.body()!!.code == 200) {
                                ToastSuccess(response.message())
                                dismiss()
                            } else ToastFail(response.message())
                        } else ToastFail(context.getString(R.string.toast_response_error))
                    }

                    override fun onFailure(call: Call<BaseResponse>, t: Throwable) {
                        t.printStackTrace()
                        ToastFail(t.toString())
                    }
                })

                true
            } else {
                false
            }
        }
    }
}