package com.example.triplyst.model

import java.time.LocalDate

data class TripSchedule(
    val id: Int,
    val date: LocalDate,
    val title: String,
    val description: String
)
