package com.example.triplyst.data.notification

import android.util.Log
import com.example.triplyst.model.Notification
import com.example.triplyst.model.NotificationType
import com.google.firebase.firestore.Query
import com.google.firebase.ktx.Firebase
import com.google.firebase.firestore.ktx.firestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class FirestoreNotificationRepository : NotificationRepository {
    private val notificationsCollection = Firebase.firestore.collection("notifications")

    override fun getNotifications(userId: String): Flow<List<Notification>> = callbackFlow {
        val listener = notificationsCollection
            .whereEqualTo("userId", userId)
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                if (snapshot != null) {
                    val notifications = snapshot.documents.mapNotNull { doc ->
                        val noti = doc.toObject(Notification::class.java)
                        noti?.copy(documentId  = doc.id)
                    }
                    trySend(notifications)
                }
            }
        awaitClose { listener.remove() }
    }

    override fun observeUnreadCount(userId: String): Flow<Int> = callbackFlow {
        val listener = notificationsCollection
            .whereEqualTo("userId", userId)
            .whereEqualTo("read", false)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Log.e("NotificationRepo", "Firestore listener error", error)
                    close(error)
                    return@addSnapshotListener
                }
                trySend(snapshot?.size() ?: 0)
            }
        awaitClose { listener.remove() }
    }

    override suspend fun addNotification(notification: Notification) {
        notificationsCollection.add(notification).await()
    }

    override suspend fun markAsRead(notificationId: String) {
        notificationsCollection.document(notificationId).update("read", true).await()
    }
}