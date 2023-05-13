package com.glittering.youxi.ui.activity

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.glittering.youxi.MyApplication
import com.glittering.youxi.R
import com.glittering.youxi.databinding.ActivityMainBinding
import com.glittering.youxi.ui.adapter.PagerAdapter
import com.glittering.youxi.ui.fragment.dashboard.DashboardFragment
import com.glittering.youxi.ui.fragment.home.HomeFragment
import com.glittering.youxi.ui.fragment.me.MeFragment
import com.glittering.youxi.ui.fragment.notifications.NotificationsFragment
import com.glittering.youxi.utils.BottomNavigationViewUtil
import com.glittering.youxi.utils.TabItem
import com.glittering.youxi.utils.ToastInfo
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : BaseActivity<ActivityMainBinding>() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val navView: BottomNavigationView = binding.navView
        val mainViewPager: ViewPager2 = binding.mainViewPager

//        mainViewPager.isUserInputEnabled = false
//        mainViewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
//            override fun onPageSelected(position: Int) {
//                //设置导航栏选中位置
//                navView.menu.getItem(if (position > 1) position + 1 else position).isChecked = true
////                findViewById<View>(R.id.action_search)?.visibility =
////                    if (position == 0) View.VISIBLE else View.GONE
//            }
//        })

        val fragmentArr = ArrayList<Fragment>()
        fragmentArr.add(HomeFragment.instance)
        fragmentArr.add(DashboardFragment.instance)
        fragmentArr.add(NotificationsFragment.instance)
        fragmentArr.add(MeFragment.instance)
        mainViewPager.adapter = PagerAdapter(this, fragmentArr)

        val tabItems = ArrayList<TabItem>()
        tabItems.add(TabItem("首页", R.drawable.ic_tab_home))
        tabItems.add(TabItem("我要买", R.drawable.ic_tab_wantbuy))
        tabItems.add(TabItem("", R.drawable.ic_wantsell, true))
        tabItems.add(TabItem("消息", R.drawable.ic_tab_notification))
        tabItems.add(TabItem("我的", R.drawable.ic_tab_me))

        val util = BottomNavigationViewUtil.getInstance()
            .init(
                this,
                binding.navView,
                binding.mainViewPager,
                fragmentArr,
                tabItems
            ).addOnlyOperateClick {
//                if (MyApplication.loggedInUser == null) {
                if (false) {
                    ToastInfo("请先登录")
                    val intent = Intent(this, LoginActivity::class.java)
                    startActivity(intent)
                } else {
                    val intent = Intent(this, NewOrderActivity::class.java)
                    startActivity(intent)
                }
            }

//        binding.navView.postDelayed({
//            util.addTabItemRedCirclePoint(3, 58)
//            util.addTabItemRedCirclePoint(0, 21)
//
//        }, 5 * 1000)

//        navView.setOnItemSelectedListener {
//            when (it.itemId) {
//                R.id.navigation_home -> {
//                    mainViewPager.setCurrentItem(0, false)
//                }
//                R.id.navigation_dashboard -> {
//                    mainViewPager.setCurrentItem(1, false)
//                }
//                R.id.navigation_notifications -> {
//                    mainViewPager.setCurrentItem(2, false)
//                }
//                R.id.navigation_me -> {
//                    mainViewPager.setCurrentItem(3, false)
//                    MeFragment.instance.updateUserInfo()
//                }
//                R.id.navigation_sell -> {
//                    //TODO: Launch sell activity
//                    ToastSuccess("Debug")
//                    val intent = Intent(this, DebugActivity::class.java)
//                    startActivity(intent)
//                }
//            }
//            Log.d(
//                "onItemSelected",
//                it.itemId.toString() + " " + mainViewPager.currentItem.toString()
//            )
//
//            true
//        }
    }
}