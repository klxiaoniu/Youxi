package com.glittering.youxi.ui.activity

import android.app.ActivityOptions
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.app.NotificationCompat
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.glittering.youxi.MyApplication.Companion.loggedInUser
import com.glittering.youxi.MyWebSocketClient
import com.glittering.youxi.R
import com.glittering.youxi.data.LoginResponse
import com.glittering.youxi.data.ServiceCreator
import com.glittering.youxi.data.UserService
import com.glittering.youxi.database.MsgDatabase
import com.glittering.youxi.databinding.ActivityMainBinding
import com.glittering.youxi.entity.MsgRecord
import com.glittering.youxi.ui.adapter.PagerAdapter
import com.glittering.youxi.ui.fragment.buy.BuyFragment
import com.glittering.youxi.ui.fragment.home.HomeFragment
import com.glittering.youxi.ui.fragment.me.MeFragment
import com.glittering.youxi.ui.fragment.notifications.NotificationsFragment
import com.glittering.youxi.utils.DarkUtil.Companion.addMaskIfDark
import com.glittering.youxi.utils.DarkUtil.Companion.isFollowSystem
import com.glittering.youxi.utils.DarkUtil.Companion.isForceDark
import com.glittering.youxi.utils.ToastFail
import com.glittering.youxi.utils.ToastInfo
import com.glittering.youxi.utils.getToken
import com.glittering.youxi.utils.rmToken
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.gson.Gson
import com.gyf.immersionbar.ImmersionBar
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.net.URI

class MainActivity : BaseActivity<ActivityMainBinding>() {

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
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
        )

        addMaskIfDark(binding.navView)

        binding.space.layoutParams.height = ImmersionBar.getNavigationBarHeight(this)
        binding.fab.imageTintList = null
        binding.fab.setOnClickListener {
            if (loggedInUser == null) {
                ToastInfo("请先登录")
                startActivity(Intent(this, LoginActivity::class.java))
            } else {
                startActivity(
                    Intent(this, NewOrderActivity::class.java),
                    ActivityOptions.makeSceneTransitionAnimation(this).toBundle()
                )
            }
        }

        if (getToken() != "") {
            val userService = ServiceCreator.create<UserService>()
            userService.loginWithToken().enqueue(object : Callback<LoginResponse> {
                override fun onResponse(
                    call: Call<LoginResponse>,
                    response: Response<LoginResponse>
                ) {
                    val code = response.body()?.code
                    if (code == 200) {
                        loggedInUser = response.body()?.data
//                        configureWebsocket()
                    } else {
                        ToastFail("登录过期，请您重新登录")
                        rmToken()
                    }
                }

                override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                    t.printStackTrace()
                }
            })
        }

        configureWebsocket()

    }

    private fun configureWebsocket() {
        val uri: URI? = URI.create("ws://121.40.165.18:8800")
        val client: MyWebSocketClient = object : MyWebSocketClient(uri){
            override fun onMessage(message: String?) {
                super.onMessage(message)
                val record = Gson().fromJson(message, MsgRecord::class.java)
                MsgDatabase.getDatabase().msgRecordDao().insertMsgRecord(record)
                sendNotification(record)
            }
        }
        client.connectBlocking()
    }

    private fun sendNotification(record: MsgRecord) {
        val manager =
            this.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if (manager.getNotificationChannel("chat") == null) {
                manager.createNotificationChannel(NotificationChannel(
                    "chat", "聊天消息", NotificationManager.IMPORTANCE_HIGH
                ))
            }
        }

        val intent = Intent(this, ChatActivity::class.java).putExtra("chat_id", record.chatId)
        val pi = PendingIntent.getActivity(
            this, 0, intent,
            PendingIntent.FLAG_IMMUTABLE
        )
        val notification = NotificationCompat.Builder(this, "chat")
            .setContentTitle(record.chatId.toString())
            .setContentText(record.content)
            .setSmallIcon(R.drawable.ic_notifications_black_24dp)   //icon
//                .setLargeIcon(
//                    BitmapFactory.decodeResource(     //头像
//                        resources,
//                        R.drawable.large_icon
//                    )
//                )
            .setContentIntent(pi)
            .setAutoCancel(true)
            .build()
        manager.notify(record.chatId.toInt(), notification)

    }
}