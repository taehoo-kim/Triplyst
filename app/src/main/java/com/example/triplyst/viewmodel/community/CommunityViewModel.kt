package com.example.triplyst.viewmodel.community

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.triplyst.data.CommunityRepository
import com.example.triplyst.model.Comment
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

    private val _selectedPost = MutableStateFlow<CommunityPost?>(null)
    val selectedPost: StateFlow<CommunityPost?> = _selectedPost

    private val _comments = MutableStateFlow<List<Comment>>(emptyList())
    val comments: StateFlow<List<Comment>> = _comments

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
                userId = currentUser.uid,
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

//    fun selectPost(post: CommunityPost) {
//        _selectedPost.value = post
//        loadComments(post.id)
//    }

    fun loadComments(postId: String) {
        viewModelScope.launch {
            try {
                _comments.value = repository.getComments(postId)
            } catch (e: Exception) {
                _uiState.value = CommunityUiState.Error("댓글 불러오기 실패")
            }
        }
    }

    fun deleteComment(postId: String, commentId: String) {
        viewModelScope.launch {
            try {
                repository.deleteComment(commentId)
                // 필요하다면 댓글 목록 새로고침
                loadComments(postId)
            } catch (e: Exception) {
                // 에러 처리
            }
        }
    }

    fun submitComment(postId: String, content: String) {
        viewModelScope.launch {
            val user = FirebaseAuth.getInstance().currentUser ?: run {
                _uiState.value = CommunityUiState.Error("로그인 필요")
                return@launch
            }
            val post = repository.getPostById(postId) ?: return@launch
            val comment = Comment(
                postId = postId,
                userId = user.uid,
                author = user.displayName ?: "익명",
                content = content
            )

            try {
                repository.addComment(comment)
                loadComments(postId)

//                // 댓글 알림 트리거
//                if (post.userId != user.uid) {
                    repository.sendCommentNotification(
                        postOwnerId = post.userId,
                        postTitle = post.title,
                        commenterName = user.displayName ?: "익명",
                        comment = content,
                        postId = post.id
                    )
//                }
            } catch (e: Exception) {
                _uiState.value = CommunityUiState.Error("댓글 작성 실패")
            }
        }
    }

    // 상세 화면 전용 실시간 관찰 함수
    fun observePost(postId: String) {
        viewModelScope.launch {
            repository.getPostStream(postId).collect { post ->
                _selectedPost.value = post
            }
        }
    }

    // 좋아요 관련 함수
    fun toggleLike(post: CommunityPost, userId: String) {
        viewModelScope.launch {
            // 낙관적 업데이트: UI 먼저 변경
            val updatedPost = post.copy(
                likes = if (post.userLikes.contains(userId)) post.likes - 1 else post.likes + 1,
                userLikes = if (post.userLikes.contains(userId))
                    post.userLikes - userId
                else
                    post.userLikes + userId
            )
            _selectedPost.value = updatedPost

            try {
                // 서버에 업데이트 요청
                repository.toggleLike(post.id, userId, !post.userLikes.contains(userId))
            } catch (e: Exception) {
                // 실패 시 롤백
                _selectedPost.value = post
                _uiState.value = CommunityUiState.Error("좋아요 처리 실패: ${e.message}")
            }

            if (post.userId != userId) { // 테스트할 땐 빼고 하면 될 듯
                repository.sendLikeNotification(
                    postOwnerId = post.userId,
                    postTitle = post.title,
                    likerName = FirebaseAuth.getInstance().currentUser?.displayName ?: "익명",
                    postId = post.id
                )
            }
        }
    }

}