package com.example.triplyst.data.notification

import kotlinx.coroutines.flow.Flow
import com.example.triplyst.model.Notification
import javax.inject.Inject

interface NotificationRepository {
    fun getNotifications(): Flow<List<Notification>>
    suspend fun addNotification(notification: Notification)
    suspend fun markAsRead(notificationId: String)
}

class NotificationRepositoryImpl
    @Inject constructor(
    private val dataSource: NotificationDataSource
) : NotificationRepository {
    override fun getNotifications() = dataSource.notifications
    override suspend fun addNotification(notification: Notification) = dataSource.addNotification(notification)
    override suspend fun markAsRead(notificationId: String) = dataSource.markAsRead(notificationId)
}
