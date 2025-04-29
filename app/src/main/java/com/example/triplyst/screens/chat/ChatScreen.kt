package com.example.triplyst.screens.chat

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.triplyst.model.ChatMessage
import com.example.triplyst.model.TripCourseDetail

@Composable
fun ChatScreen() {
    var messages by remember { mutableStateOf(sampleChat) }
    var input by remember { mutableStateOf("") }
    var showDetail by remember { mutableStateOf<TripCourseDetail?>(null) }

    if (showDetail != null) {
        // 상세보기 화면
        TripCourseDetailScreen(detail = showDetail!!, onBack = { showDetail = null })
    } else {
        // 채팅 화면
        Column(modifier = Modifier.fillMaxSize()) {
            Text(
                text = "AI 여행 챗봇",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(16.dp)
            )
            Divider()
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .padding(8.dp),
                reverseLayout = false
            ) {
                items(messages) { msg ->
                    when (msg) {
                        is ChatMessage.User -> UserMessage(msg.text)
                        is ChatMessage.AI -> AIMessage(
                            msg.text,
                            hasDetail = msg.hasDetail,
                            onDetailClick = {
                                msg.detail?.let { showDetail = it }
                            }
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
            // 입력창
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = input,
                    onValueChange = { input = it },
                    modifier = Modifier.weight(1f),
                    placeholder = { Text("메시지를 입력하세요") }
                )
                Spacer(modifier = Modifier.width(8.dp))
                Button(
                    onClick = {
                        if (input.isNotBlank()) {
                            // 유저 메시지 추가
                            messages = messages + ChatMessage.User(input)
                            // AI 응답(임시 더미)
                            messages = messages + fakeAIResponse(input)
                            input = ""
                        }
                    }
                ) {
                    Text("전송")
                }
            }
        }
    }
}

@Composable
fun UserMessage(text: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.End
    ) {
        Surface(
            color = MaterialTheme.colorScheme.primaryContainer,
            shape = RoundedCornerShape(12.dp)
        ) {
            Text(
                text = text,
                modifier = Modifier.padding(12.dp),
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
        }
    }
}

@Composable
fun AIMessage(text: String, hasDetail: Boolean, onDetailClick: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Start
    ) {
        Surface(
            color = MaterialTheme.colorScheme.secondaryContainer,
            shape = RoundedCornerShape(12.dp)
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                Text(
                    text = text,
                    color = MaterialTheme.colorScheme.onSecondaryContainer
                )
                if (hasDetail) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Button(onClick = onDetailClick) {
                        Text("상세보기")
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TripCourseDetailScreen(detail: TripCourseDetail, onBack: () -> Unit) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(detail.title) },
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
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(16.dp)
        ) {
            Text(detail.description, style = MaterialTheme.typography.bodyLarge)
            Spacer(modifier = Modifier.height(16.dp))
            Text("추천 일정", style = MaterialTheme.typography.titleMedium)
            detail.schedule.forEachIndexed { idx, item ->
                Text("${idx + 1}. $item", style = MaterialTheme.typography.bodyMedium)
            }
        }
    }
}

// 샘플 데이터 및 AI 응답 예시
val sampleDetail = TripCourseDetail(
    title = "제주 2박 3일 여행 코스",
    description = "제주도의 대표 명소와 맛집을 포함한 2박 3일 추천 코스입니다.",
    schedule = listOf(
        "1일차: 제주공항 - 오설록 - 협재해수욕장",
        "2일차: 성산일출봉 - 섭지코지 - 우도",
        "3일차: 동문시장 - 용두암 - 귀가"
    )
)

val sampleChat = listOf<ChatMessage>(
    ChatMessage.AI("안녕하세요! Triplyst입니다. 궁금한 여행지를 입력해 주세요."),
)

fun fakeAIResponse(userInput: String): ChatMessage.AI {
    // 임시: "제주"라는 단어가 들어가면 상세보기 답변 제공
    return if (userInput.contains("제주")) {
        ChatMessage.AI(
            text = "제주 여행 코스를 추천해드릴 수 있어요. 상세보기를 눌러 확인해보세요!",
            hasDetail = true,
            detail = sampleDetail
        )
    } else {
        ChatMessage.AI(text = "아직 해당 지역에 대한 추천 데이터가 부족합니다. 다른 여행지도 입력해보세요!")
    }
}
