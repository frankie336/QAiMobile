package com.app.qaimobile.data.remote

import com.app.qaimobile.data.model.network.ConversationSessionDto
import com.app.qaimobile.data.model.network.auth.LoginRequest
import com.app.qaimobile.data.model.network.auth.LoginResponse
import com.app.qaimobile.data.model.network.Message
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

/**
 * ApiService is an interface that defines the endpoints of the API.
 */
interface ApiService {

    /**
     * This function is used to login the user.
     * @param loginRequest The user object containing the user credentials.
     * @return The response containing the user object.
     */
    @POST("bp_auth/login")
    suspend fun login(@Body loginRequest: LoginRequest): Response<LoginResponse>

    /**
     * This function is used to get the user's conversation sessions.
     * @return The response containing the list of conversation sessions.
     */
    @GET("bp_mobile_database/get_conversations")
    suspend fun getConversations(): Response<List<ConversationSessionDto>>

    /**
     * This function is used to send a message to a conversation.
     * @param sendMessageRequest The request object containing the conversation ID and message.
     * @return The response indicating the result of the send operation.
     */
    @POST("bp_gpt_mobile/send_message")
    suspend fun sendMessage(@Body sendMessageRequest: SendMessageRequest): Response<SendMessageResponse>
}

/**
 * Data class representing the request to send a message.
 */
data class SendMessageRequest(
    val conversationId: String,
    val message: String
)

/**
 * Data class representing the response from sending a message.
 */
data class SendMessageResponse(
    val assistantMessage: Message?
)
