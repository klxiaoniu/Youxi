package com.glittering.youxi.ui.adapter

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.RadioButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.glittering.youxi.R
import com.glittering.youxi.data.bean.ExceptionOrder
import com.glittering.youxi.data.request.ExceptionOneHandleRequest
import com.glittering.youxi.data.request.ExceptionTwoHandleRequest
import com.glittering.youxi.data.response.BaseResponse
import com.glittering.youxi.data.service.AdminService
import com.glittering.youxi.data.service.ServiceCreator
import com.glittering.youxi.ui.activity.OrderDetailActivity
import com.glittering.youxi.utils.DialogUtil
import com.glittering.youxi.utils.RequestUtil
import com.glittering.youxi.utils.ToastFail
import com.glittering.youxi.utils.ToastInfo
import com.glittering.youxi.utils.ToastSuccess
import com.glittering.youxi.utils.applicationContext
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import retrofit2.Call
import retrofit2.Response

class ExceptionOrderAdapter(var list: List<ExceptionOrder>, val activity: Activity) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    val TYPE_FOOTVIEW: Int = 1 //item类型：footview
    val TYPE_ITEMVIEW: Int = 2 //item类型：itemview
    var typeItem = TYPE_ITEMVIEW

    inner class ItemViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val iv = view.findViewById<ImageView>(R.id.iv)
        val tvTitle = view.findViewById<TextView>(R.id.tv_title)

        //        val tvPrice = view.findViewById<TextView>(R.id.tv_price)
        val tvDesc = view.findViewById<TextView>(R.id.tv_desc)
        val btnHandle = view.findViewById<Button>(R.id.btn_handle)
    }

    inner class FootViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tv_info = view.findViewById<TextView>(R.id.tv_info)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (typeItem == TYPE_ITEMVIEW) {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_exception_order, parent, false)
            ItemViewHolder(view)
        } else {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_verifying_foot, parent, false)
            FootViewHolder(view)

        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is ItemViewHolder) {
            val options = RequestOptions().placeholder(R.drawable.loading).error(R.drawable.error)
                .diskCacheStrategy(DiskCacheStrategy.NONE).skipMemoryCache(true)
            Glide.with(applicationContext).load(list[position].picture).apply(options)
                .into(holder.iv)
            holder.tvTitle.text = list[position].title
            holder.tvDesc.text = list[position].type
//            holder.tvPrice.text = "￥" + list[position].price
            holder.itemView.setOnClickListener {
                val intent = Intent(it.context, OrderDetailActivity::class.java)
                intent.putExtra("order_id", list[position].order_id)
                it.context.startActivity(intent)
            }
            holder.btnHandle.setOnClickListener {
                handle(position)
            }
        } else if (holder is FootViewHolder) {
            holder.tv_info.visibility = if (itemCount == 1) View.GONE else View.VISIBLE
            //无项目时（往往正加载）不显示footview

            //当点击footview时，将该事件回调出去
            holder.tv_info.setOnClickListener {
                footViewClickListener.invoke()
            }
        }


    }

    private fun handle(id: Int) {
        when (list[id].type) {
            "异常1" -> {
                val view = activity.layoutInflater.inflate(R.layout.dialog_exception_1, null)

                MaterialAlertDialogBuilder(activity).setTitle("事故处理").setView(view)
                    .setPositiveButton("确定") { _, _ ->
                        val adminService = ServiceCreator.create<AdminService>()

                        val body = ExceptionOneHandleRequest(
                            view.findViewById<RadioButton>(R.id.radio_pass).isChecked, list[id].order_id
                        )
                        adminService.handleException1(RequestUtil.generateJson(body))
                            .enqueue(object : retrofit2.Callback<BaseResponse> {
                                override fun onResponse(
                                    call: Call<BaseResponse>, response: Response<BaseResponse>
                                ) {
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
                    }.setNegativeButton("取消", null).show().let {
                        DialogUtil.stylize(it)
                    }
            }

            "异常2" -> {
                val view = activity.layoutInflater.inflate(R.layout.dialog_exception_2, null)
                val etPercent = view.findViewById<EditText>(R.id.et_percent)
                view.findViewById<RadioButton>(R.id.radio_pass).setOnClickListener {
                    etPercent.isEnabled = false
                }
                view.findViewById<RadioButton>(R.id.radio_fail).setOnClickListener {
                    etPercent.isEnabled = true
                }
                MaterialAlertDialogBuilder(activity).setTitle("事故处理").setView(view)
                    .setPositiveButton("确定") { _, _ ->
                        val adminService = ServiceCreator.create<AdminService>()
                        val percentage: Double = try {
                            etPercent.text.toString().toDouble() / 100.0
                        } catch (_: NumberFormatException) {
                            -1.0
                        }
                        if (etPercent.isEnabled && (percentage < 0 || percentage > 1)) {
                            ToastInfo("比例范围不正确，请重新输入")
                            return@setPositiveButton
                        }
                        val body = ExceptionTwoHandleRequest(
                            percentage,
                            view.findViewById<RadioButton>(R.id.radio_pass).isChecked,
                            list[id].order_id
                        )
                        adminService.handleException2(RequestUtil.generateJson(body))
                            .enqueue(object : retrofit2.Callback<BaseResponse> {
                                override fun onResponse(
                                    call: Call<BaseResponse>, response: Response<BaseResponse>
                                ) {
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
                    }.setNegativeButton("取消", null).show().let {
                        DialogUtil.stylize(it)
                    }

            }

            else -> {
                ToastFail("未知异常类型，请升级APP后处理")
            }
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setAdapterList(list2: List<ExceptionOrder>) {
        list = list2
        notifyDataSetChanged()
    }

    fun plusAdapterList(list2: List<ExceptionOrder>) {
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

