package com.app.qaimobile.data.model.network

import com.app.qaimobile.data.local.ConversationSession

data class ConversationSessionDto(
    val id: String,
    val threadId: String?,
    val userId: String,
    val thread: String?,
    val summary: String?,
    val messages: List<String>,
    val appDesignation: String?
)

fun ConversationSessionDto.toConversationSession(): ConversationSession {
    return ConversationSession(
        id = id,
        threadId = threadId,
        userId = userId,
        thread = thread,
        summary = summary,
        messages = messages.toString(),
        appDesignation = appDesignation
    )
}