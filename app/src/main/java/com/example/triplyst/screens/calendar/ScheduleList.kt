package com.example.triplyst.screens.calendar

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.DismissDirection
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.IconButton
import androidx.compose.material.SwipeToDismiss
import androidx.compose.material.rememberDismissState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.triplyst.model.TripSchedule
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun ScheduleList(
    schedules: List<TripSchedule>,
    onDelete: (TripSchedule) -> Unit,
    onEdit: (TripSchedule) -> Unit
) {
    LazyColumn {
        items(schedules, key = { it.id }) { schedule ->
            val dismissState = rememberDismissState()
            val scope = rememberCoroutineScope()

            SwipeToDismiss(
                state = dismissState,
                directions = setOf(DismissDirection.EndToStart),
                background = {
                    Row(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.End,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(onClick = {
                            onEdit(schedule)
                            scope.launch {
                                dismissState.reset()
                            }
                        }
                        ) {
                            androidx.compose.material3.Icon(
                                Icons.Default.Edit, contentDescription = "수정"
                            )
                        }
                        Spacer(Modifier.width(8.dp))
                        IconButton(onClick = {
                            onDelete(schedule)
                            scope.launch {
                                dismissState.reset()
                            }
                        }) {
                            androidx.compose.material3.Icon(
                                Icons.Default.Delete, contentDescription = "삭제"
                            )
                        }
                    }
                },
                dismissContent = {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text(schedule.title, style = MaterialTheme.typography.titleSmall)
                            Text(schedule.description, style = MaterialTheme.typography.bodyMedium)
                        }
                    }
                }
            )
        }
    }
}
