package com.example.triplyst.screens.community.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.triplyst.model.CommunityPost
import com.example.triplyst.screens.community.utils.formatFirestoreTimestamp
import com.example.triplyst.viewmodel.community.CommunityViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CommunityPostDetail(
    post: CommunityPost,
    viewModel: CommunityViewModel,
    onBack: () -> Unit
) {
    val comments by viewModel.comments.collectAsState()

    LaunchedEffect(post.id) {
        viewModel.loadComments(post.id)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(post.title) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "뒤로가기")
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(Modifier.padding(innerPadding)) {
            PostContentSection(post = post)
            CommentSection(
                comments = comments,
                onSubmitComment = { content -> viewModel.submitComment(post.id, content) }
            )
        }
    }
}

@Composable
fun PostContentSection(post: CommunityPost) {
    Text(
        "by ${post.author} · ${formatFirestoreTimestamp(post.createdAt)}",
        style = MaterialTheme.typography.bodySmall,
        color = MaterialTheme.colorScheme.onSurfaceVariant
    )
    Spacer(modifier = Modifier.height(12.dp))
    Text(post.content, style = MaterialTheme.typography.bodyLarge)
}
