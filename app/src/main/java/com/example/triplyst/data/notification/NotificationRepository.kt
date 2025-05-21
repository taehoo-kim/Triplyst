package com.example.triplyst.data.notification

import kotlinx.coroutines.flow.Flow
import com.example.triplyst.model.Notification

interface NotificationRepository {
    fun getNotifications(userId: String): Flow<List<Notification>>
    suspend fun addNotification(notification: Notification)
    suspend fun markAsRead(notificationId: String)
}