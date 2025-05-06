package com.example.triplyst.screens.notification

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Comment
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.triplyst.model.Notification
import com.example.triplyst.model.NotificationType
import com.example.triplyst.viewmodel.notification.NotificationViewModel
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun NotificationScreen(
    viewModel: NotificationViewModel = hiltViewModel()
) {
    val notifications by viewModel.notifications.collectAsState()

    LazyColumn(modifier = Modifier.padding(16.dp)) {
        items(notifications) { notification ->
            NotificationItem(notification = notification)
            Divider()
        }
    }
}

