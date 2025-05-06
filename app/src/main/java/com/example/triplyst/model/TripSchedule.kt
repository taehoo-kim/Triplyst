package com.example.triplyst.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDate

@Entity(tableName = "trip_schedule")
data class TripSchedule(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val date: LocalDate,
    val title: String,
    val description: String
)

