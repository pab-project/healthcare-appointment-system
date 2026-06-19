package com.example.healthcareapp.data.dao

import androidx.room.*
import com.example.healthcareapp.data.entity.NotificationEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface NotificationDao {

    @Query("SELECT * FROM notifications WHERE targetEmail = :email OR targetEmail = '' ORDER BY timestamp DESC")
    fun getNotificationsForUser(email: String): Flow<List<NotificationEntity>>

    @Query("SELECT * FROM notifications WHERE targetEmail = :email OR targetEmail = '' ORDER BY timestamp DESC")
    suspend fun getNotificationsForUserOnce(email: String): List<NotificationEntity>

    @Query("SELECT COUNT(*) FROM notifications WHERE (targetEmail = :email OR targetEmail = '') AND isRead = 0")
    fun getUnreadCount(email: String): Flow<Int>

    @Query("UPDATE notifications SET isRead = 1 WHERE id = :id")
    suspend fun markAsRead(id: Int)

    @Query("UPDATE notifications SET isRead = 1 WHERE targetEmail = :email OR targetEmail = ''")
    suspend fun markAllAsRead(email: String)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(notification: NotificationEntity): Long

    @Delete
    suspend fun delete(notification: NotificationEntity)

    @Query("DELETE FROM notifications WHERE id = :id")
    suspend fun deleteById(id: Int)
}
