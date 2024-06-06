package com.app.qaimobile.ui.chat

sealed class ChatViewModelEvent {
    data class SendMessage(val message: String) : ChatViewModelEvent()
}
