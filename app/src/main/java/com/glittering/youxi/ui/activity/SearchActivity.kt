package com.glittering.youxi.ui.activity

import android.os.Bundle
import android.util.Log
import android.view.inputmethod.EditorInfo
import androidx.recyclerview.widget.LinearLayoutManager
import com.glittering.youxi.R
import com.glittering.youxi.data.OrderService
import com.glittering.youxi.data.SearchResponse
import com.glittering.youxi.data.SearchResultAdapter
import com.glittering.youxi.data.ServiceCreator
import com.glittering.youxi.databinding.ActivitySearchBinding
import com.glittering.youxi.utils.DarkUtil.Companion.reverseColorIfDark
import com.glittering.youxi.utils.ToastFail
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SearchActivity : BaseActivity<ActivitySearchBinding>() {

    var adapter: SearchResultAdapter? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        var key = intent.getStringExtra("key")
        if (key == null) {
            finish()
            return
        }
        reverseColorIfDark(listOf(binding.back))
        binding.back.setOnClickListener {
            finish()
        }

        binding.editTextSearch.setText(key)
        binding.editTextSearch.setOnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                if (adapter != null) {
                    adapter!!.setAdapterList(emptyList())
                }
                key = binding.editTextSearch.text.toString()
                getData(key!!, 1)
            }
            true
        }
        getData(key!!, 1)

    }

    private fun getData(key: String, i: Int) {
        val orderService = ServiceCreator.create<OrderService>()

        orderService.search(key, i).enqueue(object : Callback<SearchResponse> {
            override fun onResponse(
                call: Call<SearchResponse>,
                response: Response<SearchResponse>
            ) {
                val code = response.body()?.code
                if (code == 200) {
                    val data = response.body()?.data!!
                    Log.d("SearchActivity", data.toString())
                    if (adapter == null) {
                        adapter = SearchResultAdapter(data)
                        val layoutManager = LinearLayoutManager(applicationContext)
                        layoutManager.orientation = LinearLayoutManager.VERTICAL
                        binding.rv.layoutManager = layoutManager
                        binding.rv.adapter = adapter
                    } else {
                        adapter!!.plusAdapterList(data)
                    }

                    adapter!!.setOnFootViewAttachedToWindowListener { getData(key, i + 1) }
                    adapter!!.setOnFootViewClickListener { getData(key, i + 1) }
                } else {
                    ToastFail(response.body()?.message.toString())
                    adapter?.setOnFootViewAttachedToWindowListener { getData(key, i) }
                    adapter?.setOnFootViewClickListener { getData(key, i) }
                }
            }

            override fun onFailure(call: Call<SearchResponse>, t: Throwable) {
                ToastFail(getString(R.string.toast_response_error))
                adapter?.setOnFootViewAttachedToWindowListener { }
                adapter?.setOnFootViewClickListener { getData(key, i) }
            }
        })
    }
}