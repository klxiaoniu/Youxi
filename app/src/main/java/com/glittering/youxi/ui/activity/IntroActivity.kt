package com.glittering.youxi.ui.activity

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.glittering.youxi.R
import com.glittering.youxi.databinding.ActivityIntroBinding
import com.glittering.youxi.ui.fragment.IntroFragment
import com.zhpan.indicator.IndicatorView
import com.zhpan.indicator.enums.IndicatorSlideMode
import com.zhpan.indicator.enums.IndicatorStyle
private const val NUM_PAGES = 3
class IntroActivity : BaseActivity<ActivityIntroBinding>() {
    private lateinit var viewPager: ViewPager2

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewPager = binding.pager
        val pagerAdapter = ScreenSlidePagerAdapter(this)
        pagerAdapter.createFragment(0)
        pagerAdapter.createFragment(1)
        pagerAdapter.createFragment(2)
        viewPager.adapter = pagerAdapter

        val indicatorView = findViewById<IndicatorView>(R.id.indicator_view)
        indicatorView
            .setSlideMode(IndicatorSlideMode.WORM)
            .setIndicatorStyle(IndicatorStyle.CIRCLE)
            .setSliderColor(getColor(R.color.dark),getColor(R.color.black))
            .setupWithViewPager(viewPager)

        viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                Log.d("pos", position.toString())
                if (position == NUM_PAGES -1) {
                    findViewById<IndicatorView>(R.id.indicator_view).visibility =
                        View.GONE
                    findViewById<Button>(R.id.btn_enter).visibility =
                        View.VISIBLE
                } else {
                    findViewById<IndicatorView>(R.id.indicator_view).visibility =
                        View.VISIBLE
                    findViewById<Button>(R.id.btn_enter).visibility =
                        View.GONE
                }
            }
        })

        binding.btnEnter.setOnClickListener { finish() }//TODO:进入主页？
    }

    private inner class ScreenSlidePagerAdapter(fa: IntroActivity) : FragmentStateAdapter(fa) {
        override fun getItemCount(): Int = NUM_PAGES
        override fun createFragment(position: Int): Fragment = IntroFragment(position)
    }
}