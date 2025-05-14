package com.example.triplyst.screens.community.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.triplyst.model.CommunityPost
import com.example.triplyst.screens.community.utils.formatFirestoreTimestamp
import com.google.firebase.auth.FirebaseAuth

@Composable
fun CommunityPostCard(
    post: CommunityPost,
    onClick: () -> Unit,
    onDelete: ((CommunityPost) -> Unit)? = null
) {
    val currentUser = FirebaseAuth.getInstance().currentUser
    val isMyPost = post.author == (currentUser?.displayName ?: "")

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
            .clickable { onClick() }
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(post.title, style = MaterialTheme.typography.titleMedium)
                Spacer(modifier = Modifier.weight(1f))
                if (isMyPost && onDelete != null) {
                    IconButton(onClick = { onDelete(post) }) {
                        Icon(Icons.Default.Delete, contentDescription = "삭제")
                    }
                }
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                "by ${post.author} · ${formatFirestoreTimestamp(post.createdAt)}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                post.content.take(100) + if (post.content.length > 100) "..." else "",
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}
