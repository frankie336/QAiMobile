package com.app.qaimobile.ui.chat

import com.app.qaimobile.data.model.network.ConversationSessionDto

/**
 * Sealed class representing the different UI events that can occur in the chat functionality.
 */
sealed class ChatUiEvent {

    /**
     * Event indicating an error occurred.
     * @param message The error message.
     */
    data class ShowError(val message: String) : ChatUiEvent()

    /**
     * Event indicating that conversations are loaded.
     * @param conversations The list of loaded conversations.
     */
    data class ConversationsLoaded(val conversations: List<ConversationSessionDto>) : ChatUiEvent()

    /**
     * Event indicating that a conversation is selected.
     * @param conversationId The ID of the selected conversation.
     */
    data class SelectConversation(val conversationId: String) : ChatUiEvent()

    /**
     * Event indicating that a message is sent.
     * @param conversationId The ID of the conversation to which the message belongs.
     * @param message The text of the message.
     */
    data class SendMessage(val conversationId: String, val message: String) : ChatUiEvent()

    /**
     * Event indicating a successful operation.
     */
    object Success : ChatUiEvent()
}