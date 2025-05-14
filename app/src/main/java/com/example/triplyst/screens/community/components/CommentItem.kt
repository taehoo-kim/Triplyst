package com.example.triplyst.screens.community.components

import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.triplyst.model.Comment
import com.example.triplyst.screens.community.utils.formatFirestoreTimestamp

@Composable
fun CommentItem(comment: Comment) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        Column(Modifier.padding(12.dp)) {
            Text(
                comment.author,
                style = MaterialTheme.typography.labelMedium
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(comment.content)
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                formatFirestoreTimestamp(comment.createdAt),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
