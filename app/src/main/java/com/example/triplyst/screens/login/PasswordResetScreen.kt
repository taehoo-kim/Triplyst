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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.collectAsState
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.triplyst.viewmodel.login.LoginState
import com.example.triplyst.viewmodel.login.LoginViewModel

@Composable
fun PasswordResetScreen(onBack: () -> Unit) {
    val loginViewModel: LoginViewModel = viewModel()
    val loginState by loginViewModel.loginState.collectAsState()
    var email by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        verticalArrangement = Arrangement.Center
    ) {
        Text("비밀번호 재설정", style = MaterialTheme.typography.headlineLarge)
        Spacer(modifier = Modifier.height(32.dp))

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("등록된 이메일") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = {
                loginViewModel.resetPassword(email)
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("전송하기")
        }

        // ViewModel의 상태에 따라 메시지 표시
        when (loginState) {
            is LoginState.Info -> Text(
                (loginState as LoginState.Info).message,
                color = Color.Green,
                modifier = Modifier.padding(top = 16.dp)
            )
            is LoginState.Error -> Text(
                (loginState as LoginState.Error).message,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(top = 16.dp)
            )
            LoginState.Loading -> {
                Spacer(modifier = Modifier.height(8.dp))
                androidx.compose.material3.CircularProgressIndicator()
            }
            else -> {}
        }

        Spacer(modifier = Modifier.height(16.dp))
        TextButton(onClick = onBack) {
            Text("뒤로가기")
        }
    }
}
