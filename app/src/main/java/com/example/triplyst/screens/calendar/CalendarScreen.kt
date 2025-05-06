package com.example.triplyst.screens.calendar

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.kizitonwose.calendar.compose.HorizontalCalendar
import com.kizitonwose.calendar.compose.rememberCalendarState
import com.kizitonwose.calendar.core.CalendarDay
import com.kizitonwose.calendar.core.firstDayOfWeekFromLocale
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import com.example.triplyst.model.TripSchedule
import kotlinx.coroutines.launch

@Composable
fun CalendarScreen(
    schedules: List<TripSchedule> = sampleSchedules
) {
    val today = remember { LocalDate.now() }
    var selectedDate by remember { mutableStateOf(today) }
    val schedulesForSelected = schedules.filter { it.date == selectedDate }
    var showDialog by remember { mutableStateOf(false) }

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

    // 현재 보이는 월 추적
    val coroutineScope = rememberCoroutineScope()
    val monthFormatter = remember { DateTimeFormatter.ofPattern("yyyy년 MM월") }
    val currentMonthVisible = calendarState.firstVisibleMonth.yearMonth

    // 다이얼로그 표시
    if (showDialog) {
        var title by remember { mutableStateOf("") }
        var description by remember { mutableStateOf("") }

        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text("일정 추가") },
            text = {
                Column {
                    OutlinedTextField(
                        value = title,
                        onValueChange = { title = it },
                        label = { Text("제목") }
                    )
                    OutlinedTextField(
                        value = description,
                        onValueChange = { description = it },
                        label = { Text("설명") }
                    )
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        // 일정 추가하는 기능을 넣어야 함.

                        showDialog = false
                    }
                ) { Text("추가") }
            },
            dismissButton = {
                TextButton(
                    onClick = { showDialog = false }
                ) { Text("취소") }
            }
        )
    }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        // 월 선택 헤더
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            IconButton(
                onClick = {
                    coroutineScope.launch {
                        calendarState.animateScrollToMonth(currentMonthVisible.minusMonths(1))
                    }
                }
            ) {
                Icon(Icons.Default.ChevronLeft, contentDescription = "이전 달")
            }

            Text(
                text = currentMonthVisible.format(monthFormatter),
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.weight(1f),
                textAlign = TextAlign.Center
            )

            IconButton(
                onClick = {
                    coroutineScope.launch {
                        calendarState.animateScrollToMonth(currentMonthVisible.plusMonths(1))
                    }
                }
            ) {
                Icon(Icons.Default.ChevronRight, contentDescription = "다음 달")
            }
        }

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
            onClick = { showDialog = true },
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
