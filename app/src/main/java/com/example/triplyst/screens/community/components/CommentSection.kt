package com.example.triplyst.screens.community.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.triplyst.model.Comment


@Composable
fun CommentSection(
    comments: List<Comment>,
    onSubmitComment: (String) -> Unit
) {
    var commentText by remember { mutableStateOf("") }

    Column(Modifier.padding(16.dp)) {
        Text("댓글", style = MaterialTheme.typography.titleMedium)
        OutlinedTextField(
            value = commentText,
            onValueChange = { commentText = it },
            label = { Text("댓글 작성") },
            modifier = Modifier.fillMaxWidth(),
            trailingIcon = {
                IconButton(
                    onClick = {
                        onSubmitComment(commentText)
                        commentText = ""
                    },
                    enabled = commentText.isNotBlank()
                ) {
                    Icon(Icons.Default.Send, "댓글 등록")
                }
            }
        )
        LazyColumn(Modifier.padding(top = 8.dp)) {
            items(comments) { comment ->
                CommentItem(comment = comment)
            }
        }
    }
}
