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
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.viewpager2.widget.ViewPager2
import com.glittering.youxi.MyWebSocketClient
import com.glittering.youxi.R
import com.glittering.youxi.data.bean.LoginUser
import com.glittering.youxi.data.response.BaseDataResponse
import com.glittering.youxi.data.service.ServiceCreator
import com.glittering.youxi.data.service.UserService
import com.glittering.youxi.database.MsgDatabase
import com.glittering.youxi.databinding.ActivityMainBinding
import com.glittering.youxi.entity.MsgRecord
import com.glittering.youxi.manager.UserStateManager
import com.glittering.youxi.ui.adapter.PagerAdapter
import com.glittering.youxi.ui.fragment.buy.BuyFragment
import com.glittering.youxi.ui.fragment.home.HomeFragment
import com.glittering.youxi.ui.fragment.me.MeFragment
import com.glittering.youxi.ui.fragment.notifications.NotificationsFragment
import com.glittering.youxi.utils.DarkUtil.Companion.addMaskIfDark
import com.glittering.youxi.utils.DarkUtil.Companion.isFollowSystem
import com.glittering.youxi.utils.DarkUtil.Companion.isForceDark
import com.glittering.youxi.utils.ToastFail
import com.glittering.youxi.utils.getToken
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.gson.Gson
import com.gyf.immersionbar.ImmersionBar
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.net.URI

class MainActivity : BaseActivity<ActivityMainBinding>() {

    var client: MyWebSocketClient? = null

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
                    if (UserStateManager.getInstance().checkLogin(this)) {
                        mainViewPager.setCurrentItem(2, false)
                        binding.navView.getOrCreateBadge(R.id.navigation_notification).apply {
                            number = 0
                            isVisible = false
                        }
                    }
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
            if (UserStateManager.getInstance().checkLogin(this)) {
                startActivity(
                    Intent(this, NewOrderActivity::class.java),
                    ActivityOptions.makeSceneTransitionAnimation(this).toBundle()
                )
            }
        }


//        binding.fab.setOnLongClickListener {
//            thread {
//                val record = MsgRecord(
//                    1,
//                    0,
//                    0,
//                    "测试消息123",
//                    System.currentTimeMillis()
//                )
//                MsgDatabase.getDatabase().msgRecordDao().insertMsgRecord(record)
//
////                val intentBr = Intent("com.glittering.youxi.NEW_MSG")
////                    .putExtra("chat_id", 1)
////                LocalBroadcastManager.getInstance(this).sendBroadcast(intentBr)
//                sendNotification(record)
//            }
//            true
//        }
    }

    override fun onStart() {
        super.onStart()
        loginWithToken()
    }

    private fun loginWithToken() {
        if (getToken() != "") {
            val userService = ServiceCreator.create<UserService>()
            userService.loginWithToken().enqueue(object : Callback<BaseDataResponse<LoginUser>> {
                override fun onResponse(
                    call: Call<BaseDataResponse<LoginUser>>,
                    response: Response<BaseDataResponse<LoginUser>>
                ) {
                    if (response.body() == null) {
                        ToastFail("登录失败，请稍后重试")
                    } else {
                        if (response.body()!!.code == 200) {
                            UserStateManager.getInstance().setLoggedInUser(response.body()!!.data)
                            configureWebsocket()
                        } else {
                            ToastFail("登录过期，请您重新登录")
                            UserStateManager.getInstance().logout()
                            configureWebsocket(true)
                        }
                    }
                }

                override fun onFailure(call: Call<BaseDataResponse<LoginUser>>, t: Throwable) {
                    t.printStackTrace()
                    //无网状态
                }
            })
        }
    }

    private fun configureWebsocket(close: Boolean = false) {
        if (close) {
            client?.close()
            client = null
            return
        }
        if (client == null) {
            val uri: URI? = URI.create("ws://121.40.165.18:8800")
            client = object : MyWebSocketClient(uri) {
                override fun onMessage(message: String?) {
                    super.onMessage(message)
                    val record = Gson().fromJson(message, MsgRecord::class.java)
                    MsgDatabase.getDatabase().msgRecordDao().insertMsgRecord(record)
                    sendNotification(record)
                }
            }
        }
        if (!client!!.isOpen) {
            client!!.connectBlocking()
        }
    }

    private fun sendNotification(record: MsgRecord) {
        val intentBr = Intent("com.glittering.youxi.NEW_MSG")
            .putExtra("chat_id", record.chatId)
        val sentToActivity = LocalBroadcastManager.getInstance(this).sendBroadcast(intentBr)
        if (sentToActivity) return

        val manager =
            this.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if (manager.getNotificationChannel("chat") == null) {
                manager.createNotificationChannel(
                    NotificationChannel(
                        "chat", "聊天消息", NotificationManager.IMPORTANCE_HIGH
                    )
                )
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

        binding.navView.getOrCreateBadge(R.id.navigation_notification).number++
        // 目前的badge会在app重启后消失
    }
}