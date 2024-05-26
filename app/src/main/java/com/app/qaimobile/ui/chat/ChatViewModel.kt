package com.app.qaimobile.ui.chat

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.qaimobile.data.model.network.ConversationSessionDto
import com.app.qaimobile.data.remote.ApiService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChatViewModel @Inject constructor(
    private val apiService: ApiService
) : ViewModel() {

    private val _uiEvent = MutableSharedFlow<ChatUiEvent>()
    val uiEvent: SharedFlow<ChatUiEvent> = _uiEvent

    // Define state property
    private val _state = MutableStateFlow(ChatState())
    val state: StateFlow<ChatState> = _state

    fun onEvent(event: ChatViewModelEvent) {
        when (event) {
            is ChatViewModelEvent.SendMessage -> {
                // Handle sending message
            }
        }
    }

    fun fetchConversations() {
        viewModelScope.launch {
            _state.value = ChatState(isLoading = true)
            try {
                val response = apiService.getConversations()
                if (response.isSuccessful) {
                    response.body()?.let { conversations ->
                        _uiEvent.emit(ChatUiEvent.ConversationsLoaded(conversations))
                    } ?: run {
                        _uiEvent.emit(ChatUiEvent.ShowError("No conversations found"))
                    }
                } else {
                    _uiEvent.emit(ChatUiEvent.ShowError("Failed to load conversations"))
                }
            } catch (e: Exception) {
                _uiEvent.emit(ChatUiEvent.ShowError("Error: ${e.localizedMessage}"))
            } finally {
                _state.value = ChatState(isLoading = false)
            }
        }
    }
}

sealed class ChatUiEvent {
    data class ShowError(val message: String) : ChatUiEvent()
    data class ConversationsLoaded(val conversations: List<ConversationSessionDto>) : ChatUiEvent()
    object Success : ChatUiEvent()
}

sealed class ChatViewModelEvent {
    data class SendMessage(val message: String) : ChatViewModelEvent()
}

data class ChatState(val isLoading: Boolean = false)
