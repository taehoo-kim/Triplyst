package com.example.triplyst.screens.community.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.triplyst.model.CommunityPost


@Composable
fun CommunityPostList(
    posts: List<CommunityPost>,
    onPostClick: (CommunityPost) -> Unit,
    onWriteClick: () -> Unit,
    onDelete: (CommunityPost) -> Unit
) {
    Column(Modifier.fillMaxSize().padding(16.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("커뮤니티", style = MaterialTheme.typography.titleLarge)
            Spacer(Modifier.weight(1f))
            IconButton(onClick = onWriteClick) {
                Icon(Icons.Default.Add, "새 글 쓰기")
            }
        }
        Spacer(Modifier.height(16.dp))
        LazyColumn {
            items(posts) { post ->
                CommunityPostCard(
                    post = post,
                    onClick = { onPostClick(post) },
                    onDelete = { onDelete(post) }
                )
            }
        }
    }
}
