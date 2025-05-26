package com.example.triplyst.data

import android.util.Log
import com.example.triplyst.model.Comment
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.tasks.await
import com.example.triplyst.model.CommunityPost
import com.google.firebase.firestore.ktx.firestore
import com.example.triplyst.model.Notification
import com.example.triplyst.model.NotificationType
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
        try {
            // 게시글을 삭제할 때 해당 게시글의 댓글도 삭제한다.
            val comments = firestore.collection("comments")
                .whereEqualTo("postId", postId)
                .get()
                .await()

            val batch = firestore.batch()
            for (comment in comments.documents) {
                batch.delete(comment.reference)
            }
            batch.commit().await()
            db.collection("community_posts").document(postId).delete().await()
        } catch (e: Exception) {
            throw Exception("게시글 삭제 실패: ${e.message}")
        }
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

    suspend fun deleteComment(commentId: String) {
        try {
            firestore.collection("comments").document(commentId).delete().await()
        } catch (e: Exception) {
            throw Exception("댓글 삭제 실패: ${e.message}")
        }
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

    // 좋아요 알림 생성
    suspend fun sendLikeNotification(postOwnerId: String, postTitle: String, likerName: String, postId: String) {
        val notification = Notification(
            userId = postOwnerId,
            postId = postId,
            type = NotificationType.LIKE,
            title = "새로운 좋아요",
            message = "$likerName 님이 '$postTitle' 글에 좋아요를 눌렀어요!",
            timestamp = System.currentTimeMillis(),
            read = false
        )
        val docRef = Firebase.firestore.collection("notifications").add(notification).await()
        Firebase.firestore.collection("notifications").document(docRef.id)
            .update("id", docRef.id)
            .await()
    }

    // 댓글 알림 생성
    suspend fun sendCommentNotification(postOwnerId: String, postTitle: String, commenterName: String, comment: String, postId: String) {
        val notification = Notification(
            userId = postOwnerId,
            postId = postId,
            type = NotificationType.COMMENT,
            title = "새로운 댓글",
            message = "$commenterName: $comment",
            timestamp = System.currentTimeMillis(),
            read = false
        )
        val docRef = Firebase.firestore.collection("notifications").add(notification).await()
        Firebase.firestore.collection("notifications").document(docRef.id)
            .update("id", docRef.id)
            .await()
    }

    // postId로 post 객체 가져오기
    suspend fun getPostById(postId: String): CommunityPost? {
        val snapshot = firestore.collection("community_posts")
            .document(postId)
            .get()
            .await()
        return snapshot.toObject(CommunityPost::class.java)
    }
}