package com.app.qaimobile.data.remote

import com.app.qaimobile.data.model.network.ConversationSessionDto
import com.app.qaimobile.data.model.network.auth.LoginRequest
import com.app.qaimobile.data.model.network.auth.LoginResponse
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
    @GET("get_conversations")
    suspend fun getConversations(): Response<List<ConversationSessionDto>>
}