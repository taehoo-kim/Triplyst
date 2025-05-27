package com.example.triplyst.viewmodel.login

import android.content.Context
import android.content.Intent
import android.content.IntentSender
import android.util.Log
import android.util.Patterns
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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
import com.kakao.sdk.auth.model.OAuthToken
import com.kakao.sdk.user.UserApiClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
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
                    .setServerClientId(context.getString(R.string.default_web_client_id))
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

    // 카카오 로그인 시작
    fun startKakaoLogin(context: Context) {
        _loginState.value = LoginState.Loading
        viewModelScope.launch {
            try {
                if (UserApiClient.instance.isKakaoTalkLoginAvailable(context)) {
                    startKakaoTalkLogin(context)
                } else {
                    startKakaoAccountLogin(context)
                }
            } catch (e: Exception) {
                _loginState.value = LoginState.Error("카카오 로그인 실패: ${e.localizedMessage}")
            }
        }
    }

    private fun startKakaoTalkLogin(context: Context) {
        UserApiClient.instance.loginWithKakaoTalk(context) { token, error ->
            handleKakaoResponse(token, error)
        }
    }

    private fun startKakaoAccountLogin(context: Context) {
        UserApiClient.instance.loginWithKakaoAccount(context) { token, error ->
            handleKakaoResponse(token, error)
        }
    }

    private fun handleKakaoResponse(token: OAuthToken?, error: Throwable?) {
        when {
            error != null -> {
                Log.e("LoginViewModel", "카카오 로그인 실패: ${error.localizedMessage}", error)
                _loginState.value = LoginState.Error("카카오 로그인 실패: ${error.localizedMessage}")
            }
            token != null -> {
                Log.d("LoginViewModel", "카카오 로그인 성공! AccessToken: $token")
                _loginState.value = LoginState.Success
            }
            else -> {
                _loginState.value = LoginState.Error("알 수 없는 오류 발생")
            }
        }
    }

    fun login(email: String, password: String) {
        _loginState.value = LoginState.Loading
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // 이메일 인증했는지 계속 확인하는 로직 추가
                    auth.currentUser?.reload()?.addOnCompleteListener {
                        if (auth.currentUser?.isEmailVerified == true) {
                            _loginState.value = LoginState.Success
                        } else {
                            auth.signOut()
                            _loginState.value = LoginState.Error("이메일 인증이 완료되지 않았습니다.\n인증 메일을 확인해 주세요.")
                        }
                    }
                } else {
                    _loginState.value = LoginState.Error(task.exception?.message ?: "로그인 실패")
                }
            }
    }

    // 이메일 인증 메일 재전송 (아직 ui에 추가 X)
    fun resendVerificationEmail() {
        auth.currentUser?.sendEmailVerification()
            ?.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    _loginState.value = LoginState.Info("인증 메일이 재전송되었습니다")
                } else {
                    _loginState.value = LoginState.Error("메일 전송 실패: ${task.exception?.message}")
                }
            }
    }

    // 비밀번호 재설정
    fun resetPassword(email: String) {
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            _loginState.value = LoginState.Error("유효한 이메일 형식을 입력하세요")
            return
        }

        _loginState.value = LoginState.Loading
        auth.sendPasswordResetEmail(email)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    _loginState.value = LoginState.Info("비밀번호 재설정 메일이 전송되었습니다")
                } else {
                    _loginState.value = LoginState.Error("메일 전송 실패: ${task.exception?.message}")
                }
            }
    }

    fun signup(email: String, password: String) {
        // 입력 유효성 검사
        when {
            !Patterns.EMAIL_ADDRESS.matcher(email).matches() -> {
                _loginState.value = LoginState.Error("유효한 이메일 형식이 아닙니다")
                return
            }
            password.length < 8 -> {
                _loginState.value = LoginState.Error("비밀번호는 8자 이상이어야 합니다")
                return
            }
        }

        _loginState.value = LoginState.Loading
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    sendVerificationEmail() // 인증 메일 전송
                    val user = auth.currentUser
                    val nickname = generateRandomNickname()
                    saveUserProfile(user?.uid, user?.email, nickname)

                    // 닉네임 설정
                    user?.updateProfile(
                        UserProfileChangeRequest.Builder()
                            .setDisplayName(nickname)
                            .build()
                    )
                    auth.signOut()
                    _loginState.value = LoginState.Info("인증 메일이 발송되었습니다. 인증 후 다시 로그인해 주세요.")
                } else {
                    _loginState.value = LoginState.Error(task.exception?.message ?: "회원가입 실패")
                }
            }
    }

    private fun sendVerificationEmail() {
        auth.currentUser?.sendEmailVerification()
            ?.addOnFailureListener { e ->
                Log.e("LoginViewModel", "인증 메일 전송 실패", e)
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

    fun updateErrorState(message: String) {
        _loginState.value = LoginState.Error(message)
    }
}

sealed class LoginState {
    object Idle : LoginState()
    object Loading : LoginState()
    object Success : LoginState()
    data class Error(val message: String) : LoginState()
    data class Info(val message: String) : LoginState()
}
