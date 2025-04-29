package com.example.triplyst.screens.profile

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage

@Composable
fun ProfileScreen(
    userName: String = "홍길동",
    email: String = "honggildong@email.com",
    profileImageUrl: String = "https://randomuser.me/api/portraits/men/10.jpg",
    preferences: List<String> = listOf("자연", "맛집", "액티비티"),
    travelHistory: List<String> = listOf("제주도 2023", "부산 2022", "강릉 2021"),
    onLogout: () -> Unit = {}
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // 프로필 이미지
        AsyncImage(
            model = profileImageUrl,
            contentDescription = "프로필 이미지",
            modifier = Modifier
                .size(100.dp)
                .clip(CircleShape)
        )
        Spacer(modifier = Modifier.height(16.dp))

        // 이름, 이메일
        Text(userName, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
        Text(email, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Spacer(modifier = Modifier.height(24.dp))

        // 선호도
        Text("선호 여행 스타일", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
        Row(
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxWidth()
        ) {
            preferences.forEach { pref ->
                AssistChip(
                    onClick = { /* TODO: 선호도 수정 */ },
                    label = { Text(pref) },
                    modifier = Modifier.padding(4.dp)
                )
            }
        }
        Spacer(modifier = Modifier.height(24.dp))

        // 여행 이력
        Text("여행 이력", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
        travelHistory.forEach { history ->
            Text("• $history", style = MaterialTheme.typography.bodyMedium, modifier = Modifier.padding(start = 8.dp, top = 2.dp))
        }
        Spacer(modifier = Modifier.height(32.dp))

        // 로그아웃 버튼
        Button(
            onClick = onLogout,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("로그아웃")
        }
    }
}
