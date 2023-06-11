package com.glittering.youxi.ui.adapter

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.RadioButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.glittering.youxi.R
import com.glittering.youxi.data.BaseResponse
import com.glittering.youxi.data.OrderService
import com.glittering.youxi.data.ServiceCreator
import com.glittering.youxi.data.VerifyingOrder
import com.glittering.youxi.data.VerifyingOrderRequest
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
import retrofit2.Response

class VerifyingOrderAdapter(var list: List<VerifyingOrder>, val activity: Activity) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    val TYPE_FOOTVIEW: Int = 1 //item类型：footview
    val TYPE_ITEMVIEW: Int = 2 //item类型：itemview
    var typeItem = TYPE_ITEMVIEW

    inner class ItemViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val iv = view.findViewById<ImageView>(R.id.iv)
        val tvTitle = view.findViewById<TextView>(R.id.tv_title)
        val tvPrice = view.findViewById<TextView>(R.id.tv_price)
        val tvDesc = view.findViewById<TextView>(R.id.tv_desc)
        val btn_verify = view.findViewById<Button>(R.id.btn_verify)
    }

    inner class FootViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tv_info = view.findViewById<TextView>(R.id.tv_info)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (typeItem == TYPE_ITEMVIEW) {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_verifying_order, parent, false)
            ItemViewHolder(view)
        } else {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_verifying_foot, parent, false)
            FootViewHolder(view)

        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is ItemViewHolder) {
            val options = RequestOptions()
                .placeholder(R.drawable.loading)
                .error(R.drawable.error)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true)
            Glide.with(applicationContext)
                .load(list[position].picture)
                .apply(options)
                .into(holder.iv)
            holder.tvTitle.text = list[position].title
            holder.tvDesc.text = list[position].description
            holder.tvPrice.text = "￥" + list[position].price
            holder.itemView.setOnClickListener {
                val intent = Intent(it.context, OrderDetailActivity::class.java)
                intent.putExtra("order_id", list[position].order_id)
                it.context.startActivity(intent)
            }
            holder.btn_verify.setOnClickListener {
                verify(list[position].order_id)
            }
        } else if (holder is FootViewHolder) {
//            holder.tv_info.visibility = if (itemCount == 1) View.GONE else View.VISIBLE
            //无项目时（往往正加载）不显示footview

            //当点击footview时，将该事件回调出去
            holder.tv_info.setOnClickListener {
                footViewClickListener.invoke()
            }
        }


    }

    private fun verify(id: Int) {
        val view = activity.layoutInflater.inflate(R.layout.dialog_verify, null)

        val dialog = MaterialAlertDialogBuilder(activity)
            .setTitle("审核")
            .setView(view)
            .setPositiveButton("确定") { dialog, which ->
                val orderService = ServiceCreator.create<OrderService>()

                val body = FormBody.create(
                    MediaType.parse("application/json; charset=utf-8"),
                    Gson().toJson(
                        VerifyingOrderRequest(
                            id,
                            if (view.findViewById<RadioButton>(R.id.radio_pass).isChecked) "True" else "False"
                        )
                    )
                )
                orderService.verify(body)
                    .enqueue(object : retrofit2.Callback<BaseResponse> {
                        override fun onResponse(
                            call: Call<BaseResponse>,
                            response: Response<BaseResponse>
                        ) {
                            if (response.body() != null && response.body()!!.code == 200) {
                                ToastSuccess(response.body()!!.message)
                            } else {
                                ToastFail(response.body()!!.message)
                            }
                        }

                        override fun onFailure(call: Call<BaseResponse>, t: Throwable) {
                            ToastFail(applicationContext.getString(R.string.toast_response_error))
                        }
                    }
                    )
            }
            .setNegativeButton("取消", null)
            .show()
        DialogUtil.stylize(dialog)
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setAdapterList(list2: List<VerifyingOrder>) {
        list = list2
        notifyDataSetChanged()
    }

    fun plusAdapterList(list2: List<VerifyingOrder>) {
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

