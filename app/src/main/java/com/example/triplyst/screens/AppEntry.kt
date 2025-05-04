package com.example.triplyst.screens

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.*

import com.example.triplyst.screens.calendar.CalendarScreen
import com.example.triplyst.screens.chat.ChatScreen
import com.example.triplyst.screens.community.CommunityScreen
import com.example.triplyst.screens.home.HomeScreen
import com.example.triplyst.screens.profile.ProfileScreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppEntry() {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    // 프로필 제외한 탭 목록
    val bottomNavRoutes = listOf("home", "community", "calendar", "chat")

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Triplyst") },
                navigationIcon = {
                    if (currentRoute !in bottomNavRoutes) {
                        IconButton(onClick = { navController.popBackStack() }) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Back"
                            )
                        }
                    }
                },
                actions = {
                    if (currentRoute in bottomNavRoutes) {
                        IconButton(onClick = { navController.navigate("profile") }) {
                            Icon(
                                imageVector = Icons.Filled.AccountCircle,
                                contentDescription = "Profile"
                            )
                        }
                    }
                }
            )
        },
        bottomBar = {
            if (currentRoute in bottomNavRoutes) {
                BottomNavigationBar(
                    currentRoute = currentRoute,
                    onTabSelected = { route ->
                        if (route != currentRoute) {
                            navController.navigate(route) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    }
                )
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = "home",
            modifier = Modifier.padding(innerPadding)
        ) {
            composable("home") { HomeScreen(
                onAiRecommendClick = { navController.navigate("chat") }
            ) }
            composable("community") { CommunityScreen() }
            composable("calendar") { CalendarScreen() }
            composable("chat") { ChatScreen() }
            composable("profile") { ProfileScreen() }
        }
    }
}

@Composable
fun BottomNavigationBar(currentRoute: String?, onTabSelected: (String) -> Unit) {
    NavigationBar {
        val items = listOf(
            "home" to Icons.Filled.Home,
            "community" to Icons.Filled.Group,
            "calendar" to Icons.Filled.CalendarToday,
            "chat" to Icons.AutoMirrored.Filled.Chat
        )
        items.forEach { (route, icon) ->
            NavigationBarItem(
                icon = { Icon(icon, contentDescription = route) },
                label = { Text(route.replaceFirstChar { it.uppercase() }) },
                selected = currentRoute == route,
                onClick = { onTabSelected(route) }
            )
        }
    }
}
