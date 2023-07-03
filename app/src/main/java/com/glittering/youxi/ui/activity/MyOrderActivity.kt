package com.glittering.youxi.ui.activity

import android.os.Bundle
import com.glittering.youxi.R
import com.glittering.youxi.data.bean.MyOrderData
import com.glittering.youxi.data.response.BaseDataResponse
import com.glittering.youxi.data.service.ServiceCreator
import com.glittering.youxi.data.service.UserService
import com.glittering.youxi.databinding.ActivityMyOrderBinding
import com.glittering.youxi.ui.adapter.MyOrderAdapter
import com.glittering.youxi.utils.ToastFail
import com.gyf.immersionbar.ktx.fitsTitleBar
import retrofit2.Call

class MyOrderActivity : BaseActivity<ActivityMyOrderBinding>() {
    override val fitSystemWindows: Boolean
        get() = false

    var adapter: MyOrderAdapter? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val type = intent.getStringExtra("type")
        if (type == null) {
            ToastFail("参数错误")
            finish()
            return
        }
        binding.toolbar.let {
            it.setNavigationIcon(R.drawable.ic_back)
            it.setNavigationOnClickListener { finish() }
//            it.inflateMenu(R.menu.myorder_menu)
            fitsTitleBar(it)
        }
        binding.title.text = when (type) {
            "buying" -> "我买的"
            "selling" -> "我卖的"
            else -> "我的订单"
        }


        getData(type, 1)
    }

    private fun getData(type: String, page: Int) {
        val userService = ServiceCreator.create<UserService>()
        userService.getMyOrder(type, page).enqueue(object : retrofit2.Callback<BaseDataResponse<List<MyOrderData>>> {
            override fun onResponse(
                call: Call<BaseDataResponse<List<MyOrderData>>>,
                response: retrofit2.Response<BaseDataResponse<List<MyOrderData>>>
            ) {
                if (response.body() != null) {
                    if (response.body()!!.code == 200) {
                        val list = response.body()!!.data
                        if (adapter == null) {
                            adapter = MyOrderAdapter(list as MutableList<MyOrderData>, type, this@MyOrderActivity)
                            binding.rv.adapter = adapter
                            val layoutManager =
                                androidx.recyclerview.widget.LinearLayoutManager(applicationContext)
                            layoutManager.orientation =
                                androidx.recyclerview.widget.LinearLayoutManager.VERTICAL
                            binding.rv.layoutManager = layoutManager
                        } else {
                            adapter!!.plusAdapterList(list as MutableList<MyOrderData>)
                        }
                        adapter!!.setOnFootViewAttachedToWindowListener { getData(type, page + 1) }
                        adapter!!.setOnFootViewClickListener { getData(type, page + 1) }

                    } else {
                        ToastFail(response.body()?.message.toString())
                        adapter?.setOnFootViewAttachedToWindowListener { getData(type, page) }
                        adapter?.setOnFootViewClickListener { getData(type, page) }
                    }
                }
            }

            override fun onFailure(call: Call<BaseDataResponse<List<MyOrderData>>>, t: Throwable) {
                t.printStackTrace()
                adapter?.setOnFootViewAttachedToWindowListener { }
                adapter?.setOnFootViewClickListener { getData(type, page) }
            }
        })
    }
}