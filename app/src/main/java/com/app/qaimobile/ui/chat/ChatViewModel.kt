package com.app.qaimobile.ui.chat

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class ChatViewModel : ViewModel() {
    private val _uiEvent = MutableSharedFlow<ChatUiEvent>()
    val uiEvent: SharedFlow<ChatUiEvent> = _uiEvent

    // Define state property
    private val _state = MutableStateFlow(ChatState())
    val state: StateFlow<ChatState> = _state

    fun onEvent(event: ChatViewModelEvent) {
        // Handle events and update uiEvent accordingly
    }
}

sealed class ChatUiEvent {
    data class ShowError(val message: String) : ChatUiEvent()
    object Success : ChatUiEvent()
}

sealed class ChatViewModelEvent {
    data class SendMessage(val message: String) : ChatViewModelEvent()
}

data class ChatState(val isLoading: Boolean = false)