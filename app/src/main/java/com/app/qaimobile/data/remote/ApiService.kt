package com.app.qaimobile.data.remote

import com.app.qaimobile.data.model.network.*
import com.app.qaimobile.data.model.network.auth.LoginRequest
import com.app.qaimobile.data.model.network.auth.LoginResponse
import com.google.gson.annotations.SerializedName
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

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

    /**
     * This function is used to get the status of a run.
     * @param threadId The thread ID.
     * @return The response containing the run status.
     */
    @GET("/bp_gpt_mobile/api/assistant-run-status")
    suspend fun getRunStatus(@Query("threadId") threadId: String): RunStatusResponse

    /**
     * This function is used to create a new session.
     * @return The response containing the user ID.
     */
    @POST("bp_common/session-create")
    suspend fun createSession(): Response<CreateSessionResponse>

    /**
     * This function is used to update the user's location.
     * @param locationUpdateRequest The location update request object.
     * @return The response indicating the result of the update operation.
     */
    @POST("bp_location/3vzFLKUZLX9vW7ZmNF0zrVVOxJ4AJPV7x8AKatQMX8KCucDanC5SnfyGexElLPc2GUb60iAQvzOT2NTmnrryCHcW31BekpEduXip")
    suspend fun updateLocation(
        @Body locationUpdateRequest: LocationUpdateRequest,
        //@Header("Authorization") token: String
    ): Response<LocationUpdateResponse>

    /**
     * This function is used to get the list of files with metadata.
     * @return The response containing the list of files.
     */
    @GET("bp_vector_store/files")
    suspend fun getFiles(
        //@Header("Authorization") token: String
    ): Response<FileListResponse>


    /**
     * This function is used to delete a file.
     * @param fileId The ID of the file to be deleted.
     * @return The response indicating the result of the delete operation.
     */
    @DELETE("bp_vector_store/files/{fileId}")
    suspend fun deleteFile(
        //@Header("Authorization") token: String,
        @Path("fileId") fileId: String
    ): Response<DeleteFileResponse>




}

/**
 * Data class representing the response from sending a message.
 */
data class SendMessageResponse(
    @SerializedName("conversation_id")
    val conversationId: String,
    @SerializedName("thread_id")
    val threadId: String,
    @SerializedName("messages")
    val messages: List<Message>
)

/**
 * Data class representing the response from getting the run status.
 */
data class RunStatusResponse(
    val status: String
)

/**
 * Data class representing the response from creating a session.
 */
data class CreateSessionResponse(
    val user_id: String
)

/**
 * Data class representing the response from getting the list of files.
 */
data class FileListResponse(
    @SerializedName("files")
    val files: List<FileMetadata>
)

/**
 * Data class representing the response from deleting a file.
 */
data class DeleteFileResponse(
    val message: String
)