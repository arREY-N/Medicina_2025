package com.example.medicina.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.medicina.model.Notification
import kotlinx.coroutines.flow.Flow
import androidx.room.OnConflictStrategy
import androidx.room.Update

@Dao
interface NotificationDao {

    @Query("Select * FROM notifications")
    fun getAllNotifications(): Flow<List<Notification>>

    @Query("Select * FROM notifications WHERE id = :id")
    suspend fun getNotificationById(id: Int): Notification?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNotification(notification: Notification): Long

    @Update
    suspend fun updateNotification(notification: Notification)

    @Query("DELETE FROM notifications WHERE id = :id")
    suspend fun deleteNotification(id: Int)

}