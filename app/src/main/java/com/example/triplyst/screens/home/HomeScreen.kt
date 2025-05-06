package com.example.triplyst.screens.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.triplyst.R


data class Destination(val name: String, val imageResId: Int)
data class Course(
    val title: String,
    val shortDescription: String,
    val fullDescription: String
)

@Composable
fun HomeScreen(
    onAiRecommendClick: () -> Unit = {},
    recommendedDestinations: List<Destination> = sampleDestinations,
    popularCourses: List<Course> = sampleCourses
) {
    var selectedCourse by remember { mutableStateOf<Course?>(null) }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text("추천 여행지", style = MaterialTheme.typography.titleLarge)
        Spacer(modifier = Modifier.height(8.dp))
        DestinationRow(destinations = recommendedDestinations)
        Spacer(modifier = Modifier.height(24.dp))

        Text("인기 코스", style = MaterialTheme.typography.titleLarge)
        Spacer(modifier = Modifier.height(8.dp))
        CourseList(
            courses = popularCourses,
            onCourseClick = { course -> selectedCourse = course }
        )

        selectedCourse?.let { course ->
            CourseDetailDialog(
                course = course,
                onDismiss = { selectedCourse = null }
            )
        }
        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = onAiRecommendClick,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
        ) {
            Text("AI에게 여행 코스 추천받기")
        }
    }
}

@Composable
fun DestinationRow(destinations: List<Destination>) {
    LazyRow {
        items(destinations) { destination ->
            Card(
                modifier = Modifier
                    .padding(end = 12.dp)
                    .width(140.dp)
                    .height(180.dp)
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(8.dp)
                ) {
                    Image(
                        painter = painterResource(id = destination.imageResId),
                        contentDescription = destination.name,
                        modifier = Modifier
                            .height(100.dp)
                            .fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(destination.name, style = MaterialTheme.typography.bodyLarge)
                }
            }
        }
    }
}

@Composable
fun CourseList(
    courses: List<Course>,
    onCourseClick: (Course) -> Unit
) {
    Column {
        courses.forEach { course ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
                    .clickable { onCourseClick(course) }
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text(course.title, style = MaterialTheme.typography.titleMedium)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(course.shortDescription, style = MaterialTheme.typography.bodyMedium)
                }
            }
        }
    }
}

@Composable
fun CourseDetailDialog(
    course: Course,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(course.title) },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
            ) {
                Text("간단 코스:", style = MaterialTheme.typography.titleMedium)
                Text(
                    course.shortDescription,
                    style = MaterialTheme.typography.bodyMedium
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text("상세 일정:", style = MaterialTheme.typography.titleMedium)
                Text(
                    course.fullDescription,
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.bodySmall
                )
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("닫기")
            }
        }
    )
}


// 샘플 더미 데이터
val sampleDestinations = listOf(
    Destination("제주도", R.drawable.jeju_island),
    Destination("부산 해운대", R.drawable.busan_haeundae),
    Destination("서울 남산타워", R.drawable.seoul_namsantower)
)
val sampleCourses = listOf(
    Course(
        "서울 1박2일 코스",
        "남산타워, 경복궁, 북촌한옥마을",
        """
        [1일차]
        - 오전: 남산타워 전망대 방문
        - 점심: 명동 교자
        - 오후: 경복궁 관람
        - 저녁: 광화문 광장 산책
        
        [2일차]
        - 오전: 북촌한옥마을 투어
        - 점심: 전통 한정식 체험
        - 오후: 인사동 거리 쇼핑
        """
    ),
    Course(
        "제주도 힐링 코스",
        "성산일출봉, 협재해수욕장, 오설록 티뮤지엄",
        """
        [1일차]
        - 새벽: 성산일출봉 일출 감상
        - 오전: 협재해수욕장 해변 산책
        - 점심: 흑돼지 구이 점심식사
        
        [2일차]
        - 오전: 오설록 티뮤지엄 방문
        - 오후: 카멜리아 힐 수목원 탐방
        - 저녁: 제주 전통 시장 투어
        """
    )
)
