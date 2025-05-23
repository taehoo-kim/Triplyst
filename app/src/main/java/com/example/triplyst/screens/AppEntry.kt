package com.example.triplyst.screens

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.*
import com.example.triplyst.data.DatabaseProvider
import com.example.triplyst.screens.calendar.CalendarScreen
import com.example.triplyst.viewmodel.calendar.CalendarViewModel
import com.example.triplyst.screens.chat.ChatScreen
import com.example.triplyst.screens.community.CommunityScreen
import com.example.triplyst.screens.home.HomeScreen
import com.example.triplyst.screens.login.LoginScreen
import com.example.triplyst.screens.notification.NotificationScreen
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
            if(currentRoute != "login") {
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
                            IconButton(onClick = { navController.navigate("notifications") }) {
                                Icon(Icons.Filled.Notifications, contentDescription = "알림")
                            }
                            IconButton(onClick = { navController.navigate("profile") }) {
                                Icon(
                                    imageVector = Icons.Filled.AccountCircle,
                                    contentDescription = "Profile"
                                )
                            }
                        }
                    }
                )
            }
        },
        bottomBar = {
            if (currentRoute in bottomNavRoutes) {
                BottomNavigationBar(
                    currentRoute = currentRoute,
                    onTabSelected = { route ->
                        // 근데 이게 과연 맞는 방법일까 ..? 더 나은 방법은 없을지
                        if (route == "home") {
                            navController.popBackStack("home", inclusive = false)
                            navController.navigate("home") {
                                launchSingleTop = true
                            }
                        } else {
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
            startDestination = "login",
            modifier = Modifier.padding(innerPadding)
        ) {
            composable("login"){ LoginScreen(
                onLoginSuccess = { navController.navigate("home"){
                    popUpTo("login") { inclusive = true }
                } }
            )

            }
            composable("home") { HomeScreen(
                onAiRecommendClick = { navController.navigate("chat") }
            ) }
            composable("community") { CommunityScreen() }
            composable("calendar") {
                val context = LocalContext.current
                val dao = remember {
                    DatabaseProvider.getDatabase(context).tripScheduleDao() }
                val viewModel = remember { CalendarViewModel(dao) }
                CalendarScreen(viewModel = viewModel)
            }
            composable("chat") { ChatScreen() }
            composable("profile") { ProfileScreen() }
            composable("notifications") { NotificationScreen() }
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
