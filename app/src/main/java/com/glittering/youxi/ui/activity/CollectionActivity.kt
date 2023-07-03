package com.glittering.youxi.ui.activity

import android.os.Bundle
import com.glittering.youxi.R
import com.glittering.youxi.data.bean.CollectionData
import com.glittering.youxi.data.response.BaseDataResponse
import com.glittering.youxi.data.service.ServiceCreator
import com.glittering.youxi.data.service.UserService
import com.glittering.youxi.databinding.ActivityCollectionBinding
import com.glittering.youxi.ui.adapter.CollectionAdapter
import com.glittering.youxi.utils.ToastFail
import com.gyf.immersionbar.ktx.fitsTitleBar

class CollectionActivity : BaseActivity<ActivityCollectionBinding>() {
    override val fitSystemWindows: Boolean
        get() = false
    var adapter: CollectionAdapter? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding.toolbar.let {
            it.setNavigationIcon(R.drawable.ic_back)
            it.setNavigationOnClickListener { finish() }
            fitsTitleBar(it)
        }

        getData(1)
    }

    private fun getData(i: Int) {
        val userService = ServiceCreator.create<UserService>()
        userService.getCollection(i).enqueue(object : retrofit2.Callback<BaseDataResponse<List<CollectionData>>> {
            override fun onResponse(
                call: retrofit2.Call<BaseDataResponse<List<CollectionData>>>,
                response: retrofit2.Response<BaseDataResponse<List<CollectionData>>>
            ) {
                if (response.body() != null) {
                    if (response.body()!!.code == 200) {
                        val list = response.body()!!.data
                        if (adapter == null) {
                            adapter = CollectionAdapter(list)
                            binding.rv.adapter = adapter
                            val layoutManager =
                                androidx.recyclerview.widget.LinearLayoutManager(applicationContext)
                            layoutManager.orientation =
                                androidx.recyclerview.widget.LinearLayoutManager.VERTICAL
                            binding.rv.layoutManager = layoutManager
                        } else {
                            adapter!!.plusAdapterList(list)
                        }
                        adapter!!.setOnFootViewAttachedToWindowListener { getData(i + 1) }
                        adapter!!.setOnFootViewClickListener { getData(i + 1) }

                    } else {
                        ToastFail(response.body()?.message.toString())
                        adapter?.setOnFootViewAttachedToWindowListener { getData(i) }
                        adapter?.setOnFootViewClickListener { getData(i) }
                    }


                }
            }

            override fun onFailure(call: retrofit2.Call<BaseDataResponse<List<CollectionData>>>, t: Throwable) {
                t.printStackTrace()
                ToastFail(getString(R.string.toast_response_error))
                adapter?.setOnFootViewAttachedToWindowListener { }
                adapter?.setOnFootViewClickListener { getData(i) }
            }
        })

    }
}