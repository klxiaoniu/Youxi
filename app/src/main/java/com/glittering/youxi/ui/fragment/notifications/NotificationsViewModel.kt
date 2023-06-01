package com.glittering.youxi.ui.fragment.notifications

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.glittering.youxi.R
import com.glittering.youxi.data.Notification
import com.glittering.youxi.database.MsgDatabase
import kotlin.concurrent.thread

class NotificationsViewModel : ViewModel() {


    val notificationList = MutableLiveData<List<Notification>>()

    init {
//        notificationList.value = listOf(
//            Notification(10000, "系统通知", "系统通知内容", 0, R.drawable.ic_notifications_black_24dp),
//            Notification(2, "聊天消息", "聊天消息内容", 0, R.drawable.ic_success)
//        )
//        updateNotificationList()
    }

    fun updateNotificationList() {
        thread {
            var tmpList= emptyList<Notification>()
            val dao=MsgDatabase.getDatabase().msgRecordDao()
            dao.loadAllChatId().forEach {
                val lastMsg=dao.loadLastMsgRecord(it)
                if(lastMsg!=null){
                    tmpList=tmpList.plus(Notification(lastMsg.chatId,"聊天消息",lastMsg.content,lastMsg.time,R.drawable.ic_success))
                }
            }
            notificationList.postValue(tmpList)
        }
    }
}