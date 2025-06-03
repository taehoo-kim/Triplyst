package com.example.triplyst.screens.login

import android.util.Patterns
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.ui.Alignment
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.triplyst.viewmodel.login.LoginState
import com.example.triplyst.viewmodel.login.LoginViewModel

@Composable
fun EmailAuthScreen(
    onBack: () -> Unit,
    onLoginSuccess: () -> Unit,
    onPasswordReset: () -> Unit
) {
    val loginViewModel: LoginViewModel = viewModel()
    val loginState by loginViewModel.loginState.collectAsState()

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var isSignUpMode by remember { mutableStateOf(false) }
    var emailError by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(loginState) {
        when (loginState) {
            is LoginState.Success -> onLoginSuccess()
            else -> {}
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = if (isSignUpMode) "회원가입" else "로그인",
            style = MaterialTheme.typography.headlineLarge,
            modifier = Modifier.padding(bottom = 32.dp)
        )

        // 이메일 입력
        OutlinedTextField(
            value = email,
            onValueChange = {
                email = it
                emailError = null
            },
            label = { Text("이메일") },
            modifier = Modifier.fillMaxWidth(),
            isError = emailError != null
        )
        if (emailError != null) {
            Text(emailError!!, color = MaterialTheme.colorScheme.error)
        }

        Spacer(modifier = Modifier.height(16.dp))

        // 비밀번호 입력
        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("비밀번호") },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth()
        )

        // 회원가입 모드: 비밀번호 확인
        if (isSignUpMode) {
            Spacer(modifier = Modifier.height(16.dp))
            OutlinedTextField(
                value = confirmPassword,
                onValueChange = { confirmPassword = it },
                label = { Text("비밀번호 확인") },
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth()
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // 메인 액션 버튼
        Button(
            onClick = {
                if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    emailError = "유효한 이메일을 입력하세요"
                    return@Button
                }
                if (isSignUpMode && password != confirmPassword) {
                    loginViewModel.updateErrorState("비밀번호가 일치하지 않습니다")
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

        // 보조 액션 버튼
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            TextButton(onClick = { isSignUpMode = !isSignUpMode }) {
                Text(if (isSignUpMode) "로그인으로 전환" else "회원가입으로 전환")
            }
            if (!isSignUpMode) {
                TextButton(onClick = onPasswordReset) {
                    Text("비밀번호 찾기")
                }
            }
        }

        // 상태 표시
        when (loginState) {
            is LoginState.Error -> ErrorMessage((loginState as LoginState.Error).message)
            is LoginState.Info -> InfoMessage((loginState as LoginState.Info).message)
            LoginState.Loading -> LoadingIndicator()
            else -> {}
        }
    }
}

@Composable
fun ErrorMessage(message: String) {
    Text(
        text = message,
        color = MaterialTheme.colorScheme.error,
        modifier = Modifier.padding(vertical = 8.dp)
    )
}

@Composable
fun InfoMessage(message: String) {
    Text(
        text = message,
        color = MaterialTheme.colorScheme.primary,
        modifier = Modifier.padding(vertical = 8.dp)
    )
}

@Composable
fun LoadingIndicator() {
    androidx.compose.material3.CircularProgressIndicator(
        modifier = Modifier.padding(vertical = 8.dp)
    )
}
