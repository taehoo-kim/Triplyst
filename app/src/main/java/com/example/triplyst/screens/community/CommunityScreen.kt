package com.example.triplyst.screens.community

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.triplyst.model.CommunityPost
import com.example.triplyst.screens.community.components.*
import com.example.triplyst.viewmodel.community.*
import com.google.firebase.Timestamp
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import com.google.firebase.auth.FirebaseAuth


@Composable
fun CommunityScreen(
    viewModel: CommunityViewModel = viewModel()
) {

    val uiState by viewModel.uiState.collectAsState()
    var selectedPost by remember { mutableStateOf<CommunityPost?>(null) }
    var isWriting by remember { mutableStateOf(false) }
    var postToDelete by remember { mutableStateOf<CommunityPost?>(null) }

    LaunchedEffect(Unit) {
        viewModel.loadPosts()
    }

    if (postToDelete != null) {
        AlertDialog(
            onDismissRequest = { postToDelete = null },
            title = { Text("게시글 삭제") },
            text = { Text("정말 이 게시글을 삭제하시겠습니까?") },
            confirmButton = {
                Button(onClick = {
                    viewModel.deletePost(postToDelete!!.id)
                    postToDelete = null
                }) { Text("삭제") }
            },
            dismissButton = {
                TextButton(onClick = { postToDelete = null }) { Text("취소") }
            }
        )
    }

    when (val state = uiState) {
        is CommunityUiState.Loading -> {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        }
        is CommunityUiState.Success -> {
            if (selectedPost != null) {
                CommunityPostDetail(
                    postId = selectedPost!!.id,
                    onBack = { selectedPost = null },
                    viewModel = viewModel
                )
            } else if (isWriting) {
                NewPostScreen(
                    viewModel = viewModel,
                    onCancel = { isWriting = false },
                    onSubmit = {
                        isWriting = false
                        viewModel.loadPosts()
                    }
                )
            } else {
                CommunityPostList(
                    posts = state.posts,
                    onPostClick = { selectedPost = it },
                    onWriteClick = { isWriting = true },
                    onDelete = { postToDelete = it }
                )
            }
        }
        is CommunityUiState.Error -> {
            ErrorMessage(message = state.message)
        }
    }
}

@Composable
private fun ErrorMessage(message: String) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text(text = "오류 발생: $message", color = MaterialTheme.colorScheme.error)
    }
}

