package com.glittering.youxi.ui.notifications

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.PendingIntent.FLAG_IMMUTABLE
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.content.PermissionChecker.PERMISSION_GRANTED
import androidx.core.content.PermissionChecker.checkSelfPermission
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.glittering.youxi.LoginActivity
import com.glittering.youxi.R
import com.glittering.youxi.databinding.FragmentNotificationsBinding

class NotificationsFragment : Fragment() {

    private var _binding: FragmentNotificationsBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val notificationsViewModel =
            ViewModelProvider(this).get(NotificationsViewModel::class.java)

        _binding = FragmentNotificationsBinding.inflate(inflater, container, false)
        val root: View = binding.root

//        val textView: TextView = binding.textNotifications
//        notificationsViewModel.text.observe(viewLifecycleOwner) {
//            textView.text = it
//        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU && checkSelfPermission(
                requireActivity(),
                "android.permission.POST_NOTIFICATIONS"
            ) != PERMISSION_GRANTED
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
        binding.btnNotify.setOnClickListener {
            val intent = Intent(requireActivity(), LoginActivity::class.java)
            val pi = PendingIntent.getActivity(requireActivity(), 0, intent, FLAG_IMMUTABLE)
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