package com.example.triplyst.screens.calendar

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.kizitonwose.calendar.compose.HorizontalCalendar
import com.kizitonwose.calendar.compose.rememberCalendarState
import com.kizitonwose.calendar.core.CalendarDay
import com.kizitonwose.calendar.core.firstDayOfWeekFromLocale
import java.time.LocalDate
import java.time.YearMonth
import com.example.triplyst.model.TripSchedule

@Composable
fun CalendarScreen(
    schedules: List<TripSchedule> = sampleSchedules
) {
    val today = remember { LocalDate.now() }
    var selectedDate by remember { mutableStateOf(today) }
    val schedulesForSelected = schedules.filter { it.date == selectedDate }

    // 캘린더 상태 생성
    val currentMonth = remember { YearMonth.now() }
    val startMonth = remember { currentMonth.minusMonths(12) }
    val endMonth = remember { currentMonth.plusMonths(12) }
    val firstDayOfWeek = remember { firstDayOfWeekFromLocale() }

    val calendarState = rememberCalendarState(
        startMonth = startMonth,
        endMonth = endMonth,
        firstVisibleMonth = currentMonth,
        firstDayOfWeek = firstDayOfWeek
    )

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text("여행 일정 관리", style = MaterialTheme.typography.titleLarge)
        Spacer(modifier = Modifier.height(16.dp))

        // 캘린더 뷰
        HorizontalCalendar(
            state = calendarState,
            dayContent = { day ->
                Day(
                    day = day,
                    isSelected = day.date == selectedDate,
                    onClick = { selectedDate = day.date }
                )
            }
        )

        Spacer(modifier = Modifier.height(24.dp))

        Text("선택한 날짜의 일정", style = MaterialTheme.typography.titleMedium)
        LazyColumn(modifier = Modifier.weight(1f)) {
            items(schedulesForSelected) { schedule ->
                Card(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(schedule.title, style = MaterialTheme.typography.titleSmall)
                        Text(schedule.description, style = MaterialTheme.typography.bodyMedium)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = { /* TODO: 일정 추가 다이얼로그/화면 */ },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("일정 추가")
        }
    }
}

@Composable
fun Day(day: CalendarDay, isSelected: Boolean, onClick: () -> Unit) {
    Surface(
        shape = MaterialTheme.shapes.small,
        color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface,
        modifier = Modifier
            .size(40.dp)
            .padding(2.dp)
            .clickable { onClick() }
    ) {
        Box(contentAlignment = Alignment.Center) {
            Text(
                text = day.date.dayOfMonth.toString(),
                color = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

// 샘플 데이터 (model/TripSchedule.kt에 정의된 data class TripSchedule 사용)
val sampleSchedules = listOf(
    TripSchedule(1, LocalDate.now(), "제주도 도착", "공항 도착 후 렌터카 픽업"),
    TripSchedule(2, LocalDate.now(), "오설록 티뮤지엄 방문", "녹차 아이스크림 먹기"),
    TripSchedule(3, LocalDate.now().plusDays(1), "성산일출봉 등반", "아침 일찍 등산 시작")
)
