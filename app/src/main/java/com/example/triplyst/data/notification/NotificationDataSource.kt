package com.example.triplyst.data.notification

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import com.example.triplyst.model.Notification
import com.example.triplyst.model.NotificationType
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.random.Random

@Singleton
class NotificationDataSource @Inject constructor() {

    private val _notifications = MutableStateFlow(
        listOf( // 샘플 더미 알림 데이터
            Notification(
                type = NotificationType.LIKE,
                title = "새로운 좋아요",
                message = "내 여행 일정 '제주도 힐링 코스'에 좋아요가 달렸어요!",
                timestamp = System.currentTimeMillis() - Random.nextLong(3600000, 86400000) // 1~24시간 전
            ),
            Notification(
                type = NotificationType.COMMENT,
                title = "새로운 댓글",
                message = "익명 사용자: 정말 멋진 계획이네요! 💖",
                timestamp = System.currentTimeMillis() - Random.nextLong(60000, 3600000) // 1분~1시간 전
            ),
            Notification(
                type = NotificationType.SCHEDULE,
                title = "일정 알림",
                message = "내일 '서울 남산타워 방문' 일정이 있어요! ⏰",
                timestamp = System.currentTimeMillis() + 86400000 // 내일 같은 시간
            )
        )
    )
    val notifications: StateFlow<List<Notification>> = _notifications

    fun addNotification(notification: Notification) {
        _notifications.update { list -> list + notification }
    }

    fun markAsRead(notificationId: String) {
        _notifications.update { list ->
            list.map { if (it.id == notificationId) it.copy(isRead = true) else it }
        }
    }
}
