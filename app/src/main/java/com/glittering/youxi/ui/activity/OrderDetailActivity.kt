package com.glittering.youxi.ui.activity

import android.content.Intent
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.AbsoluteSizeSpan
import androidx.appcompat.widget.PopupMenu
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.glittering.youxi.R
import com.glittering.youxi.data.Order
import com.glittering.youxi.data.OrderInfoResponse
import com.glittering.youxi.data.OrderService
import com.glittering.youxi.data.ServiceCreator
import com.glittering.youxi.databinding.ActivityOrderDetailBinding
import com.glittering.youxi.ui.dialog.BottomBiddingDialog
import com.glittering.youxi.ui.dialog.BottomPayDialog
import com.glittering.youxi.utils.DarkUtil.Companion.reverseColorIfDark
import com.glittering.youxi.utils.ToastFail
import com.glittering.youxi.utils.ToastInfo
import retrofit2.Call
import retrofit2.Response

class OrderDetailActivity : BaseActivity<ActivityOrderDetailBinding>() {
    var order: Order? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding.back.setOnClickListener {
            finish()
        }
        reverseColorIfDark(
            listOf(
                binding.back,
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
        orderService.getOrderInfo(orderId).enqueue(object : retrofit2.Callback<OrderInfoResponse> {
            override fun onResponse(
                call: Call<OrderInfoResponse>,
                response: Response<OrderInfoResponse>
            ) {
                if (response.body() != null) {
                    println(response.body().toString())
                    if (response.body()!!.code == 200) {
                        order = response.body()!!.data[0]
                        val option = RequestOptions()
                            .placeholder(R.drawable.error)
                            .error(R.drawable.error)
                            .diskCacheStrategy(DiskCacheStrategy.ALL)
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

                        binding.option.setOnClickListener {
                            val popupMenu = PopupMenu(applicationContext, binding.option)
                            popupMenu.menuInflater.inflate(R.menu.order_detail_menu, popupMenu.menu)
                            //TODO:权限判断，不显示一些选项
                            popupMenu.setOnMenuItemClickListener { item ->
                                when (item.itemId) {
                                    R.id.action_share -> ToastInfo("分享-Not implemented")
                                    R.id.action_report -> ToastInfo("举报-Not implemented")
                                    R.id.action_delete -> ToastInfo("删除-Not implemented")
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
            if (order == null) {
                ToastInfo("正在加载商品信息，请稍候")
            } else {
                BottomPayDialog(this, order!!.order_price).show()
            }
//            BottomPayDialog(this,123.456).show()
        }
        binding.btnChat.setOnClickListener {
            val intent = Intent(this, ChatActivity::class.java)
            intent.putExtra("chat_id", order?.seller_id?.toLong())
            startActivity(intent)
        }
    }

}