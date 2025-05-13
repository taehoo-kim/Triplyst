package com.example.triplyst.screens.profile

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil3.compose.AsyncImage
import com.example.triplyst.viewmodel.profile.ProfileViewModel

@Composable
fun ProfileScreen(
    profileViewModel: ProfileViewModel = viewModel(),
    onLogout: () -> Unit = {}
) {
    val nickname by profileViewModel.nickname.collectAsState()
    val email by profileViewModel.email.collectAsState()
    val preferences by profileViewModel.preferences.collectAsState()
    val travelHistory by profileViewModel.travelHistory.collectAsState()

    var editedNickname by remember { mutableStateOf("") }
    var showNicknameDialog by remember { mutableStateOf(false) }
    var nicknameError by remember { mutableStateOf<String?>(null) }

    var newPreference by remember { mutableStateOf("") }
    var showPreferenceDialog by remember { mutableStateOf(false) }
    var preferenceToEdit by remember { mutableStateOf<String?>(null) }
    var editedPreference by remember { mutableStateOf("") }
    var preferenceToDelete by remember { mutableStateOf<String?>(null) }

    var newTravelPlace by remember { mutableStateOf("") }
    var newTravelYear by remember { mutableStateOf("") }
    var showTravelDialog by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        profileViewModel.loadProfile()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // 프로필 이미지
        AsyncImage(
            model = "https://randomuser.me/api/portraits/men/10.jpg",
            contentDescription = "프로필 이미지",
            modifier = Modifier
                .size(100.dp)
                .clip(CircleShape)
        )
        Spacer(modifier = Modifier.height(16.dp))

        // 닉네임 (수정 버튼)
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(nickname, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            IconButton(onClick = {
                editedNickname = nickname
                showNicknameDialog = true
            }) {
                Icon(Icons.Default.Edit, contentDescription = "닉네임 수정")
            }
        }
        if (nicknameError != null) {
            Text(nicknameError!!, color = MaterialTheme.colorScheme.error)
        }
        Spacer(modifier = Modifier.height(8.dp))

        // 이메일
        Text(email, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Spacer(modifier = Modifier.height(24.dp))

        // 선호도
        Text("선호 여행 스타일", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
        Row(
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxWidth()
        ) {
            preferences.forEach { pref ->
                AssistChip(
                    onClick = {
                        preferenceToEdit = pref
                        editedPreference = pref },
                    label = { Text(pref) },
                    trailingIcon = {
                        IconButton(
                            onClick = { preferenceToDelete = pref },
                            modifier = Modifier.size(18.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "삭제"
                            )
                        }
                    },
                    modifier = Modifier.padding(4.dp)
                )
            }
            IconButton(onClick = { showPreferenceDialog = true }) {
                Icon(Icons.Default.Add, contentDescription = "선호도 추가")
            }
        }
        Spacer(modifier = Modifier.height(24.dp))

        // 여행 이력
        Text("여행 이력", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
        travelHistory.forEach { history ->
            val place = history["place"] as? String ?: ""
            val year = history["year"]?.toString() ?: ""
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("• $place $year", style = MaterialTheme.typography.bodyMedium)
                IconButton(
                    onClick = {
                        profileViewModel.removeTravelHistory(history)
                    }
                ) {
                    Icon(Icons.Default.Close, contentDescription = "삭제")
                }
            }
        }
        Button(onClick = { showTravelDialog = true }, modifier = Modifier.padding(top = 8.dp)) {
            Text("여행 추가")
        }
        Spacer(modifier = Modifier.height(32.dp))

        // 로그아웃 버튼
        Button(
            onClick = onLogout,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("로그아웃")
        }
    }

    // 닉네임 수정 다이얼로그
    if (showNicknameDialog) {
        AlertDialog(
            onDismissRequest = { showNicknameDialog = false },
            title = { Text("닉네임 변경") },
            text = {
                OutlinedTextField(
                    value = editedNickname,
                    onValueChange = { editedNickname = it },
                    label = { Text("새 닉네임") }
                )
            },
            confirmButton = {
                Button(onClick = {
                    profileViewModel.updateNickname(editedNickname) { success, errorMsg ->
                        if (success) {
                            showNicknameDialog = false
                            nicknameError = null
                        } else {
                            nicknameError = errorMsg
                        }
                    }
                }) { Text("저장") }
            },
            dismissButton = {
                TextButton(onClick = { showNicknameDialog = false }) { Text("취소") }
            }
        )
    }

    // 선호도 추가 다이얼로그
    if (showPreferenceDialog) {
        AlertDialog(
            onDismissRequest = { showPreferenceDialog = false },
            title = { Text("선호도 추가") },
            text = {
                OutlinedTextField(
                    value = newPreference,
                    onValueChange = { newPreference = it },
                    label = { Text("선호도") }
                )
            },
            confirmButton = {
                Button(onClick = {
                    val updated = preferences + newPreference
                    profileViewModel.updatePreferences(updated)
                    newPreference = ""
                    showPreferenceDialog = false
                }) { Text("추가") }
            },
            dismissButton = {
                TextButton(onClick = { showPreferenceDialog = false }) { Text("취소") }
            }
        )
    }

    // 수정 다이얼로그
    if (preferenceToEdit != null) {
        AlertDialog(
            onDismissRequest = { preferenceToEdit = null },
            title = { Text("선호도 수정") },
            text = {
                OutlinedTextField(
                    value = editedPreference,
                    onValueChange = { editedPreference = it },
                    label = { Text("선호도") }
                )
            },
            confirmButton = {
                Button(onClick = {
                    val updated = preferences.map { if (it == preferenceToEdit) editedPreference else it }
                    profileViewModel.updatePreferences(updated)
                    preferenceToEdit = null
                }) { Text("저장") }
            },
            dismissButton = {
                TextButton(onClick = { preferenceToEdit = null }) { Text("취소") }
            }
        )
    }

    // 삭제 확인 다이얼로그
    if (preferenceToDelete != null) {
        AlertDialog(
            onDismissRequest = { preferenceToDelete = null },
            title = { Text("선호도 삭제") },
            text = { Text("\"${preferenceToDelete}\" 선호도를 삭제하시겠습니까?") },
            confirmButton = {
                Button(onClick = {
                    val updated = preferences - preferenceToDelete!!
                    profileViewModel.updatePreferences(updated)
                    preferenceToDelete = null
                }) { Text("삭제") }
            },
            dismissButton = {
                TextButton(onClick = { preferenceToDelete = null }) { Text("취소") }
            }
        )
    }

    // 여행 이력 추가 다이얼로그
    if (showTravelDialog) {
        AlertDialog(
            onDismissRequest = { showTravelDialog = false },
            title = { Text("여행 이력 추가") },
            text = {
                Column {
                    OutlinedTextField(
                        value = newTravelPlace,
                        onValueChange = { newTravelPlace = it },
                        label = { Text("장소") }
                    )
                    OutlinedTextField(
                        value = newTravelYear,
                        onValueChange = { newTravelYear = it.filter { c -> c.isDigit() } },
                        label = { Text("연도") }
                    )
                }
            },
            confirmButton = {
                Button(onClick = {
                    val year = newTravelYear.toIntOrNull()
                    if (newTravelPlace.isNotBlank() && year != null) {
                        profileViewModel.addTravelHistory(newTravelPlace, year)
                        newTravelPlace = ""
                        newTravelYear = ""
                        showTravelDialog = false
                    }
                }) { Text("추가") }
            },
            dismissButton = {
                TextButton(onClick = { showTravelDialog = false }) { Text("취소") }
            }
        )
    }
}
