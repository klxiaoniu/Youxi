package com.glittering.youxi.ui.activity

import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.glittering.youxi.R
import com.glittering.youxi.data.BaseDataResponse
import com.glittering.youxi.data.OrderService
import com.glittering.youxi.data.ServiceCreator
import com.glittering.youxi.data.VerifyingOrder
import com.glittering.youxi.databinding.ActivityVerifyBinding
import com.glittering.youxi.ui.adapter.VerifyingOrderAdapter
import com.glittering.youxi.utils.ToastFail
import com.gyf.immersionbar.ktx.fitsTitleBar
import retrofit2.Call
import retrofit2.Response

class VerifyActivity : BaseActivity<ActivityVerifyBinding>() {
    override val fitSystemWindows: Boolean
        get() = false
    var adapter: VerifyingOrderAdapter? = null

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

        orderService.getVerifyingOrder(page)
            .enqueue(object : retrofit2.Callback<BaseDataResponse<List<VerifyingOrder>>> {
                override fun onResponse(
                    call: Call<BaseDataResponse<List<VerifyingOrder>>>,
                    response: Response<BaseDataResponse<List<VerifyingOrder>>>
                ) {
                    if (response.body() != null && response.body()!!.code == 200) {
                        val list = response.body()!!.data
                        if (adapter == null) {
                            val layoutManager = LinearLayoutManager(applicationContext)
                            layoutManager.orientation = LinearLayoutManager.VERTICAL
                            binding.rv.layoutManager = layoutManager
                            adapter = VerifyingOrderAdapter(list, this@VerifyActivity)
                            binding.rv.adapter = adapter
                        } else {
                            adapter!!.plusAdapterList(list)
                        }
                        adapter!!.setOnFootViewClickListener { getData(page + 1) }
                        //adapter.setOnFootViewAttachedToWindowListener { getData(page + 1) }
                    } else {
                        ToastFail(getString(R.string.toast_response_error))
                    }
                }

                override fun onFailure(call: Call<BaseDataResponse<List<VerifyingOrder>>>, t: Throwable) {
                    ToastFail(getString(R.string.toast_response_error))
                }
            }
            )
    }


}