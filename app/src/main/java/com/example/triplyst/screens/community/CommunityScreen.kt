package com.example.triplyst.screens.community

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.triplyst.model.CommunityPost
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import androidx.compose.material.icons.automirrored.filled.ArrowBack


@Composable
fun CommunityScreen(
    posts: List<CommunityPost> = samplePosts
) {
    var selectedPost by remember { mutableStateOf<CommunityPost?>(null) }

    if (selectedPost == null) {
        // 게시글 리스트
        Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
            Text("커뮤니티", style = MaterialTheme.typography.titleLarge)
            Spacer(modifier = Modifier.height(16.dp))
            LazyColumn {
                items(posts) { post ->
                    CommunityPostCard(post = post, onClick = { selectedPost = post })
                }
            }
        }
    } else {
        // 게시글 상세 보기
        CommunityPostDetail(post = selectedPost!!, onBack = { selectedPost = null })
    }
}

@Composable
fun CommunityPostCard(post: CommunityPost, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
            .clickable { onClick() }
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(post.title, style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(4.dp))
            Text("by ${post.author} · ${post.createdAt.format(DateTimeFormatter.ofPattern("yyyy.MM.dd HH:mm"))}",
                style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                post.content.take(50) + if (post.content.length > 50) "..." else "",
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
            Text("by ${post.author} · ${post.createdAt.format(DateTimeFormatter.ofPattern("yyyy.MM.dd HH:mm"))}",
                style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(modifier = Modifier.height(12.dp))
            Text(post.content, style = MaterialTheme.typography.bodyLarge)
        }
    }
}

// 샘플 더미 데이터
val samplePosts = listOf(
    CommunityPost(
        id = 1,
        author = "여행가1",
        title = "제주도 추천 코스 공유해요!",
        content = "성산일출봉, 우도, 협재해수욕장 정말 좋았어요. 맛집도 많고 경치도 끝내줍니다!",
        createdAt = LocalDateTime.now().minusDays(1)
    ),
    CommunityPost(
        id = 2,
        author = "여행가2",
        title = "부산 해운대 근처 숙소 추천 좀 해주세요",
        content = "다음주에 부산 여행 가는데 해운대 근처 가성비 좋은 숙소 아시는 분 있나요?",
        createdAt = LocalDateTime.now().minusHours(5)
    ),
    CommunityPost(
        id = 3,
        author = "여행가3",
        title = "강릉 카페 거리 후기",
        content = "강릉 카페 거리 너무 예쁘고 커피도 맛있어요. 인생샷 건졌습니다!",
        createdAt = LocalDateTime.now().minusDays(2)
    )
)
