package com.app.qaimobile.repository

import android.util.Log
import com.app.qaimobile.data.local.ConversationSession
import com.app.qaimobile.data.local.ConversationSessionDao
import com.app.qaimobile.data.remote.ApiService
import com.app.qaimobile.domain.repository.ConversationRepository
import com.app.qaimobile.util.Result
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ConversationRepositoryImpl @Inject constructor(
    private val conversationSessionDao: ConversationSessionDao,
    private val apiService: ApiService
) : ConversationRepository {

    override val allConversations: Flow<List<ConversationSession>> =
        conversationSessionDao.getAllConversationSessions()

    override suspend fun refreshConversations(userId: String) {
        if (userId.isBlank()) {
            throw IllegalArgumentException("UserId cannot be blank")
        }

        val response = apiService.getConversations()
        if (response.isSuccessful) {
            response.body()?.let { conversationsFromApi ->
                val conversationEntities = conversationsFromApi.map { dto ->
                    Log.d("ConversationRepository", "Received conversation: $dto")
                    ConversationSession(
                        id = dto.id,
                        threadId = dto.threadId,
                        userId = dto.userId ?: userId, // Use the provided userId if the dto.userId is null
                        thread = dto.thread,
                        summary = dto.summary,
                        messages = dto.messages,
                        appDesignation = dto.appDesignation
                    )
                }
                Log.d("ConversationRepository", "Inserting conversations: $conversationEntities")
                conversationSessionDao.insertAll(conversationEntities)
            }
        } else {
            Log.e("ConversationRepository", "Failed to fetch conversations: ${response.errorBody()?.string()}")
        }
    }

    override suspend fun syncConversations(): Result<Unit> {
        return try {
            val userId = getUserId() // Implement a method to obtain the user ID from some source (e.g., data store or session manager)
            if (userId.isNullOrEmpty()) {
                throw IllegalArgumentException("UserId cannot be null or empty")
            }
            refreshConversations(userId)
            Log.d("ConversationRepository", "Conversations synced successfully")
            Result.Success(Unit)
        } catch (e: Exception) {
            Log.e("ConversationRepository", "Error syncing conversations", e)
            Result.Error(e)
        }
    }

    override fun getConversationSessionById(sessionId: String): Flow<ConversationSession> {
        return conversationSessionDao.getConversationSessionById(sessionId)
    }

    private fun getUserId(): String {
        // Implement this method to obtain the user ID
        return "c8475de4-e267-4ef7-bf76-f51138721727" // Example user ID, replace with actual logic
    }
}
