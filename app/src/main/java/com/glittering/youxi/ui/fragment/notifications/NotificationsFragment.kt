package com.glittering.youxi.ui.fragment.notifications

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.core.content.PermissionChecker
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.glittering.youxi.databinding.FragmentNotificationsBinding
import com.glittering.youxi.ui.adapter.NotificationAdapter
import com.glittering.youxi.utils.DialogUtil
import com.glittering.youxi.utils.applicationContext
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class NotificationsFragment : Fragment() {

    private var _binding: FragmentNotificationsBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    lateinit var adapter: NotificationAdapter

    companion object {
        val instance: NotificationsFragment by lazy(mode = LazyThreadSafetyMode.SYNCHRONIZED) {
            NotificationsFragment()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val notificationsViewModel =
            ViewModelProvider(this).get(NotificationsViewModel::class.java)

        _binding = FragmentNotificationsBinding.inflate(inflater, container, false)
        val root: View = binding.root

        adapter = NotificationAdapter(emptyList(), requireActivity())
        val layoutManager = LinearLayoutManager(applicationContext)
        layoutManager.orientation = LinearLayoutManager.VERTICAL
        binding.recyclerview.layoutManager = layoutManager
        binding.recyclerview.adapter = adapter
        notificationsViewModel.notificationList.observe(requireActivity()) {
            adapter.setAdapterList(it)
        }

        binding.editTextSearch.addTextChangedListener {
            adapter.filter(it.toString().trim())
        }

        checkPermission()
        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onResume() {
        super.onResume()
        updateNotificationList()
    }

    private fun updateNotificationList() {
        val notificationsViewModel =
            ViewModelProvider(this).get(NotificationsViewModel::class.java)
        notificationsViewModel.updateNotificationList()
    }

    private fun checkPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU && PermissionChecker.checkSelfPermission(
                requireContext(),
                "android.permission.POST_NOTIFICATIONS"
            ) != PermissionChecker.PERMISSION_GRANTED
        ) {
            MaterialAlertDialogBuilder(requireContext())
                .setTitle("权限申请")
                .setMessage("为向您推送聊天消息、系统通知等，游兮需要您授予通知权限。请您在弹出的对话框中点击“始终允许”。")
                .setPositiveButton("确定") { _, _ ->
                    ActivityCompat.requestPermissions(
                        requireActivity(),
                        arrayOf("android.permission.POST_NOTIFICATIONS"),
                        1
                    )
                }
                .setNegativeButton("取消", null)
                .setCancelable(false)
                .show()
                .let {
                    DialogUtil.stylize(it)
                }
        }
    }
}