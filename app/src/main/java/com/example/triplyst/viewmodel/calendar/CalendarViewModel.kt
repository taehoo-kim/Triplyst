package com.example.triplyst.viewmodel.calendar

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.triplyst.data.TripScheduleDao
import com.example.triplyst.model.TripSchedule
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalDate

class CalendarViewModel(
    private val dao: TripScheduleDao
) : ViewModel() {
    private val _selectedDate = MutableStateFlow(LocalDate.now())
    val selectedDate: StateFlow<LocalDate> = _selectedDate.asStateFlow()

    // 선택한 날짜 변경
    fun setSelectedDate(date: LocalDate) {
        _selectedDate.value = date
    }

    // 선택한 날짜의 일정 Flow
    val schedulesForSelected: StateFlow<List<TripSchedule>> =
        _selectedDate.flatMapLatest { date ->
            dao.getSchedulesByDate(date)
        }.stateIn(viewModelScope, SharingStarted.Companion.Lazily, emptyList())

    fun addSchedule(title: String, description: String) {
        viewModelScope.launch {
            val schedule = TripSchedule(
                date = _selectedDate.value,
                title = title,
                description = description
            )
            dao.insert(schedule)
        }
    }

    fun deleteSchedule(schedule: TripSchedule) {
        viewModelScope.launch { dao.delete(schedule) }
    }

    fun updateSchedule(schedule: TripSchedule) {
        viewModelScope.launch { dao.insert(schedule) } // onConflict=REPLACE로 동작
    }
}