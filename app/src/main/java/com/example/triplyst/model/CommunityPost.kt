package com.example.triplyst.model

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId

data class CommunityPost(
    @DocumentId val id: String = "",
    val userId: String = "",
    val author: String = "익명",
    val title: String = "",
    val content: String = "",
    val createdAt: Timestamp = Timestamp.now()
)
