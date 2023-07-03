package com.glittering.youxi.ui.fragment.home

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.glittering.youxi.R
import com.glittering.youxi.data.bean.Banner
import com.glittering.youxi.data.bean.Jingpin
import com.glittering.youxi.data.response.BaseDataResponse
import com.glittering.youxi.data.service.MainpageService
import com.glittering.youxi.data.service.ServiceCreator
import com.glittering.youxi.databinding.FragmentHomeBinding
import com.glittering.youxi.ui.activity.OrderDetailActivity
import com.glittering.youxi.ui.adapter.JingpinAdapter
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
    private lateinit var mViewPager: BannerViewPager<Banner>
    private lateinit var jingpinAdapter: JingpinAdapter

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

        getJingpinData()

        return root
    }

    private fun getJingpinData() {
        val list = listOf(
            Jingpin("https://gimg0.baidu.com/gimg/src=https%3A%2F%2Fappdown.baidu.com%2Fimg%2F0%2F512_512%2F244d263299e954004a6475b171586157.png&app=2000&size=f200,200&n=0&g=0n&q=85&fmt=jpeg?sec=0&t=908d62726408cacaef38cda9b768e956", "王者荣耀"),
            Jingpin("https://gimg0.baidu.com/gimg/src=https%3A%2F%2Fgp-dev.cdn.bcebos.com%2Fgp-dev%2Fupload%2Ffile%2Fsource%2Fd0ad98db869bb3f8e91f80c7d5365960.png&app=2000&size=f200,200&n=0&g=0n&q=85&fmt=jpeg?sec=0&t=d5678f60257cfae32225e954ae608909", "原神"),
            Jingpin("https://gimg0.baidu.com/gimg/src=https%3A%2F%2Fgp-open-platform.cdn.bcebos.com%2F204306211106%2Fe6d7b703c30f1c8c1621a43ecd310710%2Fgp-open-platform%2Fupload%2Ffile%2Fimg%2F0e77a7ed283e7eeeb8747e17905ed338.png&app=2000&size=f200,200&n=0&g=0n&q=85&fmt=jpeg?sec=0&t=d2385260b70e8b50cf65c106e02e656a", "我的世界"),
            Jingpin("https://gimg0.baidu.com/gimg/src=https%3A%2F%2Fgdown.baidu.com%2Fappcenter%2Fsource%2F1396538632%2F49ae6bef5c5f10bf743e571cb939d876%2Fres%2Fdrawable-xhdpi-v4%2Ficon.png&app=2000&size=f200,200&n=0&g=0n&q=85&fmt=jpeg?sec=0&t=afe0cd5a05c22c3315ebeba9dd5ed82f", "和平精英"),
            Jingpin("https://gimg0.baidu.com/gimg/src=https%3A%2F%2Fgp-dev.cdn.bcebos.com%2Fgp-dev%2Fupload%2Ffile%2Fsource%2F918b16307b92de76ddabea71dba64627.png&app=2000&size=f200,200&n=0&g=0n&q=85&fmt=jpeg?sec=0&t=7d4ac4ced3680fef1a17f186fffd028a", "阴阳师"),
            Jingpin("https://gimg0.baidu.com/gimg/src=https%3A%2F%2Fgp-dev.cdn.bcebos.com%2Fgp-dev%2Fupload%2Ffile%2Fsource%2F977c7dda5ae727a73f5611d2e124c9cf.png&app=2000&size=f200,200&n=0&g=0n&q=85&fmt=jpeg?sec=0&t=ef77aa2f171c9580fd8788f5bb48a97e", "三国杀"),
            Jingpin("https://gimg0.baidu.com/gimg/src=https%3A%2F%2Fgdown.baidu.com%2Fappcenter%2Fsource%2F1396538632%2F700bf96b755bd55d4077f18816c95cc2%2Fres%2Fdrawable-mdpi%2Ficon.png&app=2000&size=f200,200&n=0&g=0n&q=85&fmt=jpeg?sec=0&t=d4f1e23abb2f12f5f29eaac59a5d68d1", "光遇"),
            Jingpin("https://gimg0.baidu.com/gimg/src=https%3A%2F%2Fgp-open-platform.cdn.bcebos.com%2F204306301712%2Fcf7f7b655d4eed513af237441780ba06%2Fgp-open-platform%2Fupload%2Ffile%2Fimg%2F72ce4098fb66104e7dc10524627bd01d.png&app=2000&size=f200,200&n=0&g=0n&q=85&fmt=jpeg?sec=0&t=bba5f021b6bf8c948d0e17111b562e20","第五人格")
        )
        val layoutManager = StaggeredGridLayoutManager(4, StaggeredGridLayoutManager.VERTICAL)
        layoutManager.gapStrategy = StaggeredGridLayoutManager.GAP_HANDLING_MOVE_ITEMS_BETWEEN_SPANS
        binding.rvJingpin.layoutManager = layoutManager
        jingpinAdapter = JingpinAdapter(list)
        binding.rvJingpin.adapter = jingpinAdapter
    }

    private fun setupViewPager() {
        mViewPager = binding.bannerView as BannerViewPager<Banner>
        mViewPager.apply {
            adapter = SimpleAdapter()
            registerLifecycleObserver(lifecycle)
        }.create()

        val mainpageService = ServiceCreator.create<MainpageService>()

        mainpageService.getBanner().enqueue(object : Callback<BaseDataResponse<List<Banner>>> {
            override fun onResponse(
                call: Call<BaseDataResponse<List<Banner>>>,
                response: Response<BaseDataResponse<List<Banner>>>
            ) {
                val code = response.body()?.code
                if (code == 200) {
                    val data = response.body()?.data
                    mViewPager.refreshData(data)
                } else {
                    ToastFail(response.body()?.message.toString())
                }
            }

            override fun onFailure(call: Call<BaseDataResponse<List<Banner>>>, t: Throwable) {
                t.printStackTrace()
                ToastFail(applicationContext.getString(R.string.toast_response_error))
            }
        })

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    inner class SimpleAdapter : BaseBannerAdapter<Banner>() {

        override fun bindData(
            holder: BaseViewHolder<Banner>,
            data: Banner?,
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