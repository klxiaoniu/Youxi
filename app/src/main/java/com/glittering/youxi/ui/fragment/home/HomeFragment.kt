package com.glittering.youxi.ui.fragment.home

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.glittering.youxi.R
import com.glittering.youxi.data.BannerBean
import com.glittering.youxi.data.BannerResponse
import com.glittering.youxi.data.MainpageService
import com.glittering.youxi.data.ServiceCreator
import com.glittering.youxi.databinding.FragmentHomeBinding
import com.glittering.youxi.ui.activity.OrderDetailActivity
import com.glittering.youxi.utils.ToastFail
import com.glittering.youxi.utils.applicationContext
import com.zhpan.bannerview.BannerViewPager
import com.zhpan.bannerview.BaseBannerAdapter
import com.zhpan.bannerview.BaseViewHolder
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private lateinit var mViewPager: BannerViewPager<BannerBean>

    companion object {
        val instance: HomeFragment by lazy(mode = LazyThreadSafetyMode.SYNCHRONIZED) {
            HomeFragment()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val homeViewModel =
            ViewModelProvider(this).get(HomeViewModel::class.java)

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root
        setupViewPager()

        return root
    }

    private fun setupViewPager() {
        mViewPager = binding.bannerView as BannerViewPager<BannerBean>
        mViewPager.apply {
            adapter = SimpleAdapter()
            registerLifecycleObserver(lifecycle)
        }.create()

        val mainpageService = ServiceCreator.create<MainpageService>()

        mainpageService.getBanner().enqueue(object : Callback<BannerResponse> {
            override fun onResponse(
                call: Call<BannerResponse>,
                response: Response<BannerResponse>
            ) {
                Log.d("banner", response.body().toString())
                val code = response.body()?.code
                if (code == 200) {
                    val data = response.body()?.data
                    mViewPager.refreshData(data)
                } else {
                    ToastFail(response.body()?.message.toString())
                }
            }

            override fun onFailure(call: Call<BannerResponse>, t: Throwable) {
                t.printStackTrace()
                ToastFail(applicationContext.getString(R.string.toast_response_error))
            }
        })

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    inner class SimpleAdapter : BaseBannerAdapter<BannerBean>() {

        override fun bindData(
            holder: BaseViewHolder<BannerBean>,
            data: BannerBean?,
            position: Int,
            pageSize: Int
        ) {
            //holder.setImageResource(R.id.banner_image, data!!.order_picture)
            val layout = holder.findViewById<View>(R.id.banner_layout)
            val imageView = holder.findViewById<ImageView>(R.id.banner_image)
            val textView = holder.findViewById<TextView>(R.id.banner_text)
            val options = RequestOptions()
                //.placeholder(R.drawable.loading)
                .error(R.drawable.error)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
            Glide.with(this@HomeFragment)
                .load(data!!.order_picture)
                .apply(options)
                .into(imageView)
            textView.text = data.order_title
//            if (data.order_address != "") {
//                layout.setOnClickListener {
//                    if (URLUtil.isValidUrl(data.order_address)) {
//                        val uri = Uri.parse(data.order_address)
//                        val intent = Intent(Intent.ACTION_VIEW, uri)
//                        startActivity(intent)
//                    } else {
//                        val intent = Intent(requireContext(), OrderDetailActivity::class.java)
//                        intent.putExtra("order_id", data.order_address)
//                        startActivity(intent)
//                    }
//                }
//            }
            layout.setOnClickListener {
                val intent = Intent(requireContext(), OrderDetailActivity::class.java)
                intent.putExtra("order_id", data.order_id)
                startActivity(intent)
            }
        }

        override fun getLayoutId(viewType: Int): Int {
            return R.layout.item_banner
        }
    }

}