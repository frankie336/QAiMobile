package com.app.qaimobile.ui.chat

import com.app.qaimobile.data.model.network.ConversationSessionDto

sealed class ChatUiEvent {
    data class ShowError(val message: String) : ChatUiEvent()
    data class ConversationsLoaded(val conversations: List<ConversationSessionDto>) : ChatUiEvent()
    data class SelectConversation(val conversationId: String) : ChatUiEvent()
    data class SendMessage(val conversationId: String, val message: String) : ChatUiEvent() // Updated this line to include conversationId
    object Success : ChatUiEvent()
}
