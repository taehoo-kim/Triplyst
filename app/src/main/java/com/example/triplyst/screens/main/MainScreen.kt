package com.example.triplyst.screens.main

import androidx.compose.runtime.*
import com.example.triplyst.screens.home.HomeScreen


@Composable
fun MainScreen(onAiRecommendClick: () -> Unit) {
    HomeScreen(onAiRecommendClick = onAiRecommendClick)
}

