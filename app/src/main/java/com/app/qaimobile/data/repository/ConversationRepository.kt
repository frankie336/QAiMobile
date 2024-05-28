package com.app.qaimobile.domain.repository

import com.app.qaimobile.data.local.ConversationSession
import com.app.qaimobile.util.Result
import kotlinx.coroutines.flow.Flow

interface ConversationRepository {
    val allConversations: Flow<List<ConversationSession>>
    suspend fun refreshConversations(userId: String)
    suspend fun syncConversations(): Result<Unit>
    fun getConversationSessionById(sessionId: String): Flow<ConversationSession> // Add this line
}
