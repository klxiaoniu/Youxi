package com.glittering.youxi.ui.adapter

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
import com.glittering.youxi.data.BaseDataResponse
import com.glittering.youxi.data.BaseResponse
import com.glittering.youxi.data.ConfirmOrderRequest
import com.glittering.youxi.data.DeliverOrderRequest
import com.glittering.youxi.data.ExceptionOneRequest
import com.glittering.youxi.data.MyOrderData
import com.glittering.youxi.data.OrderService
import com.glittering.youxi.data.ServiceCreator
import com.glittering.youxi.ui.activity.OrderDetailActivity
import com.glittering.youxi.utils.DialogUtil
import com.glittering.youxi.utils.RequestUtil.Companion.generateJson
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

class MyOrderAdapter(var list: MutableList<MyOrderData>, val type: String, val activity: Activity) :
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
                                        confirmOrder(position, true)
                                    }.setNegativeButton("拒绝验货") { _, _ ->
                                        confirmOrder(position, false)
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
                                        deliverOrder(position, true)
                                    }.setNegativeButton("拒绝发货") { _, _ ->
                                        deliverOrder(position, false)
                                    }.setNeutralButton("取消", null).show().let {
                                        DialogUtil.stylize(it)
                                    }
                            }
                        }
                    }
                }

                "已结束" -> {
                    if (type == "buying") {
                        holder.btnOperate.let {
                            it.text = "举报"
                            it.visibility = View.VISIBLE
                            it.setOnClickListener {
                                MaterialAlertDialogBuilder(activity).setTitle("举报订单")
                                    .setMessage("将紧急冻结卖家钱包，移交管理员审核账号情况。情况属实，若卖家钱包足够交易价格，平台按交易价格返回给买家，解冻卖家钱包，将卖家加入黑名单。")
                                    .setPositiveButton("确定") { _, _ ->
                                        reportOrder1(position)
                                    }.setNegativeButton("取消", null)
                                    .show().let {
                                        DialogUtil.stylize(it)
                                    }
                            }
                        }
                    } else {
                        holder.btnOperate.let {
                            it.text = "举报"
                            it.visibility = View.VISIBLE
                            it.setOnClickListener {
                                MaterialAlertDialogBuilder(activity).setTitle("举报订单")
                                    .setMessage("将提交管理员审核账号情况。账号确实受损，则按照账号受损情况将交易金按协议比例发给卖家，剩余交易金返回买家。")
                                    .setPositiveButton("确定") { _, _ ->
                                        reportOrder2(position)
                                    }.setNegativeButton("取消", null)
                                    .show().let {
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

    private fun reportOrder2(position: Int) {
        val orderService = ServiceCreator.create<OrderService>()
        val json = generateJson(ExceptionOneRequest(list[position].order_id))   //参数和1一样
        orderService.reportException2(json).enqueue(object : Callback<BaseResponse> {
            override fun onResponse(call: Call<BaseResponse>, response: Response<BaseResponse>) {
                if (response.body() != null) {
                    if (response.body()!!.code == 200) ToastSuccess(response.body()!!.message)
                    else ToastFail(response.body()!!.message)
                } else {
                    ToastFail(activity.getString(R.string.toast_response_error))
                }
            }

            override fun onFailure(call: Call<BaseResponse>, t: Throwable) {
                ToastFail(activity.getString(R.string.toast_response_error))
            }
        })

    }

    private fun reportOrder1(position: Int) {
        val orderService = ServiceCreator.create<OrderService>()
        val json = generateJson(ExceptionOneRequest(list[position].order_id))
        orderService.reportException1(json).enqueue(object : Callback<BaseResponse> {
            override fun onResponse(call: Call<BaseResponse>, response: Response<BaseResponse>) {
                if (response.body() != null) {
                    if (response.body()!!.code == 200) ToastSuccess(response.body()!!.message)
                    else ToastFail(response.body()!!.message)
                } else {
                    ToastFail(activity.getString(R.string.toast_response_error))
                }
            }

            override fun onFailure(call: Call<BaseResponse>, t: Throwable) {
                ToastFail(activity.getString(R.string.toast_response_error))
            }
        })
    }

    private fun confirmOrder(position: Int, status: Boolean) {
        val orderService = ServiceCreator.create<OrderService>()
        val data = ConfirmOrderRequest(list[position].order_id, if (status) "True" else "False")
        val json = FormBody.create(
            MediaType.parse("application/json; charset=utf-8"), Gson().toJson(data)
        )
        orderService.confirmOrder(json).enqueue(object : Callback<BaseDataResponse<MyOrderData>> {
            override fun onResponse(
                call: Call<BaseDataResponse<MyOrderData>>,
                response: Response<BaseDataResponse<MyOrderData>>
            ) {
                val resp = response.body()
                if (resp != null) {
                    if (resp.code == 200) {
                        setDataInList(resp.data, position)
                        ToastSuccess(resp.message)
                    } else {
                        ToastFail(resp.message)
                    }
                }
            }

            override fun onFailure(
                call: Call<BaseDataResponse<MyOrderData>>, t: Throwable
            ) {
                ToastFail("验货失败")
            }
        })

    }

    private fun deliverOrder(position: Int, status: Boolean) {
        val orderService = ServiceCreator.create<OrderService>()
        val data = DeliverOrderRequest(list[position].order_id, if (status) "True" else "False")
        val json = FormBody.create(
            MediaType.parse("application/json; charset=utf-8"), Gson().toJson(data)
        )
        orderService.deliverOrder(json).enqueue(object : Callback<BaseDataResponse<MyOrderData>> {
            override fun onResponse(
                call: Call<BaseDataResponse<MyOrderData>>,
                response: Response<BaseDataResponse<MyOrderData>>
            ) {
                val resp = response.body()
                if (resp != null) {
                    if (resp.code == 200) {
                        setDataInList(resp.data, position)
                        ToastSuccess(resp.message)
                    } else {
                        ToastFail(resp.message)
                    }
                }
            }

            override fun onFailure(
                call: Call<BaseDataResponse<MyOrderData>>, t: Throwable
            ) {
                ToastFail("发货失败")
            }
        })

    }

    fun setDataInList(order: MyOrderData, i: Int) {
        list[i] = order
        notifyItemChanged(i)
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setAdapterList(list2: MutableList<MyOrderData>) {
        list = list2
        notifyDataSetChanged()
    }

    fun plusAdapterList(list2: MutableList<MyOrderData>) {
        list.addAll(list2)
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