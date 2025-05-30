package com.example.triplyst.screens.community.components

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.triplyst.model.CommunityPost
import com.example.triplyst.screens.community.utils.formatFirestoreTimestamp
import com.example.triplyst.viewmodel.community.CommunityViewModel
import com.google.firebase.auth.FirebaseAuth

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CommunityPostDetail(
    postId: String,
    viewModel: CommunityViewModel = hiltViewModel(),
    navController: NavController
) {
    val comments by viewModel.comments.collectAsState()
    val currentUserId = FirebaseAuth.getInstance().currentUser?.uid.orEmpty()
    val post by viewModel.selectedPost.collectAsState()
//    val isLiked = post?.userLikes?.contains(currentUserId)

    LaunchedEffect(postId) {
        Log.d("PostDetail", "postId: $postId")
        viewModel.observePost(postId)
        viewModel.loadComments(postId)
    }

    post?.let { currentPost ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Text(
                text = currentPost.title,
                style = MaterialTheme.typography.headlineSmall,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            PostContentSection(post = currentPost)
            Spacer(modifier = Modifier.height(24.dp))

            // 좋아요 버튼과 카운트
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                var isProcessing by remember { mutableStateOf(false) }
                IconToggleButton(
                    checked = currentPost.userLikes.contains(currentUserId),
                    onCheckedChange = {
                        if (!isProcessing) {
                            isProcessing = true
                            viewModel.toggleLike(currentPost, currentUserId)
                            isProcessing = false
                        }
                    }
                ) {
                    Icon(
                        imageVector = if (currentPost.userLikes.contains(currentUserId))
                            Icons.Filled.Favorite
                        else
                            Icons.Filled.FavoriteBorder,
                        contentDescription = "좋아요",
                        tint = if (currentPost.userLikes.contains(currentUserId))
                            Color.Red
                        else
                            Color.Gray
                    )
                }
                Text(
                    text = "${currentPost.likes}",
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            Spacer(modifier = Modifier.height(10.dp))
            CommentSection(
                comments = comments,
                currentUserId = currentUserId,
                onSubmitComment = { content -> viewModel.submitComment(postId, content) },
                onDeleteComment = { commentId -> viewModel.deleteComment(postId, commentId) }
            )
        }
    } ?: run {
        // 로딩 중 표시
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
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
    Text(
        post.content,
        style = MaterialTheme.typography.bodyLarge,
        modifier = Modifier.fillMaxWidth()
    )
}
