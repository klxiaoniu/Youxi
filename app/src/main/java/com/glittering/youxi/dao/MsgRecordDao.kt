package com.glittering.youxi.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.glittering.youxi.entity.MsgRecord

@Dao
interface MsgRecordDao {
    @Insert
    fun insertMsgRecord(msgRecord: MsgRecord): Long

    @Update
    fun updateMsgRecord(msgRecord: MsgRecord)

    @Delete
    fun deleteMsgRecord(msgRecord: MsgRecord)

    @Query("SELECT * FROM MsgRecord WHERE chatId = :chatId")
    fun loadMsgRecord(chatId: Long): List<MsgRecord>

    @Query("SELECT * FROM MsgRecord WHERE chatId = :chatId ORDER BY time DESC LIMIT 1")
    fun loadLastMsgRecord(chatId: Long): MsgRecord?

    @Query("DELETE FROM MsgRecord WHERE chatId = :chatId")
    fun deleteMsgRecordByChatId(chatId: Long)
}