package com.glittering.youxi.utils

import android.content.res.ColorStateList
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.ColorInt
import androidx.annotation.DrawableRes
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.glittering.youxi.R
import com.google.android.material.bottomnavigation.BottomNavigationItemView
import com.google.android.material.bottomnavigation.BottomNavigationMenuView
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationBarView

/**
 * 一个可以将BottomNavigationView和ViewPager2直接绑定的工具类
 * 可以添加小红点，修改数字
 * 可以添加操作项
 */
class BottomNavigationViewUtil private constructor() {
    companion object {
        @Volatile
        private var instance: BottomNavigationViewUtil? = null

        fun getInstance(): BottomNavigationViewUtil {
            return instance ?: synchronized(this) {
                instance ?: BottomNavigationViewUtil()
            }
        }
    }

    /**
     * 用于保存索引位置的小红点
     */
    private val map = mutableMapOf<Int, TextView>()

    /**
     * 上下文
     */
    private lateinit var fragmentActivity: FragmentActivity

    /**
     *
     */
    private var bottomNavigationView: BottomNavigationView? = null

    /**
     *
     */
    private var viewPager2: ViewPager2? = null

    /**
     * 展示的fragment的列表
     */
    private var fragments = mutableListOf<Fragment>()

    /**
     * 底部按钮列表，可以包含只有操作的按钮
     */
    private var tabItems = mutableListOf<TabItem>()

    /**
     * 选中时按钮显示的颜色值
     */
    private var selectedColor: Int = -1

    /**
     * 没有选中时按钮显示的颜色值
     */
    private var unselectedColor: Int = -1

    /**
     * 底部按钮的菜单按钮，通过该View可以添加小红点，添加操作按钮
     */
    private var menuView: BottomNavigationMenuView? = null

    /**
     * 当前选中的索引
     */
    private var currentSelectedIndex = 0

    /**
     * 操作按钮索引
     */
    private var onlyOperateIndex = -1

    /**
     * 操作按钮图标
     */
    private var onlyOperateIcon = -1

    /**
     * 初始化
     * @param fragment Fragment
     * @param bottomNavigationView BottomNavigationView
     * @param viewPager2 ViewPager2
     * @param fragments MutableList<Fragment>
     * @param tabItems MutableList<TabItem>
     * @param selectedColor Int
     * @param unselectedColor Int
     * @return BottomNavigationViewUtil
     */
    fun init(
        fragment: FragmentActivity,
        bottomNavigationView: BottomNavigationView,
        viewPager2: ViewPager2,
        fragments: MutableList<Fragment>,
        tabItems: MutableList<TabItem>//,
//        @ColorInt selectedColor: Int,
//        @ColorInt unselectedColor: Int
    ): BottomNavigationViewUtil {
        this.fragmentActivity = fragment
        addBottomNavigationView(bottomNavigationView)
        addViewPager2(viewPager2)
        addFragments(fragments)
        addItems(tabItems)
//        setSelectedColor(selectedColor)
//        setUnselectedColor(unselectedColor)
        attach()
        return this
    }

    /**
     * 添加底部组件
     * @param bottomNavigationView BottomNavigationView
     * @return BottomNavigationViewUntil
     */
    private fun addBottomNavigationView(bottomNavigationView: BottomNavigationView): BottomNavigationViewUtil {
        this.bottomNavigationView = bottomNavigationView
        menuView = bottomNavigationView.getChildAt(0) as BottomNavigationMenuView
        return this
    }

    /**
     * 添加ViewPager2
     * @param viewPager2 ViewPager2
     * @return BottomNavigationViewUntil
     */
    private fun addViewPager2(viewPager2: ViewPager2): BottomNavigationViewUtil {
        this.viewPager2 = viewPager2
        return this
    }

    /**
     * 添加Fragment
     * @param fragments MutableList<Fragment>
     * @return BottomNavigationViewUntil
     */
    private fun addFragments(fragments: MutableList<Fragment>): BottomNavigationViewUtil {
        this.fragments.apply {
            clear()
            addAll(fragments)
        }
        return this
    }

    /**
     * 添加底部按钮信息
     * @param tabItems MutableList<TabItem>
     * @return BottomNavigationViewUntil
     */
    private fun addItems(tabItems: MutableList<TabItem>): BottomNavigationViewUtil {
        this.tabItems.apply {
            clear()
            addAll(tabItems)
        }
        return this
    }

    /**
     * 设置选中颜色
     * @param selectedColor Int
     * @return BottomNavigationViewUntil
     */
    private fun setSelectedColor(@ColorInt selectedColor: Int): BottomNavigationViewUtil {
        this.selectedColor = selectedColor
        return this
    }

    /**
     * 设置未选中时颜色
     * @param unselectedColor Int
     * @return BottomNavigationViewUntil
     */
    private fun setUnselectedColor(@ColorInt unselectedColor: Int): BottomNavigationViewUtil {
        this.unselectedColor = unselectedColor
        return this
    }


    /**
     * 绑定BottomNavigationView和ViewPager2
     */
    private fun attach(): BottomNavigationViewUtil {
        if (fragments.size == 0 || tabItems.size == 0) {
            error("请在调用该方法前先调用addBottomNavigationView()和addItems()")
        }
        initTabItem()
        initBottomNavigationViewAndViewPager2()
        return this
    }


    /**
     * 初始化底部按钮
     */
    private fun initTabItem() {
        bottomNavigationView?.menu.apply {
            tabItems.forEachIndexed { index, tabItem ->
                this?.add(0, index, index, tabItem.title)
                this?.getItem(index)!!.setIcon(tabItem.icon)
                if (tabItem.onlyOperate) {
                    onlyOperateIndex = index
                    onlyOperateIcon = tabItem.icon
                }
            }
        }
        bottomNavigationView?.apply {
            labelVisibilityMode = NavigationBarView.LABEL_VISIBILITY_LABELED
//            val colorStateList = getColorStateList()
//            itemIconTintList = colorStateList
//            itemTextColor = colorStateList
            itemIconTintList = null
            itemTextColor = null
        }

        if (onlyOperateIndex != -1) {
            val itemView = menuView!!.getChildAt(onlyOperateIndex) as BottomNavigationItemView
            //itemView.setLabelVisibilityMode(NavigationBarView.LABEL_VISIBILITY_UNLABELED)
            val badge =
                fragmentActivity.layoutInflater.inflate(R.layout.menu_other, menuView, false)
            val ivIcon = badge.findViewById<ImageView>(R.id.ivIcon)
            ivIcon.setImageResource(onlyOperateIcon)
            badge.setOnClickListener {
                operate()
            }
            itemView.addView(badge)
        }
    }

    /**
     * 初始化view并绑定事件
     */
    private fun initBottomNavigationViewAndViewPager2() {
        var onlyOperate = false
        viewPager2?.apply {
            isUserInputEnabled = false
            adapter = BottomTabAdapter(fragmentActivity, fragments)
            //注释：出现了BottomNavigationView回调调用两次的问题时，BottomNavigationView选中时设置了VIewpager2选中对应Item。
            // 而ViewPager2设置了item选中监听，在这个监听中又设置了BottomNavigationView选中对应Item
            //所以要想viewpager2滚动，如要在BottomNavigationView监听中处理一下
//            registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
//                override fun onPageSelected(position: Int) {
//                    super.onPageSelected(position)
//                    bottomNavigationView?.selectedItemId =
//                        bottomNavigationView!!.menu.getItem(position).itemId
//                }
//            })
        }
        bottomNavigationView?.apply {
            setOnNavigationItemSelectedListener { menuItem ->
                val order = menuItem.order
                when {
                    order == onlyOperateIndex -> {
                        onlyOperate = true
                    }
                    order < onlyOperateIndex -> {
                        onlyOperate = false
                        viewPager2?.setCurrentItem(order, false)
                        currentSelectedIndex = order
                    }
                    else -> {
                        onlyOperate = false
                        viewPager2?.setCurrentItem(order - 1, false)
                        currentSelectedIndex = order

                    }
                }
                true
            }
            setOnNavigationItemReselectedListener { menuItem ->
                println("Reselected:" + menuItem.title)
            }
        }


    }

    /**
     * 生成底部颜色
     * @return ColorStateList
     */
    private fun getColorStateList(): ColorStateList {
        val states = arrayOfNulls<IntArray>(2)
        states[0] = IntArray(1) { android.R.attr.state_checked }
        states[1] = IntArray(1) { 0 }
        val colors = intArrayOf(selectedColor, unselectedColor)
        return ColorStateList(states, colors)
    }


    /**
     * 添加对应索引位置小红点
     * @param index Int
     * @param num Int
     * @return BottomNavigationViewUntil
     */
    fun addTabItemRedCirclePoint(index: Int, num: Int): BottomNavigationViewUtil {
        if (menuView == null) {
            error("请在调用该方法前先调用attach()")
        }
        if (index >= tabItems.size) {
            error("请填入正确索引值")
        }
        val itemView = menuView!!.getChildAt(index) as BottomNavigationItemView
        val badge = fragmentActivity.layoutInflater.inflate(R.layout.menu_badge, menuView, false)
        val tvNum = badge.findViewById<TextView>(R.id.tvNum)
        tvNum.text = num.toString()
        map[index] = tvNum
        itemView.addView(badge, 0)
        return this
    }


    /**
     * 改变红点展示的值，当值小于等于0时，红点隐藏
     * @param index Int
     * @param num Int
     */
    fun changeRedCirclePointNum(index: Int, num: Int) {
        if (index >= tabItems.size) {
            error("请填入正确索引值")
        }
        val tvNum = map[index]
        if (num <= 0) {
            tvNum?.visibility = View.INVISIBLE
        } else {
            tvNum?.text = num.toString()
        }
    }


    var operate: () -> Unit = {}
    fun addOnlyOperateClick(operate: () -> Unit = {}): BottomNavigationViewUtil {
        this.operate = operate
        return this
    }

}

class TabItem(
    val title: String = "",
    @DrawableRes val icon: Int,
    //@DrawableRes val icon_selected: Int,
    val onlyOperate: Boolean = false
)

class BottomTabAdapter(
    activity: FragmentActivity,
    private val fragmentList: MutableList<Fragment>
) : FragmentStateAdapter(activity) {
    constructor(
        fragment: Fragment,
        fragmentList: MutableList<Fragment>
    ) : this(fragment.requireActivity(), fragmentList)

    override fun getItemCount() = fragmentList.size

    override fun createFragment(position: Int): Fragment {
        return fragmentList[position]
    }
}