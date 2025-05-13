package com.example.triplyst.screens.login

import android.content.Context
import android.content.Intent
import android.content.IntentSender
import android.util.Patterns
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.Column
import androidx.compose.ui.platform.LocalContext
import com.example.triplyst.viewmodel.login.LoginState
import com.example.triplyst.viewmodel.login.LoginViewModel
import com.google.android.gms.auth.api.identity.Identity
import com.google.android.gms.auth.api.identity.SignInClient
import com.google.firebase.auth.GoogleAuthProvider

@Composable
fun LoginScreen(
    loginViewModel: LoginViewModel = androidx.lifecycle.viewmodel.compose.viewModel(),
    onLoginSuccess: () -> Unit
) {
    val loginState by loginViewModel.loginState.collectAsState()
    val context = LocalContext.current

    // 구글 로그인 처리
    val googleSignInLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartIntentSenderForResult()
    ) { result ->
        loginViewModel.handleGoogleSignInResult(context, result.data)
    }

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isSignUpMode by remember { mutableStateOf(false) }
    var emailError by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = if (isSignUpMode) "회원가입" else "로그인", style = MaterialTheme.typography.headlineLarge)
        Spacer(modifier = Modifier.height(24.dp))
        OutlinedTextField(
            value = email,
            onValueChange = {
                email = it
                emailError = null },
            label = { Text("이메일") },
            modifier = Modifier.fillMaxWidth(),
            isError = emailError != null
        )
        if (emailError != null) {
            Text(emailError!!, color = MaterialTheme.colorScheme.error)
        }
        Spacer(modifier = Modifier.height(16.dp))
        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("비밀번호") },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(24.dp))

        if (loginState is LoginState.Error) {
            Text((loginState as LoginState.Error).message, color = MaterialTheme.colorScheme.error)
            Spacer(modifier = Modifier.height(8.dp))
        }

        Button(
            onClick = {
                val trimmedEmail = email.trim()
                // 이메일 형식 체크
                if (!Patterns.EMAIL_ADDRESS.matcher(trimmedEmail).matches()) {
                    emailError = "올바른 이메일 형식을 입력하세요."
                    return@Button
                }
                if (isSignUpMode) {
                    loginViewModel.signup(email, password)
                } else {
                    loginViewModel.login(email, password)
                }
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = loginState != LoginState.Loading
        ) {
            Text(if (isSignUpMode) "회원가입" else "로그인")
        }

        // 구글 로그인 버튼
        Button(
            onClick = {
                loginViewModel.startGoogleSignIn(context) { intentSender ->
                    googleSignInLauncher.launch(
                        IntentSenderRequest.Builder(intentSender).build()
                    )
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Google로 계속하기")
        }

        TextButton(onClick = { isSignUpMode = !isSignUpMode }) {
            Text(if (isSignUpMode) "이미 계정이 있으신가요? 로그인" else "계정이 없으신가요? 회원가입")
        }

        // 로딩 인디케이터
        if (loginState == LoginState.Loading) {
            CircularProgressIndicator(modifier = Modifier.padding(top = 16.dp))
        }
    }

    // 로그인 성공 시 콜백
    if (loginState == LoginState.Success) {
        LaunchedEffect(Unit) {
            onLoginSuccess()
        }
    }
}

