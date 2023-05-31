package com.glittering.youxi.ui.fragment.notifications

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.glittering.youxi.R
import com.glittering.youxi.data.Notification
import com.glittering.youxi.data.NotificationAdapter
import com.glittering.youxi.databinding.FragmentNotificationsBinding
import com.glittering.youxi.utils.applicationContext

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

        val list: List<Notification> = listOf(
            Notification(10000, "系统通知", "系统通知内容", 0, R.drawable.ic_notifications_black_24dp),
            Notification(2, "聊天消息", "聊天消息内容", 0, R.drawable.ic_success)
        )
        adapter = NotificationAdapter(list,requireActivity())
        val layoutManager = LinearLayoutManager(applicationContext)
        layoutManager.orientation = LinearLayoutManager.VERTICAL
        binding.recyclerview.layoutManager = layoutManager
        binding.recyclerview.adapter = adapter


        binding.editTextSearch.addTextChangedListener {
            adapter.filter(it.toString().trim())
        }
        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}