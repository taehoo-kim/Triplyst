package com.example.triplyst.screens.calendar

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.kizitonwose.calendar.compose.HorizontalCalendar
import com.kizitonwose.calendar.compose.rememberCalendarState
import com.kizitonwose.calendar.core.CalendarDay
import com.kizitonwose.calendar.core.firstDayOfWeekFromLocale
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import com.example.triplyst.model.TripSchedule
import com.example.triplyst.viewmodel.calendar.CalendarViewModel
import kotlinx.coroutines.launch

@Composable
fun CalendarScreen(
    viewModel: CalendarViewModel
) {
    val selectedDate by viewModel.selectedDate.collectAsState()
    val schedulesForSelected by viewModel.schedulesForSelected.collectAsState()

    // 일정 추가 다이얼로그 상태
    var showAddDialog by remember { mutableStateOf(false) }
    var newTitle by remember { mutableStateOf("") }
    var newDescription by remember { mutableStateOf("") }

    // 일정 수정 다이얼로그 상태
    var showEditDialog by remember { mutableStateOf(false) }
    var scheduleToEdit by remember { mutableStateOf<TripSchedule?>(null) }

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
                    onClick = { viewModel.setSelectedDate(day.date) }
                )
            }
        )

        // 일정 추가 다이얼로그
        if (showAddDialog) {
            AlertDialog(
                onDismissRequest = { showAddDialog = false },
                title = { Text("일정 추가") },
                text = {
                    Column {
                        OutlinedTextField(
                            value = newTitle,
                            onValueChange = { newTitle = it },
                            label = { Text("제목") }
                        )
                        OutlinedTextField(
                            value = newDescription,
                            onValueChange = { newDescription = it },
                            label = { Text("설명") }
                        )
                    }
                },
                confirmButton = {
                    TextButton(
                        onClick = {
                            if (newTitle.isNotBlank()) {
                                viewModel.addSchedule(newTitle, newDescription)
                            }
                            showAddDialog = false
                        }
                    ) { Text("추가") }
                },
                dismissButton = {
                    TextButton(
                        onClick = { showAddDialog = false }
                    ) { Text("취소") }
                }
            )
        }

        // 일정 수정 다이얼로그
        if (showEditDialog && scheduleToEdit != null){
            EditScheduleDialog(
                schedule = scheduleToEdit!!,
                onDismiss = { showEditDialog = false },
                onConfirm = { updatedSchedule ->
                    viewModel.updateSchedule(updatedSchedule)
                    showEditDialog = false
                }
            )
        }


        Spacer(modifier = Modifier.height(24.dp))

        Text("선택한 날짜의 일정", style = MaterialTheme.typography.titleMedium)
        ScheduleList(
            schedules = schedulesForSelected,
            onDelete = { viewModel.deleteSchedule(it) },
            onEdit = {
                scheduleToEdit = it
                showEditDialog = true
            }
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                newTitle = ""
                newDescription = ""
                showAddDialog = true },
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
