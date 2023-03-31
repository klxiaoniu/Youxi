package com.glittering.youxi.ui.fragment.home

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.content.PermissionChecker
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.glittering.youxi.R
import com.glittering.youxi.databinding.FragmentHomeBinding
import com.glittering.youxi.ui.activity.IntroActivity
import com.glittering.youxi.ui.activity.LoginActivity
import com.glittering.youxi.ui.activity.ProfileUpdateActivity
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.hjq.toast.Toaster

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

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


        val btnLogin: Button = binding.btnLogin
        val btnIntro: Button = binding.btnIntro
        val btnProfile: Button = binding.btnProfile
        val btnToast: Button = binding.btnToastSucc
        val btnToastFail: Button = binding.btnToastFail
        btnLogin.setOnClickListener {
            val intent = Intent(context, LoginActivity::class.java)
            startActivity(intent)
        }
        btnIntro.setOnClickListener {
            val intent = Intent(context, IntroActivity::class.java)
            startActivity(intent)
        }
        btnProfile.setOnClickListener {
            val intent = Intent(context, ProfileUpdateActivity::class.java)
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
            val dialog = MaterialAlertDialogBuilder(requireActivity())
                .setTitle("提示")
                .setView(view)
                .setPositiveButton("确定") { dialog, which ->
                    // Respond to positive button press
                }
                .setNegativeButton("取消") { dialog, which ->
                    // Respond to negative button press
                }
                .show()
            val btnPositive = dialog.getButton(AlertDialog.BUTTON_POSITIVE)
            val btnNegative = dialog.getButton(AlertDialog.BUTTON_NEGATIVE)
            btnPositive.setBackgroundColor(resources.getColor(R.color.primary_yellow, null))
            btnPositive.setTextColor(resources.getColor(R.color.black, null))
            btnNegative.setBackgroundColor(resources.getColor(R.color.primary_yellow_2, null))
            btnNegative.setTextColor(resources.getColor(R.color.black, null))
            btnPositive.setPadding(80, 50, 80, 50)
            btnNegative.setPadding(80, 50, 80, 50)
            btnPositive.setTextSize(18f)
            btnNegative.setTextSize(18f)
        }
        binding.btnNotify.setOnClickListener {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU && PermissionChecker.checkSelfPermission(
                    requireActivity(),
                    "android.permission.POST_NOTIFICATIONS"
                ) != PermissionChecker.PERMISSION_GRANTED
            ) {
                val pms = arrayOf("android.permission.POST_NOTIFICATIONS")
                ActivityCompat.requestPermissions(requireActivity(), pms, 1)
            }
            val manager =
                requireActivity().getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
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

            val intent = Intent(requireActivity(), LoginActivity::class.java)
            val pi = PendingIntent.getActivity(requireActivity(), 0, intent,
                PendingIntent.FLAG_IMMUTABLE
            )
            val notification = NotificationCompat.Builder(requireActivity(), "chat")
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

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}