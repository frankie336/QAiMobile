package com.app.qaimobile.data.repository

import com.app.qaimobile.data.local.ConversationSessionDao
import com.app.qaimobile.data.local.ConversationSession
import com.app.qaimobile.data.remote.ApiService
import com.app.qaimobile.domain.repository.ConversationRepository
import com.app.qaimobile.util.Result
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ConversationRepositoryImpl @Inject constructor(
    private val conversationSessionDao: ConversationSessionDao,
    private val apiService: ApiService
) : ConversationRepository {

    override val allConversations: Flow<List<ConversationSession>> =
        conversationSessionDao.getAllConversationSessions()

    override suspend fun refreshConversations(userId: String) {
        val response = apiService.getConversations()
        if (response.isSuccessful) {
            response.body()?.let { conversationsFromApi ->
                val conversationEntities = conversationsFromApi.map { dto ->
                    ConversationSession(
                        id = dto.id,
                        threadId = dto.threadId,
                        userId = dto.userId,
                        thread = dto.thread,
                        summary = dto.summary,
                        messages = dto.messages.toString(),
                        appDesignation = dto.appDesignation
                    )
                }
                conversationSessionDao.insertAll(conversationEntities)
            }
        }
    }

    override suspend fun syncConversations(): Result<Unit> {
        return try {
            val userId = "" // obtain the user ID from some source (e.g., data store or session manager)
            refreshConversations(userId)
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }
}
