package com.glittering.youxi.ui.activity

import android.os.Bundle
import com.glittering.youxi.R
import com.glittering.youxi.databinding.ActivityChatBinding
import com.gyf.immersionbar.ktx.fitsTitleBar

class ChatActivity : BaseActivity<ActivityChatBinding>() {
    override val fitSystemWindows: Boolean
        get() = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding.toolbar.let {
            it.setNavigationIcon(R.drawable.ic_back)
            it.setNavigationOnClickListener { finish() }
            it.inflateMenu(R.menu.chat_menu)
        }
        fitsTitleBar(binding.toolbar)
        //binding.bottomView.marginBottom=navigationBarHeight

        val chatId = intent.getLongExtra("chat_id", -1L)
        if (chatId == -1L) {
            finish()
            return
        }
        //TODO:加载聊天消息
    }
}