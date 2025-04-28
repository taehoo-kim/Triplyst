package com.example.triplyst.screens.main

import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.foundation.layout.Box
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Home
import com.example.triplyst.screens.home.HomeScreen
import com.example.triplyst.screens.community.CommunityScreen
import com.example.triplyst.screens.calendar.CalendarScreen
import com.example.triplyst.screens.chat.ChatScreen
import com.example.triplyst.screens.profile.ProfileScreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    onLogout: () -> Unit = {}
) {
    var selectedTab by remember { mutableStateOf("Home") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Triplyst") },
                actions = {
                    IconButton(onClick = { selectedTab = "Profile" }) {
                        Icon(
                            imageVector = Icons.Filled.AccountCircle,
                            contentDescription = "Profile"
                        )
                    }
                }
            )
        },
        bottomBar = {
            NavigationBar {
                val items = listOf("Home", "Community", "Calendar", "Chat")
                items.forEach { item ->
                    NavigationBarItem(
                        icon = {
                            val icon = when (item) {
                                "Home" -> Icons.Filled.Home
                                "Community" -> Icons.Filled.Group
                                "Calendar" -> Icons.Filled.CalendarToday
                                "Chat" -> Icons.AutoMirrored.Filled.Chat
                                else -> Icons.Filled.Home
                            }
                            Icon(icon, contentDescription = item)
                        },
                        label = { Text(item) },
                        selected = selectedTab == item,
                        onClick = { selectedTab = item }
                    )
                }
            }
        }
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding)) {
            when (selectedTab) {
                "Home" -> HomeScreen()
                "Community" -> CommunityScreen()
                "Calendar" -> CalendarScreen()
                "Chat" -> ChatScreen()
                "Profile" -> ProfileScreen(onLogout = onLogout)
            }
        }
    }
}
