package com.example.triplyst.data

import android.util.Log
import com.example.triplyst.model.Comment
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.tasks.await
import com.example.triplyst.model.CommunityPost
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.ktx.toObjects
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

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

    // 단일 게시글 실시간 스트림
    fun getPostStream(postId: String): Flow<CommunityPost> = callbackFlow {
        val listener = firestore.collection("community_posts").document(postId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Log.e("Firestore", "Error getting post", error)
                    close(error)
                    return@addSnapshotListener
                }
                if (snapshot != null && snapshot.exists()) {
                    val post = snapshot.toObject(CommunityPost::class.java)
                    post?.let {
                        trySend(it)
                    } ?: run {
                        Log.e("Firestore", "Failed to convert post document")
                    }
                } else {
                    Log.e("Firestore", "Post document does not exist")
                }
            }
        awaitClose { listener.remove() }
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

    suspend fun toggleLike(postId: String, userId: String, isLiked: Boolean) {
        val postRef = firestore.collection("community_posts").document(postId)
        firestore.runTransaction { transaction ->
            val snapshot = transaction.get(postRef)
            if (!snapshot.exists()) {
                throw IllegalStateException("게시글이 Database에 존재하지 않습니다: $postId")
            }
            val currentLikes = snapshot.getLong("likes") ?: 0
            val currentUserLikes = (snapshot.get("userLikes") as? List<*>)?.mapNotNull { it as? String } ?: emptyList()
            val newLikes = if (isLiked) currentLikes + 1 else (currentLikes - 1).coerceAtLeast(0)
            val newUserLikes = if (isLiked)
                currentUserLikes + userId
            else
                currentUserLikes - userId
            transaction.update(postRef, mapOf(
                "likes" to newLikes,
                "userLikes" to newUserLikes
            ))
        }.await()
    }

    suspend fun updateAuthorName(userId: String, newAuthor: String){
        try {
            val querySnapshot = firestore.collection("community_posts")
                .whereEqualTo("userId", userId)
                .get()
                .await()

            for (document in querySnapshot.documents) {
                document.reference.update("author", newAuthor).await()
            }
        } catch (e: Exception) {
            Log.e("CommunityRepository", "updateAuthorName 실패", e)
            throw e
        }
    }
}