package com.glittering.youxi.data

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.glittering.youxi.R
import com.glittering.youxi.ui.activity.OrderDetailActivity
import com.glittering.youxi.utils.DialogUtil
import com.glittering.youxi.utils.ToastFail
import com.glittering.youxi.utils.ToastInfo
import com.glittering.youxi.utils.ToastSuccess
import com.glittering.youxi.utils.applicationContext
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.gson.Gson
import okhttp3.FormBody
import okhttp3.MediaType
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MyOrderAdapter(var list: List<MyOrderData>, val type: String, val activity: Activity) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    val TYPE_FOOTVIEW: Int = 1 //item类型：footview
    val TYPE_ITEMVIEW: Int = 2 //item类型：itemview
    var typeItem = TYPE_ITEMVIEW

    inner class ItemViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val iv = view.findViewById<ImageView>(R.id.iv)
        val tvTitle = view.findViewById<TextView>(R.id.tv_title)
        val tvPrice = view.findViewById<TextView>(R.id.tv_price)
        val tvDesc = view.findViewById<TextView>(R.id.tv_desc)
        val btnOperate = view.findViewById<TextView>(R.id.btn_operate)
        val item = view
    }

    inner class FootViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var tv_msg = itemView.findViewById<TextView>(R.id.tv_info)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (typeItem == TYPE_ITEMVIEW) {
            val view =
                LayoutInflater.from(parent.context).inflate(R.layout.item_myorder, parent, false)
            ItemViewHolder(view)
        } else {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_search_foot, parent, false)
            FootViewHolder(view)

        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is ItemViewHolder) {
            val options = RequestOptions().placeholder(R.drawable.loading).error(R.drawable.error)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
            Glide.with(applicationContext).load(list[position].picture).apply(options)
                .into(holder.iv)
            holder.tvTitle.text = list[position].title
            holder.tvPrice.text = "￥" + list[position].price
            holder.tvDesc.text = list[position].status
            //"待审核", "已通过", "待验货", "待重审", "待发货", "已结束", "异常"
            when (list[position].status) {
                "待验货" -> {
                    if (type == "buying") {
                        holder.btnOperate.let {
                            it.text = "验货"
                            it.visibility = View.VISIBLE
                            it.setOnClickListener {
                                MaterialAlertDialogBuilder(activity).setTitle("确认验货")
                                    .setMessage("请确认货物满足您的要求后再验货哦")
                                    .setPositiveButton("确认验货") { _, _ ->
                                        confirmOrder(list[position].order_id, true)
                                    }.setNegativeButton("拒绝验货") { _, _ ->
                                        confirmOrder(list[position].order_id, false)
                                    }.setNeutralButton("取消", null).show().let {
                                        DialogUtil.stylize(it)
                                    }
                            }
                        }
                    }
                }

                "待发货" -> {
                    if (type == "selling") {
                        holder.btnOperate.let {
                            it.text = "发货"
                            it.visibility = View.VISIBLE
                            it.setOnClickListener {
                                MaterialAlertDialogBuilder(activity).setTitle("确认发货")
                                    .setMessage("确认发货吗？")
                                    .setPositiveButton("确认发货") { _, _ ->
                                        deliverOrder(list[position].order_id, true)
                                    }.setNegativeButton("拒绝发货") { _, _ ->
                                        deliverOrder(list[position].order_id, false)
                                    }.setNeutralButton("取消", null).show().let {
                                        DialogUtil.stylize(it)
                                    }
                            }
                        }
                    }
                }
            }
            holder.item.setOnClickListener {
                val intent = Intent(it.context, OrderDetailActivity::class.java)
                intent.putExtra("order_id", list[position].order_id)
                it.context.startActivity(intent)
            }
        } else if (holder is FootViewHolder) {
            holder.tv_msg.visibility = if (itemCount == 1) View.GONE else View.VISIBLE
            //无项目时（往往正加载）不显示footview

            //当点击footview时，将该事件回调出去
            holder.tv_msg.setOnClickListener {
                footViewClickListener.invoke()
            }
        }


    }

    private fun confirmOrder(orderId: Int, status: Boolean) {
        val orderService = ServiceCreator.create<OrderService>()
        val data = ConfirmOrderRequest(orderId, if (status) "True" else "False")
        val json = FormBody.create(
            MediaType.parse("application/json; charset=utf-8"), Gson().toJson(data)
        )
        orderService.confirmOrder(json).enqueue(object : Callback<ConfirmOrderResponse> {
            override fun onResponse(
                call: Call<ConfirmOrderResponse>, response: Response<ConfirmOrderResponse>
            ) {
                val resp = response.body()
                if (resp != null) {
                    if (resp.code == 200) {
                        ToastSuccess(resp.message)
                    } else {
                        ToastFail(resp.message)
                    }
                }
            }

            override fun onFailure(
                call: Call<ConfirmOrderResponse>, t: Throwable
            ) {
                ToastFail("验货失败")
            }
        })

    }

    private fun deliverOrder(orderId: Int, status: Boolean) {
        val orderService = ServiceCreator.create<OrderService>()
        val data = DeliverOrderRequest(orderId, if (status) "True" else "False")
        val json = FormBody.create(
            MediaType.parse("application/json; charset=utf-8"), Gson().toJson(data)
        )
        orderService.deliverOrder(json).enqueue(object : Callback<DeliverOrderResponse> {
                override fun onResponse(
                    call: Call<DeliverOrderResponse>, response: Response<DeliverOrderResponse>
                ) {
                    val resp = response.body()
                    if (resp != null) {
                        if (resp.code == 200) {
                            ToastSuccess(resp.message)
                        } else {
                            ToastFail(resp.message)
                        }
                    }
                }

                override fun onFailure(
                    call: Call<DeliverOrderResponse>, t: Throwable
                ) {
                    ToastFail("发货失败")
                }
            })

    }

    @SuppressLint("NotifyDataSetChanged")
    fun setAdapterList(list2: List<MyOrderData>) {
        list = list2
        notifyDataSetChanged()
    }

    fun plusAdapterList(list2: List<MyOrderData>) {
        list = list.plus(list2)
        notifyItemRangeInserted(list.size - list2.size, list2.size)
    }

    override fun getItemCount() = list.size + 1

    override fun getItemViewType(position: Int): Int {
        //设置在数据最底部显示footview
        typeItem = if (position == list.size) TYPE_FOOTVIEW else TYPE_ITEMVIEW
        return typeItem
    }

    var footviewPosition = 0
    override fun onViewAttachedToWindow(holder: RecyclerView.ViewHolder) {
        super.onViewAttachedToWindow(holder)
        if (footviewPosition == holder.adapterPosition) {
            return
        }
        if (holder is FootViewHolder) {
            footviewPosition = holder.adapterPosition
            //回调查询事件
            footViewAttachedToWindowListener.invoke()
        }
    }


    //定义footview附加到Window上时的回调
    private var footViewAttachedToWindowListener: () -> Unit = { }
    fun setOnFootViewAttachedToWindowListener(pListener: () -> Unit) {
        this.footViewAttachedToWindowListener = pListener
    }

    //定义footview点击时的回调
    private var footViewClickListener: () -> Unit = { ToastInfo("已无更多") }  //如果是默认，说明不是分页查询
    fun setOnFootViewClickListener(pListener: () -> Unit) {
        this.footViewClickListener = pListener
    }
}