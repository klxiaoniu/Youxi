package com.glittering.youxi.database

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.glittering.youxi.dao.MsgRecordDao
import com.glittering.youxi.entity.MsgRecord
import com.glittering.youxi.manager.UserStateManager
import com.glittering.youxi.utils.applicationContext

@Database(entities = [MsgRecord::class], version = 1)
abstract class MsgDatabase : RoomDatabase() {
    abstract fun msgRecordDao(): MsgRecordDao

    companion object {
        private var instance: MsgDatabase? = null
        private var userId: Long? = null

        @Synchronized
        fun getDatabase(): MsgDatabase {
            if (instance == null || userId != UserStateManager.getInstance().getUserId()) {
                userId = UserStateManager.getInstance().getUserId()
                instance = Room.databaseBuilder(
                    applicationContext,
                    MsgDatabase::class.java,
                    "msg_database_$userId"
                )
                    .build()
            }
            return instance!!
        }
    }
}