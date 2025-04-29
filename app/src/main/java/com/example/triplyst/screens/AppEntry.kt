package com.example.triplyst.screens

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import com.example.triplyst.screens.login.LoginViewModel
import com.example.triplyst.screens.login.LoginScreen
import com.example.triplyst.screens.main.MainScreen
import com.google.firebase.auth.FirebaseAuth
import androidx.compose.runtime.remember
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.triplyst.screens.chat.ChatScreen
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.ui.Modifier
import com.example.triplyst.screens.calendar.CalendarScreen
import com.example.triplyst.screens.community.CommunityScreen
import com.example.triplyst.screens.home.HomeScreen
import com.example.triplyst.screens.profile.ProfileScreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppEntry(
    loginViewModel: LoginViewModel = androidx.lifecycle.viewmodel.compose.viewModel(),
    onLogout: () -> Unit = {}
) {
    val navController = rememberNavController()
    val currentRoute = navController.currentBackStackEntryAsState().value?.destination?.route

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Triplyst") },
                navigationIcon = {
                    if (currentRoute != "main") { // 메인 외 화면에서만 뒤로가기 버튼 표시
                        IconButton(onClick = { navController.popBackStack() }) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "뒤로가기"
                            )
                        }
                    }
                },
                actions = {
                    if (currentRoute == "main") { // 메인 화면에서만 프로필 버튼 표시
                        IconButton(onClick = { navController.navigate("profile") }) {
                            Icon(
                                imageVector = Icons.Filled.AccountCircle,
                                contentDescription = "프로필"
                            )
                        }
                    }
                }
            )
        },
        bottomBar = {
            if (currentRoute in listOf("home", "community", "calendar", "chat")) { // 탭 화면에서만 표시
                NavigationBar {
                    val items = listOf("home", "community", "calendar", "chat")
                    items.forEach { item ->
                        NavigationBarItem(
                            icon = {
                                val icon = when (item) {
                                    "home" -> Icons.Filled.Home
                                    "community" -> Icons.Filled.Group
                                    "calendar" -> Icons.Filled.CalendarToday
                                    "chat" -> Icons.AutoMirrored.Filled.Chat
                                    else -> Icons.Filled.Home
                                }
                                Icon(icon, contentDescription = item)
                            },
                            label = { Text(item.replaceFirstChar { it.uppercase() }) },
                            selected = currentRoute == item,
                            onClick = { navController.navigate(item) }
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = "main",
            modifier = Modifier.padding(innerPadding)
        ) {
            composable("main") {
                MainScreen(
                    onAiRecommendClick = { navController.navigate("chat") }
                )
            }
            composable("home") { HomeScreen(onAiRecommendClick = { navController.navigate("chat") }) }
            composable("community") { CommunityScreen() }
            composable("calendar") { CalendarScreen() }
            composable("chat") { ChatScreen() }
            composable("profile") { ProfileScreen(onLogout = onLogout) }
        }
    }
}