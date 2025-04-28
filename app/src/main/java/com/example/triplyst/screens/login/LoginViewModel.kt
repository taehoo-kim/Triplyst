package com.example.triplyst.screens.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class LoginViewModel : ViewModel() {
    private val auth = FirebaseAuth.getInstance()

    private val _loginState = MutableStateFlow<LoginState>(LoginState.Idle)
    val loginState: StateFlow<LoginState> = _loginState

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
                _loginState.value = if (task.isSuccessful) {
                    LoginState.Success
                } else {
                    LoginState.Error(task.exception?.message ?: "회원가입 실패")
                }
            }
    }

    fun logout() {
        auth.signOut()
        _loginState.value = LoginState.Idle
    }
}

sealed class LoginState {
    object Idle : LoginState()
    object Loading : LoginState()
    object Success : LoginState()
    data class Error(val message: String) : LoginState()
}
