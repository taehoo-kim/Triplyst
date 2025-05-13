package com.example.triplyst.viewmodel.profile

import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class ProfileViewModel : ViewModel() {
    private val auth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()
    private val uid = auth.currentUser?.uid

    private val _nickname = MutableStateFlow("")
    val nickname: StateFlow<String> = _nickname

    private val _email = MutableStateFlow("")
    val email: StateFlow<String> = _email

    private val _preferences = MutableStateFlow<List<String>>(emptyList())
    val preferences: StateFlow<List<String>> = _preferences

    private val _travelHistory = MutableStateFlow<List<Map<String, Any>>>(emptyList())
    val travelHistory: StateFlow<List<Map<String, Any>>> = _travelHistory

    fun loadProfile() {
        uid ?: return
        firestore.collection("users").document(uid).get()
            .addOnSuccessListener { doc ->
                _nickname.value = doc.getString("nickname") ?: ""
                _email.value = doc.getString("email") ?: ""
                _preferences.value = doc.get("preferences") as? List<String> ?: emptyList()
                _travelHistory.value = doc.get("travelHistory") as? List<Map<String, Any>> ?: emptyList()
            }
    }

    fun updateNickname(newNickname: String, onResult: (Boolean, String?) -> Unit) {
        if (newNickname == nickname.value) {
            onResult(true, null)
            return
        }
        // 닉네임 중복 체크
        firestore.collection("users")
            .whereEqualTo("nickname", newNickname)
            .get()
            .addOnSuccessListener { documents ->
                if (documents.isEmpty) {
                    firestore.collection("users").document(uid!!)
                        .update("nickname", newNickname)
                        .addOnSuccessListener {
                            auth.currentUser?.updateProfile(
                                UserProfileChangeRequest.Builder()
                                    .setDisplayName(newNickname)
                                    .build()
                            )
                            _nickname.value = newNickname
                            onResult(true, null)
                        }
                        .addOnFailureListener {
                            onResult(false, "닉네임 변경 실패: ${it.message}")
                        }
                } else {
                    onResult(false, "이미 사용 중인 닉네임입니다.")
                }
            }
            .addOnFailureListener {
                onResult(false, "중복 확인 실패: ${it.message}")
            }
    }

    fun updatePreferences(newPreferences: List<String>, onResult: (Boolean) -> Unit = {}) {
        firestore.collection("users").document(uid!!)
            .update("preferences", newPreferences)
            .addOnSuccessListener {
                _preferences.value = newPreferences
                onResult(true)
            }
            .addOnFailureListener { onResult(false) }
    }

    fun addTravelHistory(place: String, year: Int, onResult: (Boolean) -> Unit = {}) {
        val newEntry = mapOf("place" to place, "year" to year)
        firestore.collection("users").document(uid!!)
            .update("travelHistory", FieldValue.arrayUnion(newEntry))
            .addOnSuccessListener {
                _travelHistory.value = _travelHistory.value + newEntry
                onResult(true)
            }
            .addOnFailureListener { onResult(false) }
    }

    fun removeTravelHistory(entry: Map<String, Any>, onResult: (Boolean) -> Unit = {}) {
        firestore.collection("users").document(uid!!)
            .update("travelHistory", FieldValue.arrayRemove(entry))
            .addOnSuccessListener {
                _travelHistory.value = _travelHistory.value.filter { it != entry }
                onResult(true)
            }
            .addOnFailureListener { onResult(false) }
    }
}
