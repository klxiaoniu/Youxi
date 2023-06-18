package com.glittering.youxi.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.glittering.youxi.entity.UserInfoStored

@Dao
interface UserInfoDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertUserInfo(userInfo: UserInfoStored): Long

    @Query("SELECT * FROM UserInfoStored WHERE userId = :userId")
    fun getUserInfo(userId: Long): UserInfoStored?
}