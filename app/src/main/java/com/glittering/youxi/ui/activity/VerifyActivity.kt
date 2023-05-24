package com.glittering.youxi.ui.activity

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.RadioButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.glittering.youxi.R
import com.glittering.youxi.data.OrderService
import com.glittering.youxi.data.ServiceCreator
import com.glittering.youxi.data.VerifyResponse
import com.glittering.youxi.data.VerifyingOrder
import com.glittering.youxi.data.VerifyingOrderResponse
import com.glittering.youxi.databinding.ActivityVerifyBinding
import com.glittering.youxi.utils.DialogUtil
import com.glittering.youxi.utils.ToastFail
import com.glittering.youxi.utils.ToastInfo
import com.glittering.youxi.utils.ToastSuccess
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.gson.Gson
import com.gyf.immersionbar.ktx.fitsTitleBar
import okhttp3.FormBody
import okhttp3.MediaType
import retrofit2.Call
import retrofit2.Response

class VerifyActivity : BaseActivity<ActivityVerifyBinding>() {
    override val fitSystemWindows: Boolean
        get() = false
    lateinit var adapter: VerifyingOrderAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding.toolbar.let {
            it.setNavigationIcon(R.drawable.ic_back)
            it.setNavigationOnClickListener { finish() }
            //it.inflateMenu(R.menu.chat_menu)
            fitsTitleBar(it)
        }

        getData(1)
        //verify(1)
    }


    private fun getData(page: Int) {
        val orderService = ServiceCreator.create<OrderService>()

        data class BodyData(val page: Int)

        val body = FormBody.create(
            MediaType.parse("application/json; charset=utf-8"),
            Gson().toJson(BodyData(page))
        )
        orderService.getVerifyingOrder(body)
            .enqueue(object : retrofit2.Callback<VerifyingOrderResponse> {
                override fun onResponse(
                    call: Call<VerifyingOrderResponse>,
                    response: Response<VerifyingOrderResponse>
                ) {
                    if (response.body() != null) {
                        if (response.body()!!.code == 200) {
                            val list = response.body()!!.data
                            adapter = VerifyingOrderAdapter(list)
                            binding.rv.adapter = adapter
                            adapter.setOnFootViewClickListener { getData(page + 1) }
                            //adapter.setOnFootViewAttachedToWindowListener { getData(page + 1) }
                        }
                    }
                }

                override fun onFailure(call: Call<VerifyingOrderResponse>, t: Throwable) {
                    ToastFail(getString(R.string.toast_response_error))
                }
            }
            )
    }

    inner class VerifyingOrderAdapter(var list: List<VerifyingOrder>) :
        RecyclerView.Adapter<RecyclerView.ViewHolder>() {
        val TYPE_FOOTVIEW: Int = 1 //item类型：footview
        val TYPE_ITEMVIEW: Int = 2 //item类型：itemview
        var typeItem = TYPE_ITEMVIEW

        inner class ItemViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            val tv_info = view.findViewById<TextView>(R.id.tv_info)
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
                holder.tv_info.text = list[position].toString()
                holder.itemView.setOnClickListener {
                    val position = holder.adapterPosition
                    val fund = list[position]
//                    val intent = Intent(it.context, FundDetailActivity::class.java)
//                    intent.putExtra("fund_id", fund.id)
//                    it.context.startActivity(intent)
                }
                holder.btn_verify.setOnClickListener {
                    verify(list[position].order_id)
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

        fun setAdapterList(list2: List<VerifyingOrder>) {
            list = list2
            notifyDataSetChanged()
        }

        fun plusAdapterList(list2: List<VerifyingOrder>) {
            list = list.plus(list2)
            notifyDataSetChanged()
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


    private fun verify(id: Int) {
        val view = layoutInflater.inflate(R.layout.dialog_verify, null)

        val dialog = MaterialAlertDialogBuilder(this)
            .setTitle("审核")
            .setView(view)
            .setPositiveButton("确定") { dialog, which ->
                val orderService = ServiceCreator.create<OrderService>()

                data class BodyData(val order_id: Int, val verifying: String)

                val body = FormBody.create(
                    MediaType.parse("application/json; charset=utf-8"),
                    Gson().toJson(
                        BodyData(
                            id,
                            if (view.findViewById<RadioButton>(R.id.radio_pass).isChecked) "True" else "False"
                        )
                    )
                )
                orderService.verify(body)
                    .enqueue(object : retrofit2.Callback<VerifyResponse> {
                        override fun onResponse(
                            call: Call<VerifyResponse>,
                            response: Response<VerifyResponse>
                        ) {
                            if (response.body() != null) {
                                if (response.body()!!.code == 200) {
                                    ToastSuccess(response.body()!!.message)
                                } else {
                                    ToastFail(response.body()!!.message)
                                }
                            }
                        }

                        override fun onFailure(call: Call<VerifyResponse>, t: Throwable) {
                            ToastFail(getString(R.string.toast_response_error))
                        }
                    }
                    )
            }
            .setNegativeButton("取消", null)
            .show()
        DialogUtil.stylize(dialog)
    }

}