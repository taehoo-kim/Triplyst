package com.example.triplyst.viewmodel.notification

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.triplyst.data.notification.NotificationRepository
import com.example.triplyst.model.Notification
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class NotificationViewModel @Inject constructor(
    private val repository: NotificationRepository
) : ViewModel() {

    private val _notifications = MutableStateFlow<List<Notification>>(emptyList())
    val notifications: StateFlow<List<Notification>> = _notifications.asStateFlow()

    private val _unreadCount = MutableStateFlow(0)
    val unreadCount: StateFlow<Int> = _unreadCount

    private var notificationsJob: Job? = null
    private var unreadCountJob: Job? = null

    fun observeMyNotifications(userId: String) {
        // 기존 구독 취소
        cancelObservations()

        // 알림 목록 구독 (백그라운드)
        notificationsJob = viewModelScope.launch(Dispatchers.IO) {
            repository.getNotifications(userId).collect { list  ->
                withContext(Dispatchers.Main.immediate) { // 메인 스레드에서 UI 업데이트
                    _notifications.value = list
                }
            }
        }

        // 읽지 않은 알림 개수 구독 (백그라운드)
        unreadCountJob = viewModelScope.launch(Dispatchers.IO) {
            repository.observeUnreadCount(userId).collect { count ->
                withContext(Dispatchers.Main.immediate) { // 메인 스레드에서 UI 업데이트
                    _unreadCount.value = count
                }
            }
        }
    }

    fun cancelObservations() {
        notificationsJob?.cancel()
        unreadCountJob?.cancel()
    }

    fun markAsRead(notificationId: String) {
        viewModelScope.launch(Dispatchers.IO) { // 백그라운드에서 처리
            repository.markAsRead(notificationId)
        }
    }
}
