package com.example.triplyst.screens.notification

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Comment
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.triplyst.model.Notification
import com.example.triplyst.model.NotificationType

@Composable
fun NotificationItem(notification: Notification) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = when (notification.type) {
                    NotificationType.COMMENT -> Icons.Filled.Comment
                    NotificationType.LIKE -> Icons.Filled.Favorite
                    NotificationType.SCHEDULE -> Icons.Filled.Notifications
                },
                contentDescription = null,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = notification.title,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold
            )
        }
        Text(
            text = notification.message,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(start = 32.dp)
        )
        Text(
            text = java.text.SimpleDateFormat("MM/dd HH:mm").format(notification.timestamp),
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.outline,
            modifier = Modifier.padding(start = 32.dp)
        )
    }
}
