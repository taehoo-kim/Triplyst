package com.example.triplyst.model

import java.util.UUID

enum class NotificationType {
    COMMENT, LIKE, SCHEDULE
}

data class Notification(
    val id: String = UUID.randomUUID().toString(),
    val type: NotificationType,
    val title: String,
    val message: String,
    val timestamp: Long = System.currentTimeMillis(),
    val isRead: Boolean = false
)
