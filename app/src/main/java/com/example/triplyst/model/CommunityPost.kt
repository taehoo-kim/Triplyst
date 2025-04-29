package com.example.triplyst.model

import java.time.LocalDateTime

data class CommunityPost(
    val id: Int,
    val author: String,
    val title: String,
    val content: String,
    val createdAt: LocalDateTime
)
