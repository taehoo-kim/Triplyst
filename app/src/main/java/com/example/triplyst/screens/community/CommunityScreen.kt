package com.example.triplyst.screens.community

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.triplyst.model.CommunityPost
import com.example.triplyst.viewmodel.community.CommunityViewModel
import com.google.firebase.Timestamp
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import com.example.triplyst.viewmodel.community.CommunityUiState
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
                    post = selectedPost!!,
                    onBack = { selectedPost = null }
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
                    onDelete = { postToDelete = it}
                )
            }
        }
        is CommunityUiState.Error -> {
            ErrorMessage(message = state.message)
        }
    }
}

@Composable
private fun CommunityPostList(
    posts: List<CommunityPost>,
    onPostClick: (CommunityPost) -> Unit,
    onWriteClick: () -> Unit,
    onDelete: (CommunityPost) -> Unit
) {
    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("커뮤니티", style = MaterialTheme.typography.titleLarge)
            Spacer(modifier = Modifier.weight(1f))
            IconButton(onClick = onWriteClick) {
                Icon(Icons.Default.Add, contentDescription = "새 글 쓰기")
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
        LazyColumn {
            items(posts) { post ->
                CommunityPostCard(post = post, onClick = { onPostClick(post) }, onDelete = onDelete)
            }
        }
    }
}

@Composable
fun CommunityPostCard(post: CommunityPost,
                      onClick: () -> Unit,
                      onDelete: ((CommunityPost) -> Unit)? = null) {
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CommunityPostDetail(post: CommunityPost, onBack: () -> Unit) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(post.title) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "뒤로가기"
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(modifier = Modifier.padding(innerPadding).padding(16.dp)) {
            Text(
                "by ${post.author} · ${formatFirestoreTimestamp(post.createdAt)}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(post.content, style = MaterialTheme.typography.bodyLarge)
        }
    }
}

@Composable
private fun NewPostScreen(
    viewModel: CommunityViewModel,
    onCancel: () -> Unit,
    onSubmit: () -> Unit
) {
    var title by remember { mutableStateOf("") }
    var content by remember { mutableStateOf("") }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text("새 글 작성", style = MaterialTheme.typography.titleLarge)
        Spacer(modifier = Modifier.height(16.dp))
        OutlinedTextField(
            value = title,
            onValueChange = { title = it },
            label = { Text("제목") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = content,
            onValueChange = { content = it },
            label = { Text("내용") },
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp),
            maxLines = 10
        )
        Spacer(modifier = Modifier.height(16.dp))
        Row {
            Button(
                onClick = {
                    viewModel.submitPost(title, content)
                    onSubmit()
                },
                enabled = title.isNotBlank() && content.isNotBlank()
            ) {
                Text("등록")
            }
            Spacer(modifier = Modifier.width(8.dp))
            TextButton(onClick = onCancel) {
                Text("취소")
            }
        }
    }
}

@Composable
private fun ErrorMessage(message: String) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text(text = "오류 발생: $message", color = MaterialTheme.colorScheme.error)
    }
}

private fun formatFirestoreTimestamp(timestamp: Timestamp): String {
    val instant = Instant.ofEpochSecond(timestamp.seconds)
    val formatter = DateTimeFormatter.ofPattern("yyyy.MM.dd HH:mm")
        .withZone(ZoneId.systemDefault())
    return formatter.format(instant)
}
