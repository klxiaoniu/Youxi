package com.glittering.youxi.ui.activity

import android.content.Intent
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.AbsoluteSizeSpan
import android.view.View
import androidx.appcompat.widget.PopupMenu
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.glittering.youxi.MyApplication.Companion.loggedInUser
import com.glittering.youxi.R
import com.glittering.youxi.data.BidInfoAdapter
import com.glittering.youxi.data.BidInfoResponse
import com.glittering.youxi.data.DeleteOrderResponse
import com.glittering.youxi.data.Order
import com.glittering.youxi.data.OrderInfoResponse
import com.glittering.youxi.data.OrderService
import com.glittering.youxi.data.PayRequest
import com.glittering.youxi.data.PayResponse
import com.glittering.youxi.data.ServiceCreator
import com.glittering.youxi.data.UserInfoResponse
import com.glittering.youxi.data.UserService
import com.glittering.youxi.databinding.ActivityOrderDetailBinding
import com.glittering.youxi.ui.dialog.BottomBiddingDialog
import com.glittering.youxi.utils.DarkUtil.Companion.reverseColorIfDark
import com.glittering.youxi.utils.DialogUtil
import com.glittering.youxi.utils.ToastFail
import com.glittering.youxi.utils.ToastInfo
import com.glittering.youxi.utils.ToastSuccess
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.gson.Gson
import okhttp3.FormBody
import okhttp3.MediaType
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class OrderDetailActivity : BaseActivity<ActivityOrderDetailBinding>() {
    var order: Order? = null
    var adapter: BidInfoAdapter? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding.back.setOnClickListener {
            finish()
        }
        reverseColorIfDark(
            listOf(
                binding.back,
                binding.option,
                binding.ivFavorite,
//                binding.ivLike,
//                binding.ivDislike
                binding.ivPricing
            )
        )
        val orderId = intent.getIntExtra("order_id", -1)
        if (orderId == -1) {
            finish()
            return
        }
        val orderService = ServiceCreator.create<OrderService>()
        orderService.getOrderInfo(orderId).enqueue(object : Callback<OrderInfoResponse> {
            override fun onResponse(
                call: Call<OrderInfoResponse>,
                response: Response<OrderInfoResponse>
            ) {
                if (response.body() != null) {
                    if (response.body()!!.code == 200) {
                        order = response.body()!!.data[0]
                        val option = RequestOptions()
                            .placeholder(R.drawable.error)
                            .error(R.drawable.error)
                            .diskCacheStrategy(DiskCacheStrategy.NONE)
                        Glide.with(this@OrderDetailActivity)
                            .load(order!!.order_picture)
                            .apply(option)
                            .into(binding.ivOrderPicture)
                        binding.tvOrderTitle.text = order!!.order_title
                        val s = order!!.order_price.toString().split(".")
                        val text = "￥" + order!!.order_price.toString()
                        val textSpan = SpannableStringBuilder(text)
                        textSpan.setSpan(
                            AbsoluteSizeSpan(30),
                            0,
                            1,
                            Spannable.SPAN_INCLUSIVE_INCLUSIVE
                        )
                        textSpan.setSpan(
                            AbsoluteSizeSpan(50),
                            text.indexOf(".") + 1,
                            text.length - 1,
                            Spannable.SPAN_INCLUSIVE_INCLUSIVE
                        )
                        textSpan.setSpan(
                            AbsoluteSizeSpan(30),
                            text.length - 1,
                            text.length,
                            Spannable.SPAN_INCLUSIVE_INCLUSIVE
                        )
                        binding.tvOrderPrice.text = textSpan
                        binding.tvOrderDescription.text = order!!.order_description

                        if (order!!.seller_id == loggedInUser?.id) {
                            binding.bottom.visibility = View.GONE
                        }

                        val userService = ServiceCreator.create<UserService>()
                        userService.getUserInfo(order!!.seller_id)
                            .enqueue(object : Callback<UserInfoResponse> {
                                override fun onResponse(
                                    call: Call<UserInfoResponse>,
                                    response: Response<UserInfoResponse>
                                ) {
                                    if (response.body() != null) {
                                        if (response.body()!!.code == 200) {
                                            val seller = response.body()!!.data[0]
                                            binding.tvUsername.text = seller.name
                                            Glide.with(this@OrderDetailActivity)
                                                .load(seller.photo)
                                                .apply(option)
                                                .into(binding.ivAvatar)
                                            binding.tvAll.text = seller.orders.toString()
                                        } else ToastFail(response.body()!!.message)
                                    } else ToastFail(getString(R.string.toast_response_error))
                                }

                                override fun onFailure(call: Call<UserInfoResponse>, t: Throwable) {
                                    ToastFail(getString(R.string.toast_response_error))
                                }
                            })


                        orderService.getBidInfo(orderId, 1)
                            .enqueue(object : Callback<BidInfoResponse> {
                                override fun onResponse(
                                    call: Call<BidInfoResponse>,
                                    response: Response<BidInfoResponse>
                                ) {
                                    if (response.body() != null) {
                                        if (response.body()!!.code == 200) {
                                            val bids = response.body()!!.data
                                            if (bids.isNotEmpty()) {
                                                adapter = BidInfoAdapter(bids)
                                                binding.rvBidinfo.layoutManager =
                                                    LinearLayoutManager(this@OrderDetailActivity).apply {
                                                        orientation = LinearLayoutManager.VERTICAL
                                                    }
                                                binding.rvBidinfo.adapter = adapter
                                            }
                                        } else ToastFail(response.body()!!.message)
                                    } else ToastFail(getString(R.string.toast_response_error))
                                }

                                override fun onFailure(call: Call<BidInfoResponse>, t: Throwable) {
                                    ToastFail(getString(R.string.toast_response_error))
                                }
                            })


                        binding.option.setOnClickListener {
                            val popupMenu = PopupMenu(this@OrderDetailActivity, binding.option)
                            menuInflater.inflate(R.menu.order_detail_menu, popupMenu.menu)
                            //TODO:权限判断，不显示一些选项
                            popupMenu.setOnMenuItemClickListener { item ->
                                when (item.itemId) {
                                    R.id.action_share -> ToastInfo("分享-Not implemented")
                                    R.id.action_report -> ToastInfo("举报-Not implemented")
                                    R.id.action_delete -> {
                                        deleteOrder(orderId)
                                    }

                                    R.id.action_edit -> {
                                        val intent = Intent(
                                            this@OrderDetailActivity,
                                            NewOrderActivity::class.java
                                        )
                                        intent.putExtra("order_id", order!!.order_id)
                                        startActivity(intent)
                                    }
                                }
                                true
                            }
                            popupMenu.show()
                        }
                    } else ToastFail(response.message())
                } else ToastFail(getString(R.string.toast_response_error))
            }

            override fun onFailure(call: Call<OrderInfoResponse>, t: Throwable) {
                t.printStackTrace()
                ToastFail(getString(R.string.toast_response_error))
            }
        })

        binding.llPricing.setOnClickListener {
            BottomBiddingDialog(this, orderId).show()
        }

        binding.btnBuy.setOnClickListener {
            if (loggedInUser == null) {
                ToastInfo("请先登录")
                startActivity(Intent(this, LoginActivity::class.java))
                return@setOnClickListener
            }
            if (order == null) {
                ToastInfo("正在加载商品信息，请稍候")
            } else {
                //BottomPayDialog(this, order!!.order_price).show()
                val dialog = MaterialAlertDialogBuilder(this)
                    .setTitle("确认购买")
                    .setMessage("确认使用钱包余额购买该商品？")
                    .setPositiveButton("确定") { dialog, which ->
                        val payData = PayRequest(orderId)
                        val json = FormBody.create(
                            MediaType.parse("application/json; charset=utf-8"),
                            Gson().toJson(payData)
                        )
                        orderService.pay(json).enqueue(object : Callback<PayResponse> {
                            override fun onResponse(
                                call: Call<PayResponse>,
                                response: Response<PayResponse>
                            ) {
                                if (response.body() != null) {
                                    val code = response.body()?.code
                                    if (code == 200) {
                                        ToastSuccess(response.body()?.message.toString())
                                    } else {
                                        ToastFail(response.body()?.message.toString())
                                    }
                                } else ToastFail(getString(R.string.toast_response_error))
                            }

                            override fun onFailure(call: Call<PayResponse>, t: Throwable) {
                                t.printStackTrace()
                                ToastFail(getString(R.string.toast_response_error))
                            }
                        })

                    }
                    .setNegativeButton("取消", null)
                    .show()
                DialogUtil.stylize(dialog)
            }
        }
        binding.btnChat.setOnClickListener {
            val intent = Intent(this, ChatActivity::class.java)
            intent.putExtra("chat_id", order?.seller_id?.toLong())
            startActivity(intent)
        }
    }

    private fun deleteOrder(orderId: Int) {
        val dialog =
            MaterialAlertDialogBuilder(this@OrderDetailActivity)
                .setTitle("确认删除")
                .setMessage("确认删除此商品？")
                .setPositiveButton("确定") { _, _ ->
                    val orderService =
                        ServiceCreator.create<OrderService>()

                    orderService.deleteOrder(orderId)
                        .enqueue(object :
                            Callback<DeleteOrderResponse> {
                            override fun onResponse(
                                call: Call<DeleteOrderResponse>,
                                response: Response<DeleteOrderResponse>
                            ) {
                                val code = response.body()?.code
                                if (code == 200) {
                                    ToastSuccess(response.body()?.message.toString())
                                    finish()
                                } else {
                                    ToastFail(response.body()?.message.toString())
                                }
                            }

                            override fun onFailure(
                                call: Call<DeleteOrderResponse>,
                                t: Throwable
                            ) {
                                t.printStackTrace()
                                ToastFail(getString(R.string.toast_response_error))
                            }
                        })
                }
                .setNegativeButton("取消", null)
                .show()
        DialogUtil.stylize(dialog)
    }

}