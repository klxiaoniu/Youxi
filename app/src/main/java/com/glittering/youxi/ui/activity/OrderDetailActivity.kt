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
import com.flyjingfish.openimagelib.OpenImage
import com.flyjingfish.openimagelib.enums.MediaType
import com.glittering.youxi.R
import com.glittering.youxi.data.bean.BidInfo
import com.glittering.youxi.data.bean.Order
import com.glittering.youxi.data.bean.UserInfo
import com.glittering.youxi.data.request.AddFavoriteRequest
import com.glittering.youxi.data.request.PayRequest
import com.glittering.youxi.data.response.BaseDataResponse
import com.glittering.youxi.data.response.BaseResponse
import com.glittering.youxi.data.service.OrderService
import com.glittering.youxi.data.service.ServiceCreator
import com.glittering.youxi.data.service.UserService
import com.glittering.youxi.databinding.ActivityOrderDetailBinding
import com.glittering.youxi.manager.TaskManager
import com.glittering.youxi.manager.UserStateManager
import com.glittering.youxi.ui.adapter.BidInfoAdapter
import com.glittering.youxi.ui.dialog.BottomBiddingDialog
import com.glittering.youxi.utils.DarkUtil.Companion.reverseColorIfDark
import com.glittering.youxi.utils.DialogUtil
import com.glittering.youxi.utils.RequestUtil
import com.glittering.youxi.utils.ToastFail
import com.glittering.youxi.utils.ToastInfo
import com.glittering.youxi.utils.ToastSuccess
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.zhpan.bannerview.transform.ScaleInTransformer
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class OrderDetailActivity : BaseActivity<ActivityOrderDetailBinding>() {
    var order: Order? = null
    var adapter: BidInfoAdapter? = null
    val orderService = ServiceCreator.create<OrderService>()
    val userService = ServiceCreator.create<UserService>()
    val taskManager = TaskManager()
    override val contentView: View
        get() = binding.contentView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding.back.setOnClickListener {
            finish()
        }
        reverseColorIfDark(
            listOf(
                binding.back, binding.option, binding.ivFavorite,
                binding.ivPricing
            )
        )
        val orderId = intent.getIntExtra("order_id", -1)
        if (orderId == -1) {
            finish()
            return
        }


        taskManager.setTaskListener(this,object : TaskManager.TaskListener() {
            override fun onTaskSuccess() {
                showContentView()
                binding.llPricing.setOnClickListener {
                    if (!UserStateManager.getInstance().checkLogin(this@OrderDetailActivity)) return@setOnClickListener
                    BottomBiddingDialog(this@OrderDetailActivity, orderId).show()
                }

                binding.btnBuy.setOnClickListener {
                    if (!UserStateManager.getInstance().checkLogin(this@OrderDetailActivity)) return@setOnClickListener
                    if (order == null) {
                        ToastInfo("正在加载商品信息，请稍候")
                    } else {
                        //BottomPayDialog(this, order!!.order_price).show()
                        val dialog = MaterialAlertDialogBuilder(this@OrderDetailActivity).setTitle("确认购买")
                            .setMessage("确认使用钱包余额购买该商品？")
                            .setPositiveButton("确定") { dialog, which ->
                                val payData = PayRequest(orderId)
                                orderService.pay(RequestUtil.generateJson(payData))
                                    .enqueue(object : Callback<BaseResponse> {
                                        override fun onResponse(
                                            call: Call<BaseResponse>, response: Response<BaseResponse>
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

                                        override fun onFailure(call: Call<BaseResponse>, t: Throwable) {
                                            t.printStackTrace()
                                            ToastFail(getString(R.string.toast_response_error))
                                        }
                                    })

                            }.setNegativeButton("取消", null).show()
                        DialogUtil.stylize(dialog)
                    }
                }
                binding.btnChat.setOnClickListener {
                    if (!UserStateManager.getInstance().checkLogin(this@OrderDetailActivity)) return@setOnClickListener
                    val intent = Intent(this@OrderDetailActivity, ChatActivity::class.java)
                    intent.putExtra("chat_id", order?.seller_id?.toLong())
                    startActivity(intent)
                }
                binding.ivFavorite.setOnClickListener {
                    if (!UserStateManager.getInstance().checkLogin(this@OrderDetailActivity)) return@setOnClickListener
                    val json = RequestUtil.generateJson(AddFavoriteRequest(orderId))
                    orderService.addFavorite(json).enqueue(object : Callback<BaseResponse> {
                        override fun onResponse(
                            call: Call<BaseResponse>, response: Response<BaseResponse>
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

                        override fun onFailure(call: Call<BaseResponse>, t: Throwable) {
                            t.printStackTrace()
                            ToastFail(getString(R.string.toast_response_error))
                        }
                    })
                }
            }

            override fun onTaskFail() {
                showErrorView()
            }
        })
        getData(orderId)


    }

    private fun getData(orderId: Int) {

        showLoadingView()

        taskManager.add()
        orderService.getOrderInfo(orderId)
            .enqueue(object : Callback<BaseDataResponse<List<Order>>> {
                override fun onResponse(
                    call: Call<BaseDataResponse<List<Order>>>,
                    response: Response<BaseDataResponse<List<Order>>>
                ) {
                    if (response.body() != null) {
                        if (response.body()!!.code == 200) {
                            order = response.body()!!.data[0]
                            val option = RequestOptions().placeholder(R.drawable.error)
                                .error(R.drawable.error).diskCacheStrategy(DiskCacheStrategy.NONE)
                            Glide.with(this@OrderDetailActivity).load(order!!.order_picture)
                                .apply(option).into(binding.ivOrderPicture)
                            binding.ivOrderPicture.setOnClickListener {
                                OpenImage.with(this@OrderDetailActivity)
                                    .setClickImageView(binding.ivOrderPicture)
                                    .setSrcImageViewScaleType(binding.ivOrderPicture.scaleType,true)
                                    .setImageUrl(order!!.order_picture,MediaType.IMAGE)
                                    .addPageTransformer(ScaleInTransformer(0.85f))
                                    .show()
                            }
                            binding.tvOrderTitle.text = order!!.order_title
                            val text = "￥" + order!!.order_price.toString()
                            val textSpan = SpannableStringBuilder(text)
                            textSpan.setSpan(
                                AbsoluteSizeSpan(30), 0, 1, Spannable.SPAN_INCLUSIVE_INCLUSIVE
                            )
                            textSpan.setSpan(
                                AbsoluteSizeSpan(50),
                                2,
                                text.indexOf(".") + 1,
                                Spannable.SPAN_INCLUSIVE_INCLUSIVE
                            )
                            textSpan.setSpan(
                                AbsoluteSizeSpan(30),
                                text.indexOf(".") + 1,
                                text.length,
                                Spannable.SPAN_INCLUSIVE_INCLUSIVE
                            )
                            binding.tvOrderPrice.text = textSpan
                            binding.tvOrderDescription.text = order!!.order_description

                            if (order!!.seller_id == UserStateManager.getInstance().getUserId()) {
                                binding.bottom.visibility = View.GONE
                            }

                            taskManager.add()
                            userService.getUserInfo(order!!.seller_id)
                                .enqueue(object : Callback<BaseDataResponse<List<UserInfo>>> {
                                    override fun onResponse(
                                        call: Call<BaseDataResponse<List<UserInfo>>>,
                                        response: Response<BaseDataResponse<List<UserInfo>>>
                                    ) {
                                        if (response.body() != null) {
                                            if (response.body()!!.code == 200) {
                                                val seller = response.body()!!.data[0]
                                                binding.tvUsername.text = seller.name
                                                val option =
                                                    RequestOptions().placeholder(R.drawable.ic_default_avatar)
                                                        .error(R.drawable.ic_default_avatar)
                                                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                                                Glide.with(this@OrderDetailActivity)
                                                    .load(seller.photo).apply(option)
                                                    .into(binding.ivAvatar)
                                                binding.tvAll.text = seller.orders.toString()
                                            } else ToastFail(response.body()!!.message)
                                            taskManager.remove()
                                        } else taskManager.fail()
                                    }

                                    override fun onFailure(
                                        call: Call<BaseDataResponse<List<UserInfo>>>, t: Throwable
                                    ) {
                                        taskManager.fail()
                                    }
                                })


                            taskManager.add()
                            orderService.getBidInfo(orderId, 1)
                                .enqueue(object : Callback<BaseDataResponse<List<BidInfo>>> {
                                    override fun onResponse(
                                        call: Call<BaseDataResponse<List<BidInfo>>>,
                                        response: Response<BaseDataResponse<List<BidInfo>>>
                                    ) {
                                        if (response.body() != null) {
                                            if (response.body()!!.code == 200) {
                                                val bids = response.body()!!.data
                                                if (bids.isNotEmpty()) {
                                                    adapter = BidInfoAdapter(bids)
                                                    binding.rvBidinfo.layoutManager =
                                                        LinearLayoutManager(this@OrderDetailActivity).apply {
                                                            orientation =
                                                                LinearLayoutManager.VERTICAL
                                                        }
                                                    binding.rvBidinfo.adapter = adapter
                                                }
                                            } else ToastFail(response.body()!!.message)
                                            taskManager.remove()
                                        } else taskManager.fail()
                                    }

                                    override fun onFailure(
                                        call: Call<BaseDataResponse<List<BidInfo>>>, t: Throwable
                                    ) {
                                        taskManager.fail()
                                    }
                                })


                            binding.option.setOnClickListener {
                                val popupMenu = PopupMenu(this@OrderDetailActivity, binding.option)
                                menuInflater.inflate(R.menu.order_detail_menu, popupMenu.menu)
                                // 权限判断，不显示一些选项
                                if (order!!.seller_id != UserStateManager.getInstance()
                                        .getUserId() && !UserStateManager.getInstance().isAdmin()
                                ) {
                                    popupMenu.menu.removeItem(R.id.action_edit)
                                    popupMenu.menu.removeItem(R.id.action_delete)
                                }
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
                        taskManager.remove()
                    } else taskManager.fail()
                }

                override fun onFailure(call: Call<BaseDataResponse<List<Order>>>, t: Throwable) {
                    t.printStackTrace()
                    taskManager.fail()
                }
            })
    }

    private fun deleteOrder(orderId: Int) {
        MaterialAlertDialogBuilder(this@OrderDetailActivity).setTitle("确认删除")
            .setMessage("确认删除此商品？").setPositiveButton("确定") { _, _ ->
                val orderService = ServiceCreator.create<OrderService>()

                orderService.deleteOrder(orderId).enqueue(object : Callback<BaseResponse> {
                    override fun onResponse(
                        call: Call<BaseResponse>, response: Response<BaseResponse>
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
                        call: Call<BaseResponse>, t: Throwable
                    ) {
                        t.printStackTrace()
                        ToastFail(getString(R.string.toast_response_error))
                    }
                })
            }.setNegativeButton("取消", null).show().let {
                DialogUtil.stylize(it)
            }
    }

}