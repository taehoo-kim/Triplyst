package com.example.triplyst.screens

import androidx.compose.runtime.Composable
import com.example.triplyst.screens.login.LoginViewModel
import com.example.triplyst.screens.login.LoginScreen
import com.example.triplyst.screens.main.MainScreen
import com.google.firebase.auth.FirebaseAuth
import androidx.compose.runtime.remember
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue

@Composable
fun AppEntry(
    loginViewModel: LoginViewModel = androidx.lifecycle.viewmodel.compose.viewModel()
) {
    // val isLoggedIn = FirebaseAuth.getInstance().currentUser != null
    val isLoggedIn = true // 개발 중엔 무조건 로그인 상태로
    var showMain by remember { mutableStateOf(isLoggedIn) }

    if (showMain) {
        MainScreen(
            onLogout = {
                loginViewModel.logout()
                showMain = false
            }
        )
    } else {
        LoginScreen(
            loginViewModel = loginViewModel,
            onLoginSuccess = { showMain = true }
        )
    }
}
