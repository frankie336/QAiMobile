package com.app.qaimobile.data.model.network

import com.app.qaimobile.data.local.ConversationSession

data class ConversationSessionDto(
    val id: String,
    val threadId: String?,
    val userId: String,
    val thread: String?,
    val summary: String?,
    val messages: String?,
    val appDesignation: String?
)

fun ConversationSessionDto.toEntity(): ConversationSession {
    return ConversationSession(
        id = id,
        threadId = threadId,
        userId = userId,
        thread = thread,
        summary = summary,
        messages = messages ?: "[]",
        appDesignation = appDesignation
    )
}

fun ConversationSession.toDto(): ConversationSessionDto {
    return ConversationSessionDto(
        id = id,
        threadId = threadId,
        userId = userId,
        thread = thread,
        summary = summary,
        messages = messages,
        appDesignation = appDesignation
    )
}