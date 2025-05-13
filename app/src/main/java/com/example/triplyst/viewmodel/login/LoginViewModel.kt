package com.example.triplyst.viewmodel.login

import android.content.Context
import android.content.Intent
import android.content.IntentSender
import androidx.lifecycle.ViewModel
import com.example.triplyst.R
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.android.gms.auth.api.identity.Identity
import com.google.android.gms.auth.api.identity.SignInClient
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.util.UUID
import kotlinx.coroutines.tasks.await

class LoginViewModel : ViewModel() {
    private val auth = FirebaseAuth.getInstance()
    private lateinit var oneTapClient: SignInClient

    private val _loginState = MutableStateFlow<LoginState>(LoginState.Idle)
    val loginState: StateFlow<LoginState> = _loginState

    // 구글 로그인 시작
    fun startGoogleSignIn(context: Context, onSuccess: (IntentSender) -> Unit) {
        oneTapClient = Identity.getSignInClient(context)
        _loginState.value = LoginState.Loading

        val signInRequest = BeginSignInRequest.builder()
            .setGoogleIdTokenRequestOptions(
                BeginSignInRequest.GoogleIdTokenRequestOptions.builder()
                    .setSupported(true)
                    .setServerClientId(context.getString(R.string.default_web_client_id)) // 🔥 Firebase 콘솔에서 복사한 값으로 변경
                    .setFilterByAuthorizedAccounts(false)
                    .build()
            )
            .build()

        oneTapClient.beginSignIn(signInRequest)
            .addOnSuccessListener { result ->
                onSuccess(result.pendingIntent.intentSender)
            }
            .addOnFailureListener { e ->
                _loginState.value = LoginState.Error("구글 로그인 실패: ${e.message}")
            }
    }

    // 구글 로그인 결과 처리
    fun handleGoogleSignInResult(context: Context, data: Intent?) {
        oneTapClient = Identity.getSignInClient(context)
        try {
            val credential = oneTapClient.getSignInCredentialFromIntent(data)
            val idToken = credential.googleIdToken
            if (idToken != null) {
                val firebaseCredential = GoogleAuthProvider.getCredential(idToken, null)
                auth.signInWithCredential(firebaseCredential)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            // 최초 로그인만 프로필 생성하기 위함
                            val isNewUser = task.result?.additionalUserInfo?.isNewUser ?: false

                            var user = auth.currentUser

                            if (isNewUser && user != null) {
                                createUserProfile(user.uid, user.email)
                            }
                            _loginState.value = LoginState.Success
                        } else {
                            _loginState.value = LoginState.Error(task.exception?.message ?: "구글 로그인 실패")
                        }
                    }
            }
        } catch (e: ApiException) {
            _loginState.value = LoginState.Error("구글 로그인 오류: ${e.message}")
        }

    }

    fun login(email: String, password: String) {
        _loginState.value = LoginState.Loading
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                _loginState.value = if (task.isSuccessful) {
                    LoginState.Success
                } else {
                    LoginState.Error(task.exception?.message ?: "로그인 실패")
                }
            }
    }

    fun signup(email: String, password: String) {
        _loginState.value = LoginState.Loading
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    val nickname = generateRandomNickname()
                    saveUserProfile(user?.uid, user?.email, nickname)

                    // displayName 업데이트
                    user?.updateProfile(
                        UserProfileChangeRequest.Builder()
                            .setDisplayName(nickname)
                            .build()
                    )

                    _loginState.value = LoginState.Success
                } else {
                    _loginState.value = LoginState.Error(task.exception?.message ?: "회원가입 실패")
                }
            }
    }

    fun logout() {
        auth.signOut()
        _loginState.value = LoginState.Idle
    }

    private fun generateRandomNickname(): String {
        val adjectives = listOf("여행자", "탐험가", "트래블러", "길위의", "떠돌이", "걸리버")
        val uuid = UUID.randomUUID().toString().substring(0, 6)
        return "${adjectives.random()}_$uuid"
    }

    private fun createUserProfile(uid: String, email: String?) {
        val nickname = generateRandomNickname() // 앞서 만든 랜덤 닉네임 함수
        val userMap = mapOf(
            "nickname" to nickname,
            "email" to email,
            "createdAt" to com.google.firebase.Timestamp.now()
        )
        FirebaseFirestore.getInstance()
            .collection("users")
            .document(uid)
            .set(userMap)
    }

    private fun saveUserProfile(uid: String?, email: String?, nickname: String) {
        if (uid == null) return
        val userMap = mapOf(
            "nickname" to nickname,
            "email" to email,
            "createdAt" to com.google.firebase.Timestamp.now()
        )
        FirebaseFirestore.getInstance()
            .collection("users")
            .document(uid)
            .set(userMap)
    }
}

sealed class LoginState {
    object Idle : LoginState()
    object Loading : LoginState()
    object Success : LoginState()
    data class Error(val message: String) : LoginState()
}
