package com.example.triplyst

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.triplyst.screens.AppEntry
import androidx.activity.enableEdgeToEdge
import com.example.triplyst.ui.theme.TriplystTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        enableEdgeToEdge()
//        앱이 전체 화면에 배치되어야 한다고
//        자동으로 선언하고 시스템 막대의 색상 조정

        setContent {
            TriplystTheme {
                AppEntry()
            }
        }
    }
}