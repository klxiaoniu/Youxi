package com.glittering.youxi.ui.activity

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import androidx.viewpager2.widget.ViewPager2
import com.glittering.youxi.R
import com.glittering.youxi.databinding.ActivityMainBinding
import com.glittering.youxi.ui.adapter.PagerAdapter
import com.glittering.youxi.ui.fragment.dashboard.DashboardFragment
import com.glittering.youxi.ui.fragment.home.HomeFragment
import com.glittering.youxi.ui.fragment.notifications.NotificationsFragment
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : BaseActivity<ActivityMainBinding>() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val navView: BottomNavigationView = binding.navView
        val mainViewPager: ViewPager2 = binding.mainViewPager

        mainViewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                //设置导航栏选中位置
                navView.menu.getItem(position).isChecked = true
//                findViewById<View>(R.id.action_search)?.visibility =
//                    if (position == 0) View.VISIBLE else View.GONE
            }
        })

        val fragmentArr = ArrayList<Fragment>()
        fragmentArr.add(HomeFragment.instance)
        fragmentArr.add(DashboardFragment.instance)
        fragmentArr.add(NotificationsFragment.instance)
        mainViewPager.adapter = PagerAdapter(this, fragmentArr)

        navView.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.navigation_home -> {
                    mainViewPager.currentItem = 0
                }
                R.id.navigation_dashboard -> {
                    mainViewPager.currentItem = 1
                }
                R.id.navigation_notifications -> {
                    mainViewPager.currentItem = 2
                }
            }
            true
        }
    }
}