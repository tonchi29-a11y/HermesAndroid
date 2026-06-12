package com.hermes.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.hermes.app.viewmodel.ChatUiMessage

@Composable
fun MessageBubble(
    message: ChatUiMessage,
    modifier: Modifier = Modifier,
) {
    val isUser = message.role == "user"
    val bubbleColor = if (isUser) Color(0xFF1B5E20) else Color(0xFF2D2D2D)
    val alignment = if (isUser) androidx.compose.ui.Alignment.End else androidx.compose.ui.Alignment.Start

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 4.dp),
        horizontalAlignment = alignment,
    ) {
        Box(
            modifier = Modifier
                .widthIn(max = 320.dp)
                .clip(RoundedCornerShape(16.dp, 16.dp, if (isUser) 4.dp else 16.dp, if (isUser) 16.dp else 4.dp))
                .background(bubbleColor)
                .padding(12.dp),
        ) {
            Text(
                text = message.content + if (message.isStreaming) " ▌" else "",
                color = Color.White,
                style = MaterialTheme.typography.bodyMedium,
            )
        }
    }
}
