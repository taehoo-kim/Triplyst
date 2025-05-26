package com.example.triplyst.model

import com.google.firebase.firestore.DocumentId

enum class NotificationType { LIKE, COMMENT, SCHEDULE }

data class Notification(
    @DocumentId val documentId: String = "",
    val userId: String = "",
    val postId: String? = null,
    val type: NotificationType = NotificationType.LIKE,
    val title: String = "",
    val message: String = "",
    val timestamp: Long = System.currentTimeMillis(),
    val read: Boolean = false
)

