package com.app.qaimobile.data.remote

import com.google.gson.annotations.SerializedName

/**
 * Data class representing the request to send a message.
 */
data class SendMessageRequest(
    val conversationId: String,
    val message: String,
    val personality: String,
    val selectedModel: String,
    val latitude: Double?,
    val longitude: Double?,
    val altitude: Double?,
    val permissionStatus: String
)
