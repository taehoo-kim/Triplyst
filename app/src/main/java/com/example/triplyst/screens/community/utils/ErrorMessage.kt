package com.example.triplyst.screens.community.utils

import com.google.firebase.Timestamp
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

fun formatFirestoreTimestamp(timestamp: Timestamp): String {
    val instant = Instant.ofEpochSecond(timestamp.seconds)
    val formatter = DateTimeFormatter.ofPattern("yyyy.MM.dd HH:mm")
        .withZone(ZoneId.systemDefault())
    return formatter.format(instant)
}