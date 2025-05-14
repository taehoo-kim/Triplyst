package com.example.triplyst.data

import com.example.triplyst.model.Comment
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.tasks.await
import com.example.triplyst.model.CommunityPost
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObjects
import com.google.firebase.ktx.Firebase

class CommunityRepository {
    private val db = FirebaseFirestore.getInstance()
    private val postsCollection = db.collection("community_posts")
    private val firestore = Firebase.firestore

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

    suspend fun deletePost(postId: String) {
        db.collection("community_posts").document(postId).delete().await()
    }

    suspend fun getComments(postId: String): List<Comment> {
        return firestore.collection("comments")
            .whereEqualTo("postId", postId)
            .orderBy("createdAt", Query.Direction.ASCENDING)
            .get()
            .await()
            .toObjects(Comment::class.java)
    }

    suspend fun addComment(comment: Comment) {
        firestore.collection("comments")
            .add(comment)
            .await()
    }
}