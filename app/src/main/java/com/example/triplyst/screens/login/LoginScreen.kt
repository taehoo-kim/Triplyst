package com.example.triplyst.screens.login

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
import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import com.example.triplyst.viewmodel.login.LoginState
import com.example.triplyst.viewmodel.login.LoginViewModel
import com.example.triplyst.R

@Composable
fun LoginScreen(
    loginViewModel: LoginViewModel = androidx.lifecycle.viewmodel.compose.viewModel(),
    onLoginSuccess: () -> Unit,
    onEmailAuthClick: () -> Unit
) {
    val loginState by loginViewModel.loginState.collectAsState()
    val context = LocalContext.current

    // 구글 로그인 처리
    val googleSignInLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartIntentSenderForResult()
    ) { result ->
        loginViewModel.handleGoogleSignInResult(context, result.data)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Triplyst",
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.Bold,
            )
        Spacer(modifier = Modifier.height(32.dp))

        // 소셜 로그인 버튼들
        SocialLoginButton(
            text = "구글로 계속하기",
            color = Color(0xFFEAEAEA),
            iconRes = R.drawable.ic_google,
            textColor = Color(0xFF1F1F1F),
            onClick = {
                loginViewModel.startGoogleSignIn(context) { intentSender ->
                    googleSignInLauncher.launch(
                        IntentSenderRequest.Builder(intentSender).build()
                    )
                }
            }
        )
        Spacer(modifier = Modifier.height(16.dp))
        SocialLoginButton(
            text = "카카오로 계속하기",
            color = Color(0xFFFEE500),
            iconRes = R.drawable.ic_kakao,
            onClick = { loginViewModel.startKakaoLogin(context) }
        )
        Spacer(modifier = Modifier.height(16.dp))
        SocialLoginButton(
            text = "페이스북으로 계속하기",
            color = Color(0xFF1877F2),
            iconRes = R.drawable.ic_facebook,
            onClick = { /* TODO: 페이스북 로그인 */ }
        )
        Spacer(modifier = Modifier.height(32.dp))

        // 이메일 로그인 펼치기 버튼
        TextButton(onClick = onEmailAuthClick,
            modifier = Modifier.fillMaxWidth()
            ) {
            Text("이메일로 계속하기", color = MaterialTheme.colorScheme.primary)
        }

        // 에러/로딩 표시, 배포할 때는 지워야 됨.
        if (loginState is LoginState.Error) {
            Text((loginState as LoginState.Error).message, color = MaterialTheme.colorScheme.error)
            Spacer(modifier = Modifier.height(8.dp))
        }
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

@Composable
fun SocialLoginButton(
    text: String,
    color: Color,
    iconRes: Int,
    textColor: Color = Color.Unspecified,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(48.dp),
        colors = ButtonDefaults.buttonColors(containerColor = color),
        contentPadding = PaddingValues(0.dp), // 내부 여백 직접 관리
        shape = RoundedCornerShape(24.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxSize()
        ) {
            Spacer(Modifier.width(16.dp))
            Icon(
                painter = painterResource(id = iconRes),
                contentDescription = null,
                modifier = Modifier.size(24.dp), // padding 대신 Spacer로 여백 확보
                tint = Color.Unspecified
            )
            Spacer(Modifier.width(12.dp))
            Text(
                text = text,
                color = textColor,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.weight(1f),
                textAlign = TextAlign.Center
            )
            Spacer(Modifier.width(24.dp)) // 오른쪽 여백 (아이콘 크기만큼)
        }
    }
}

