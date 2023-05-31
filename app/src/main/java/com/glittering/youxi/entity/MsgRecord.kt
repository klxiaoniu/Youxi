package com.glittering.youxi.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class MsgRecord(
    val chatId: Long,   //对象id
    val senderType: Int,    //0对方，1自己，2系统
    val msgType: Int,   //0文本，1图片，2json
    val content: String,
    val time: Long
) {
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0
}
