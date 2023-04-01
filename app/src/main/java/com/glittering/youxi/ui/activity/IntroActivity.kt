package com.glittering.youxi.ui.activity

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import androidx.viewpager2.widget.ViewPager2
import com.glittering.youxi.R
import com.glittering.youxi.databinding.ActivityIntroBinding
import com.glittering.youxi.utils.ToastSuccess
import com.glittering.youxi.utils.isFirstEnter
import com.glittering.youxi.utils.setFirstEnter
import com.zhpan.bannerview.BannerViewPager
import com.zhpan.bannerview.BaseBannerAdapter
import com.zhpan.bannerview.BaseViewHolder
import com.zhpan.indicator.enums.IndicatorSlideMode
import com.zhpan.indicator.enums.IndicatorStyle


class IntroActivity : BaseActivity<ActivityIntroBinding>() {
    private lateinit var mViewPager: BannerViewPager<Int>
    private var size: Int = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (!isFirstEnter()) {
            ToastSuccess("已经进入过了")
//            finish()
        }

        setupViewPager()

        binding.btnEnter.setOnClickListener {
            setFirstEnter(false)
            finish()
        }//TODO:进入主页？
        binding.btnEnter.setOnLongClickListener {
            setFirstEnter(true)
            ToastSuccess("清除进入记录")
            finish()
            true
        }


    }

    private fun setupViewPager() {
        mViewPager = binding.bannerView as BannerViewPager<Int>
        val data = listOf(
            R.drawable.close,
            R.drawable.error,
            R.drawable.loading,
            R.drawable.ic_success   //TODO:这里是测试数据，需要替换
        )
        size = data.size
        mViewPager.apply {
            adapter = SimpleAdapter()
        }.create(data)
        mViewPager.setIndicatorSlideMode(IndicatorSlideMode.WORM)
            .setIndicatorStyle(IndicatorStyle.CIRCLE)
            .setIndicatorSliderColor(getColor(R.color.dark), getColor(R.color.black))
        mViewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                if (position == size - 1) {
                    mViewPager.setIndicatorVisibility(View.GONE)    //不可用
                    findViewById<Button>(R.id.btn_enter).visibility =
                        View.VISIBLE
                } else {
                    mViewPager.setIndicatorVisibility(View.VISIBLE)
                    findViewById<Button>(R.id.btn_enter).visibility =
                        View.GONE
                }
            }
        })
      }

    inner class SimpleAdapter : BaseBannerAdapter<Int>() {

        override fun bindData(
            holder: BaseViewHolder<Int>,
            data: Int?,
            position: Int,
            pageSize: Int
        ) {
            val imageView = holder.findViewById<ImageView>(R.id.imageView)
            if (data != null) {
                imageView.setImageResource(data)
            }
        }

        override fun getLayoutId(viewType: Int): Int {
            return R.layout.item_intro
        }
    }
}