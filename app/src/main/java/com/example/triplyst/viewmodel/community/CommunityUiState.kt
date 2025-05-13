package com.example.triplyst.viewmodel.community

import com.example.triplyst.model.CommunityPost

sealed class CommunityUiState {
    object Loading : CommunityUiState()
    data class Success(val posts: List<CommunityPost>) : CommunityUiState()
    data class Error(val message: String) : CommunityUiState()
}