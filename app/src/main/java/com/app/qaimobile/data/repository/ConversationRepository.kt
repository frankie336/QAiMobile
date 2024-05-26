package com.app.qaimobile.data.repository

import com.app.qaimobile.util.Result

interface ConversationRepository {
    suspend fun syncConversations(): Result<Unit>
}