package com.example.triplyst.data

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.tasks.await
import com.example.triplyst.model.CommunityPost

class CommunityRepository {
    private val db = FirebaseFirestore.getInstance()
    private val postsCollection = db.collection("community_posts")

    // 게시글 목록 조회
    suspend fun getPosts(): List<CommunityPost> {
        return postsCollection
            .orderBy("createdAt", Query.Direction.DESCENDING)
            .get()
            .await()
            .documents
            .map { it.toObject(CommunityPost::class.java)!! }
    }

    // 새 게시글 추가
    suspend fun addPost(post: CommunityPost) {
        postsCollection.add(post).await()
    }
}