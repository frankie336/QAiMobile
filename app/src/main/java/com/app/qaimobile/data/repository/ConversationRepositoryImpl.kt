package com.app.qaimobile.data.repository

import com.app.qaimobile.data.local.ConversationSessionDao
import com.app.qaimobile.data.model.network.ConversationSessionDto
import com.app.qaimobile.data.model.network.toConversationSession
import com.app.qaimobile.data.remote.ApiService
import com.app.qaimobile.util.Result
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class ConversationRepositoryImpl @Inject constructor(
    private val apiService: ApiService,
    private val conversationSessionDao: ConversationSessionDao
) : ConversationRepository {

    override suspend fun syncConversations(): Result<Unit> {
        return try {
            val response = apiService.getConversations()
            if (response.isSuccessful) {
                val conversations = response.body()
                conversations?.let { conversationList: List<ConversationSessionDto> ->
                    withContext(Dispatchers.IO) {
                        conversationList.forEach { conversationDto: ConversationSessionDto ->
                            val conversationSession = conversationDto.toConversationSession()
                            conversationSessionDao.insert(conversationSession)
                        }
                    }
                }
                Result.Success(Unit)
            } else {
                Result.Error(Exception("Failed to sync conversations"))
            }
        } catch (e: Exception) {
            Result.Error(e)
        }
    }
}