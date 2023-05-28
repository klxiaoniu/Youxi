package com.glittering.youxi.ui.activity

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.glittering.youxi.R
import com.glittering.youxi.databinding.ActivityMainBinding
import com.glittering.youxi.ui.adapter.PagerAdapter
import com.glittering.youxi.ui.fragment.buy.BuyFragment
import com.glittering.youxi.ui.fragment.home.HomeFragment
import com.glittering.youxi.ui.fragment.me.MeFragment
import com.glittering.youxi.ui.fragment.notifications.NotificationsFragment
import com.glittering.youxi.utils.DarkUtil.Companion.addMaskIfDark
import com.glittering.youxi.utils.DarkUtil.Companion.isFollowSystem
import com.glittering.youxi.utils.DarkUtil.Companion.isForceDark
import com.glittering.youxi.utils.ToastInfo
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : BaseActivity<ActivityMainBinding>() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val navView: BottomNavigationView = binding.navView
        val mainViewPager: ViewPager2 = binding.mainViewPager

        mainViewPager.isUserInputEnabled = false

        val fragmentArr = ArrayList<Fragment>()
        fragmentArr.add(HomeFragment.instance)
        fragmentArr.add(BuyFragment.instance)
        fragmentArr.add(NotificationsFragment.instance)
        fragmentArr.add(MeFragment.instance)
        mainViewPager.adapter = PagerAdapter(this, fragmentArr)

        navView.itemIconTintList = null
        navView.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.navigation_home -> {
                    mainViewPager.setCurrentItem(0, false)
                }

                R.id.navigation_wantbuy -> {
                    mainViewPager.setCurrentItem(1, false)
                }

                R.id.navigation_notification -> {
                    mainViewPager.setCurrentItem(2, false)
                }

                R.id.navigation_me -> {
                    mainViewPager.setCurrentItem(3, false)
                }
            }
            true
        }

        if (!isFollowSystem(this)) AppCompatDelegate.setDefaultNightMode(
            if (isForceDark(this)) AppCompatDelegate.MODE_NIGHT_YES else AppCompatDelegate.MODE_NIGHT_NO
        )   //TODO: fix crash

        addMaskIfDark(binding.navView)

        binding.fab.imageTintList = null
        binding.fab.setOnClickListener {
//            if (loggedInUser == null) {
            if (false) {
                ToastInfo("请先登录")
                startActivity(Intent(this, LoginActivity::class.java))
            } else {
                startActivity(Intent(this, NewOrderActivity::class.java))
            }
        }
    }
}