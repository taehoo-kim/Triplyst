package com.example.triplyst.model

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId

data class CommunityPost(
    @DocumentId val id: String = "",
    val userId: String = "", // 유저 아이디
    val author: String = "익명", // 작성자 기본 값은 익명
    val title: String = "", // 제목
    val content: String = "", // 글 내용
    val createdAt: Timestamp = Timestamp.now(), // 현재 시간
    val likes: Int = 0, // 좋아요 수
    val userLikes: List<String> = emptyList() // 좋아요 누른 유저 uid 리스트
)
