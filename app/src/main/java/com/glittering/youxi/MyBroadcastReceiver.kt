package com.glittering.youxi

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent


class MyBroadcastReceiver : BroadcastReceiver() {

    lateinit var chatInteraction: ChatInteraction

    override fun onReceive(context: Context, intent: Intent) {
        chatInteraction.onNewMsg(intent.getLongExtra("chat_id",-1))
    }

    interface ChatInteraction {
        fun onNewMsg(chat_id:Long)
    }

    fun setChatInteractionListener(interaction: ChatInteraction) {
        chatInteraction = interaction
    }
}
