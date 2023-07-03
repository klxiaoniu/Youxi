package com.glittering.youxi.ui.activity

import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.glittering.youxi.R
import com.glittering.youxi.data.bean.ReportUserData
import com.glittering.youxi.data.response.BaseDataResponse
import com.glittering.youxi.data.service.AdminService
import com.glittering.youxi.data.service.ServiceCreator
import com.glittering.youxi.databinding.ActivityReportHandleBinding
import com.glittering.youxi.ui.adapter.ReportHandleAdapter
import com.glittering.youxi.utils.ToastFail
import com.gyf.immersionbar.ktx.fitsTitleBar
import retrofit2.Call
import retrofit2.Response

class ReportHandleActivity : BaseActivity<ActivityReportHandleBinding>() {
    override val fitSystemWindows: Boolean
        get() = false
    lateinit var adapter: ReportHandleAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        binding.toolbar.let {
            it.setNavigationIcon(R.drawable.ic_back)
            it.setNavigationOnClickListener { finish() }
            fitsTitleBar(it)
        }

        val layoutManager = LinearLayoutManager(applicationContext)
        layoutManager.orientation = LinearLayoutManager.VERTICAL
        binding.rv.layoutManager = layoutManager
        adapter = ReportHandleAdapter(emptyList(), this)
        binding.rv.adapter = adapter
        getData(1)
    }

    private fun getData(page: Int) {
        val adminService = ServiceCreator.create<AdminService>()

        adminService.getReportList(page)
            .enqueue(object : retrofit2.Callback<BaseDataResponse<List<ReportUserData>>> {
                override fun onResponse(
                    call: Call<BaseDataResponse<List<ReportUserData>>>,
                    response: Response<BaseDataResponse<List<ReportUserData>>>
                ) {
                    if (response.body() != null) {
                        if (response.body()!!.code == 200) {
                            val list = response.body()!!.data
                            adapter.plusAdapterList(list)
                            adapter.setOnFootViewClickListener { getData(page + 1) }
                            adapter.setOnFootViewAttachedToWindowListener { getData(page + 1) }
                        } else {
                            ToastFail(response.body()!!.message)
                            adapter.setOnFootViewClickListener { getData(page) }
                            adapter.setOnFootViewAttachedToWindowListener { getData(page) }
                        }
                    } else {
                        ToastFail(getString(R.string.toast_response_error))
                        adapter.setOnFootViewClickListener { getData(page) }
                        adapter.setOnFootViewAttachedToWindowListener { getData(page) }
                    }
                }

                override fun onFailure(
                    call: Call<BaseDataResponse<List<ReportUserData>>>,
                    t: Throwable
                ) {
                    ToastFail(getString(R.string.toast_response_error))
                    adapter.setOnFootViewClickListener { getData(page) }
                    adapter.setOnFootViewAttachedToWindowListener { getData(page) }
                }
            }
            )
    }
}