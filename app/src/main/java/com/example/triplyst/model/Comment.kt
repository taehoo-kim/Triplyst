package com.example.triplyst.model

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId

data class Comment(
    @DocumentId val id: String = "",
    val postId: String = "",  // 연결된 게시글 ID
    val userId: String = "",
    val author: String = "익명",
    val content: String = "",
    val createdAt: Timestamp = Timestamp.now()
)