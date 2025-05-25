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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.triplyst.viewmodel.notification.NotificationViewModel

@Composable
fun NotificationScreen(
    navController: NavController,
    viewModel: NotificationViewModel = hiltViewModel(),
    userId: String
) {
    val notifications by viewModel.notifications.collectAsState()

    LaunchedEffect(userId) {
        viewModel.observeMyNotifications(userId)
    }

    LazyColumn(modifier = Modifier.padding(16.dp)) {
        items(notifications) { notification ->
            NotificationItem(
                notification = notification,
                onItemClick = { postId ->
                    navController.navigate("communityPostDetail/$postId")
                },
                onMarkAsRead = { notificationId ->
                    viewModel.markAsRead(notificationId)
                }
            )
            Divider()
        }
    }
}

