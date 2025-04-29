package com.example.triplyst.model

sealed class ChatMessage {
    data class User(val text: String) : ChatMessage()
    data class AI(
        val text: String,
        val hasDetail: Boolean = false,
        val detail: TripCourseDetail? = null
    ) : ChatMessage()
}

data class TripCourseDetail(
    val title: String,
    val description: String,
    val schedule: List<String>
)
