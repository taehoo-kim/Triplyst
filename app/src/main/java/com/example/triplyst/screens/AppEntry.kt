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
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.CompositingStrategy
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.*
import androidx.navigation.navArgument
import com.example.triplyst.data.DatabaseProvider
import com.example.triplyst.screens.calendar.CalendarScreen
import com.example.triplyst.viewmodel.calendar.CalendarViewModel
import com.example.triplyst.screens.chat.ChatScreen
import com.example.triplyst.screens.community.CommunityScreen
import com.example.triplyst.screens.community.components.CommunityPostDetail
import com.example.triplyst.screens.home.HomeScreen
import com.example.triplyst.screens.login.LoginScreen
import com.example.triplyst.screens.notification.NotificationScreen
import com.example.triplyst.screens.profile.ProfileScreen
import com.example.triplyst.viewmodel.community.CommunityViewModel
import com.example.triplyst.viewmodel.notification.NotificationViewModel
import com.google.firebase.auth.FirebaseAuth

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppEntry() {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    // 알림 뱃지 상태 구독 - userId 필요
    val notificationViewModel: NotificationViewModel = hiltViewModel()
    val userId = FirebaseAuth.getInstance().currentUser?.uid ?: ""
    val unreadCount by notificationViewModel.unreadCount.collectAsState()

    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner, userId) {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_START -> {
                    if (userId.isNotEmpty()) {
                        notificationViewModel.observeMyNotifications(userId)
                    }
                }
                Lifecycle.Event.ON_STOP -> {
                    notificationViewModel.cancelObservations()
                }
                else -> {}
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
            notificationViewModel.cancelObservations()
        }
    }

    // 프로필 제외한 탭 목록
    val bottomNavRoutes = listOf("home", "community", "calendar", "chat")

    LaunchedEffect(userId) {
        if (userId.isNotEmpty()) {
            notificationViewModel.observeMyNotifications(userId)
        }
    }

    Scaffold(
        topBar = {
            if (currentRoute != "login") {
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
                            IconButton(onClick = {
                                navController.navigate("notifications/$userId")
                            }) {
                                BadgedBox(
                                    badge = {
                                        if (unreadCount > 0) {
                                            Badge {
                                                Text(
                                                    text = unreadCount.toString(),
                                                    modifier = Modifier
                                                        .graphicsLayer {
                                                            compositingStrategy = CompositingStrategy.Offscreen
                                                        }
                                                )
                                            }
                                        }
                                    }
                                ) {
                                    Icon(Icons.Filled.Notifications, contentDescription = "알림")
                                }
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
            composable("login") {
                LoginScreen(
                    onLoginSuccess = {
                        navController.navigate("home") {
                            popUpTo("login") { inclusive = true }
                        }
                    }
                )

            }
            composable("home") {
                HomeScreen(
                    onAiRecommendClick = { navController.navigate("chat") }
                )
            }
            composable("community") {
                CommunityScreen(navController = navController)
            }
            composable("calendar") {
                val context = LocalContext.current
                val dao = remember {
                    DatabaseProvider.getDatabase(context).tripScheduleDao()
                }
                val viewModel = remember { CalendarViewModel(dao) }
                CalendarScreen(viewModel = viewModel)
            }
            composable("chat") { ChatScreen() }
            composable("profile") {
                ProfileScreen(
                    onLogout = {
                        FirebaseAuth.getInstance().signOut()
                        navController.navigate("login") {
                            popUpTo("login") { inclusive = true }
                            launchSingleTop = true
                        }
                    }
                )
            }
            composable(
                route = "notifications/{userId}",
                arguments = listOf(navArgument("userId") { type = NavType.StringType })
            ) { backStackEntry ->
                val userId = backStackEntry.arguments?.getString("userId") ?: ""
                NotificationScreen(
                    navController = navController,
                    userId = userId
                )
            }
            composable(
                route = "communityPostDetail/{postId}",
                arguments = listOf(navArgument("postId") { type = NavType.StringType })
            ) { backStackEntry ->
                val postId = backStackEntry.arguments?.getString("postId") ?: ""
                val viewModel: CommunityViewModel = hiltViewModel()
                CommunityPostDetail(
                    postId = postId,
                    viewModel = viewModel,
                    navController = navController
                )
            }
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
