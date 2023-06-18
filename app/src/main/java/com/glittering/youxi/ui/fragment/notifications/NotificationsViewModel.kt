package com.glittering.youxi.ui.fragment.notifications

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.glittering.youxi.database.MsgDatabase
import kotlin.concurrent.thread

class NotificationsViewModel : ViewModel() {

    val notificationList = MutableLiveData<List<Long>>()

    fun updateNotificationList() {
        thread {
            notificationList.postValue(MsgDatabase.getDatabase().msgRecordDao().loadAllChatId().plus(10000L))
        }
    }

}