package com.app.qaimobile.ui.composables

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.ClickableText
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.unit.dp
import com.app.qaimobile.data.model.network.Message
import com.app.qaimobile.util.parseMarkdownContent

@Composable
fun ChatBubble(message: Message) {
    val isUser = message.role == "user"
    val bubbleColor = if (isUser) Color(0xFFD0EBFE) else Color(0xFFE1F5FE)
    val padding = if (isUser) PaddingValues(start = 40.dp, end = 8.dp, top = 8.dp, bottom = 8.dp) else PaddingValues(start = 8.dp, end = 40.dp, top = 8.dp, bottom = 8.dp)
    val context = LocalContext.current

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(padding),
        horizontalArrangement = if (isUser) Arrangement.End else Arrangement.Start
    ) {
        Box(
            modifier = Modifier
                .background(color = bubbleColor, shape = RoundedCornerShape(8.dp))
                .border(
                    width = 1.dp,
                    brush = Brush.linearGradient(
                        colors = listOf(
                            Color(0xFFB0BEC5), // Light metallic color
                            Color(0xFF78909C)  // Darker metallic color
                        )
                    ),
                    shape = RoundedCornerShape(8.dp)
                )
                .padding(8.dp)
                .padding(vertical = 4.dp)
        ) {
            SelectionContainer {
                Column {
                    Text(
                        text = if (isUser) "You:" else "Assistant:",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Black
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    message.content.forEach { content ->
                        val text = content.text?.value ?: ""
                        val imageUrl = extractImageUrl(text)
                        Log.d("ChatBubble", "Text: $text, Image URL: $imageUrl")
                        if (imageUrl != null) {
                            ImageFromUrl(url = imageUrl, modifier = Modifier.fillMaxWidth().height(200.dp))
                            ClickableText(
                                text = parseMarkdownContent(text.replace(imageUrl, "").trim()),
                                onClick = { offset ->
                                    handleUrlClicks(context, parseMarkdownContent(text.replace(imageUrl, "").trim()), offset)
                                },
                                style = MaterialTheme.typography.bodyMedium.copy(color = Color.Black)
                            )
                        } else {
                            val annotatedString = parseMarkdownContent(text)
                            ClickableText(
                                text = annotatedString,
                                style = MaterialTheme.typography.bodyMedium,
                                onClick = { offset ->
                                    annotatedString.getStringAnnotations(tag = "URL", start = offset, end = offset)
                                        .firstOrNull()?.let { annotation ->
                                            handleUrlClicks(context, annotatedString, offset)
                                        }
                                }
                            )
                        }
                    }
                    message.attachments.forEach { attachment ->
                        Log.d("ChatBubble", "Attachment URL: ${attachment.url}, Type: ${attachment.type}")
                        if (attachment.type == "image") {
                            ImageFromUrl(url = attachment.url, modifier = Modifier
                                .fillMaxWidth()
                                .height(200.dp))
                        }
                    }
                }
            }
        }
    }
}

private fun handleUrlClicks(context: Context, text: AnnotatedString, offset: Int) {
    text.getStringAnnotations(tag = "URL", start = offset, end = offset)
        .firstOrNull()?.let { annotation ->
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(annotation.item))
            context.startActivity(intent)
        }
}

fun extractImageUrl(text: String): String? {
    val regex = """!\[.*?\]\((.*?)\)""".toRegex()
    val matchResult = regex.find(text)
    return matchResult?.groups?.get(1)?.value
}
