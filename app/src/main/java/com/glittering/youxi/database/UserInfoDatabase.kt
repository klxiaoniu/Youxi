package com.glittering.youxi.database

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.glittering.youxi.dao.UserInfoDao
import com.glittering.youxi.entity.UserInfoStored
import com.glittering.youxi.utils.applicationContext

@Database(entities = [UserInfoStored::class], version = 1)
abstract class UserInfoDatabase : RoomDatabase() {
    abstract fun userInfoDao(): UserInfoDao

    companion object {
        private var instance: UserInfoDatabase? = null

        @Synchronized
        fun getDatabase(): UserInfoDatabase {
            if (instance == null) {
                instance = Room.databaseBuilder(
                    applicationContext,
                    UserInfoDatabase::class.java, "userinfo_database"
                )
                    .build()
            }
            return instance!!
        }
    }
}