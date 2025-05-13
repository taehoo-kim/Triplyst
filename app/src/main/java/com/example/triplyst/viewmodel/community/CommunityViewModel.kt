package com.example.triplyst.viewmodel.community

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.triplyst.data.CommunityRepository
import com.example.triplyst.model.CommunityPost
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import com.google.firebase.auth.FirebaseAuth

class CommunityViewModel (
    private val repository: CommunityRepository = CommunityRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow<CommunityUiState>(CommunityUiState.Loading)
    val uiState: StateFlow<CommunityUiState> = _uiState

    // 게시글 목록 로드
    fun loadPosts() {
        viewModelScope.launch {
            _uiState.value = CommunityUiState.Loading
            try {
                val posts = repository.getPosts()
                _uiState.value = CommunityUiState.Success(posts)
            } catch (e: Exception) {
                _uiState.value = CommunityUiState.Error(e.message ?: "알 수 없는 오류")
            }
        }
    }

    // 게시글 작성
    fun submitPost(title: String, content: String) {
        viewModelScope.launch {
            val currentUser = FirebaseAuth.getInstance().currentUser
            if (currentUser == null) {
                _uiState.value = CommunityUiState.Error("로그인이 필요합니다")
                return@launch
            }

            val newPost = CommunityPost(
                author = currentUser.displayName ?: "익명",
                title = title,
                content = content
                // createdAt은 기본값으로 Timestamp.now()가 들어감
            )

            try {
                repository.addPost(newPost)
                loadPosts() // 작성 후 목록 새로고침
            } catch (e: Exception) {
                _uiState.value = CommunityUiState.Error(e.message ?: "게시글 작성 실패")
            }
        }
    }

    // 게시글 삭제
    fun deletePost(postId: String) {
        viewModelScope.launch {
            try {
                repository.deletePost(postId)
                loadPosts() // 삭제 후 목록 새로고침
            } catch (e: Exception) {
                _uiState.value = CommunityUiState.Error(e.message ?: "게시글 삭제 실패")
            }
        }
    }
}