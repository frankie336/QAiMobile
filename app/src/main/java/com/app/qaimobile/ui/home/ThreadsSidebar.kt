package com.app.qaimobile.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.app.qaimobile.data.local.ConversationSession
import com.app.qaimobile.ui.composables.ThreadItem

@Composable
fun ThreadsSidebar(
    conversations: List<ConversationSession>,
    onThreadClick: (String) -> Unit = {},
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier
            .fillMaxHeight()
            .width(200.dp)
            .background(color = MaterialTheme.colorScheme.surface)
            .border(1.dp, Color.LightGray, androidx.compose.foundation.shape.RoundedCornerShape(0.dp))
    ) {
        items(conversations) { conversation ->
            ThreadItem(
                summary = conversation.summary ?: "",
                onThreadClick = onThreadClick,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}
