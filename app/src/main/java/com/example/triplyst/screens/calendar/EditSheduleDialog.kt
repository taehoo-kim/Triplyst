package com.example.triplyst.screens.calendar

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import com.example.triplyst.model.TripSchedule

@Composable
fun EditScheduleDialog(
    schedule: TripSchedule,
    onDismiss: () -> Unit,
    onConfirm: (TripSchedule) -> Unit
) {
    var title by remember { mutableStateOf(schedule.title) }
    var description by remember { mutableStateOf(schedule.description) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("일정 수정") },
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
            TextButton(onClick = {
                onConfirm(schedule.copy(title = title, description = description))
            }) {
                Text("수정")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("취소")
            }
        }
    )
}
