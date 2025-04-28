package com.example.triplyst.screens.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage

data class Destination(val name: String, val imageUrl: String)
data class Course(val title: String, val description: String)

@Composable
fun HomeScreen(
    onAiRecommendClick: () -> Unit = {},
    recommendedDestinations: List<Destination> = sampleDestinations,
    popularCourses: List<Course> = sampleCourses
) {
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
        CourseList(courses = popularCourses)
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
                    AsyncImage(
                        model = destination.imageUrl,
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
fun CourseList(courses: List<Course>) {
    Column {
        courses.forEach { course ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text(course.title, style = MaterialTheme.typography.titleMedium)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(course.description, style = MaterialTheme.typography.bodyMedium)
                }
            }
        }
    }
}

// 샘플 더미 데이터
val sampleDestinations = listOf(
    Destination("제주도", "https://images.unsplash.com/photo-1506744038136-46273834b3fb"),
    Destination("부산 해운대", "https://images.unsplash.com/photo-1464983953574-0892a716854b"),
    Destination("서울 남산타워", "https://images.unsplash.com/photo-1519125323398-675f0ddb6308")
)
val sampleCourses = listOf(
    Course("서울 1박2일 코스", "남산타워, 경복궁, 북촌한옥마을"),
    Course("제주도 힐링 코스", "성산일출봉, 협재해수욕장, 오설록 티뮤지엄")
)
