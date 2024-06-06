package com.app.qaimobile.data.model.network

import com.google.gson.Gson
import com.google.gson.annotations.SerializedName

data class MessageContent(
    @SerializedName("text")
    val text: MessageText,
    @SerializedName("type")
    val type: String
)

data class MessageText(
    @SerializedName("annotations")
    val annotations: List<Any>,
    @SerializedName("value")
    val value: String
)

data class Message(
    @SerializedName("id")
    val id: String,
    @SerializedName("assistant_id")
    val assistantId: String?,
    @SerializedName("attachments")
    val attachments: List<Any>,
    @SerializedName("completed_at")
    val completedAt: Long?,
    @SerializedName("content")
    val content: List<MessageContent>,
    @SerializedName("created_at")
    val createdAt: Long,
    @SerializedName("incomplete_at")
    val incompleteAt: Long?,
    @SerializedName("incomplete_details")
    val incompleteDetails: Any?,
    @SerializedName("metadata")
    val metadata: Map<String, Any>,
    @SerializedName("object")
    val objectType: String,
    @SerializedName("role")
    val role: String,
    @SerializedName("run_id")
    val runId: String?,
    @SerializedName("status")
    val status: String?,
    @SerializedName("thread_id")
    val threadId: String
) {
    sealed class Content {
        data class Text(val value: String) : Content()
        // Add other content types if needed, e.g., Image, File, etc.
    }
}

fun String.toMessageList(): List<Message> {
    return Gson().fromJson(this, Array<Message>::class.java).toList()
}
