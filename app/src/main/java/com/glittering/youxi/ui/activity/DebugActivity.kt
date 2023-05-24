package com.glittering.youxi.ui.activity

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.content.PermissionChecker
import com.glittering.youxi.R
import com.glittering.youxi.databinding.ActivityDebugBinding
import com.glittering.youxi.utils.DialogUtil
import com.glittering.youxi.utils.setToken
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.hjq.toast.Toaster

class DebugActivity : BaseActivity<ActivityDebugBinding>() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        val btnLogin: Button = binding.btnLogin
        val btnIntro: Button = binding.btnIntro
        val btnProfile: Button = binding.btnProfile
        val btnToast: Button = binding.btnToastSucc
        val btnToastFail: Button = binding.btnToastFail
        btnLogin.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }
        btnIntro.setOnClickListener {
            val intent = Intent(this, IntroActivity::class.java)
            startActivity(intent)
        }
        btnProfile.setOnClickListener {
            val intent = Intent(this, ProfileUpdateActivity::class.java)
            startActivity(intent)
        }
        btnToast.setOnClickListener {
            Toaster.setView(R.layout.toast_success)
            Toaster.show("操作成功!")
        }
        btnToastFail.setOnClickListener {
            Toaster.setView(R.layout.toast_fail)
            Toaster.show("账号或密码不正确。账号或密码不正确。账号或密码不正确。账号或密码不正确。")
        }
        binding.btnConfirmDialog.setOnClickListener {
            val view = layoutInflater.inflate(R.layout.dialog_view, null)
            view.findViewById<TextView>(R.id.message)
                .text = "确定操作？"
            val dialog = MaterialAlertDialogBuilder(this)
                .setTitle("提示")
                .setView(view)
                .setPositiveButton("确定") { dialog, which ->
                    // Respond to positive button press
                }
                .setNegativeButton("取消") { dialog, which ->
                    // Respond to negative button press
                }
                .show()
            DialogUtil.stylize(dialog)
        }
        binding.btnNotify.setOnClickListener {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU && PermissionChecker.checkSelfPermission(
                    this,
                    "android.permission.POST_NOTIFICATIONS"
                ) != PermissionChecker.PERMISSION_GRANTED
            ) {
                val pms = arrayOf("android.permission.POST_NOTIFICATIONS")
                ActivityCompat.requestPermissions(this, pms, 1)
            }
            val manager =
                this.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val channel: List<NotificationChannel> = listOf(
                    NotificationChannel(
                        "system", "系统通知", NotificationManager.IMPORTANCE_DEFAULT
                    ), NotificationChannel(
                        "chat", "聊天消息", NotificationManager.IMPORTANCE_HIGH
                    )
                )
                channel.forEach { c ->
                    manager.createNotificationChannel(c)
                }
            }

            val intent = Intent(this, LoginActivity::class.java)
            val pi = PendingIntent.getActivity(
                this, 0, intent,
                PendingIntent.FLAG_IMMUTABLE
            )
            val notification = NotificationCompat.Builder(this, "chat")
                .setContentTitle("用户名")
                .setContentText("消息具体内容")
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
            manager.notify(1, notification)
        }
        binding.writeToken.setOnClickListener {
//val view = layoutInflater.inflate(EditText, null)
//    MaterialAlertDialogBuilder(this)
//        .setView()
            setToken("eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiJhc2Rhc2QiLCJ1aWQiOjEsImFjY2VzcyI6ImFkbWluIn0.2JvH_dyg1_3dFjfz4vUpWMl-vpImrgH9yxXSPciidi4")
        }
        binding.orderDetail.setOnClickListener {
            val intent = Intent(this, OrderDetailActivity::class.java)
            intent.putExtra("order_id", 4)
            startActivity(intent)
        }
        binding.btnChat.setOnClickListener {
            val intent = Intent(this, ChatActivity::class.java)
            intent.putExtra("chat_id",0L)
            startActivity(intent)
        }
        binding.setting.setOnClickListener {
            val intent = Intent(this, SettingActivity::class.java)
            startActivity(intent)
        }
        binding.verify.setOnClickListener {
            val intent = Intent(this, VerifyActivity::class.java)
            startActivity(intent)
        }
    }
}