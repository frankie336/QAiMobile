package com.app.qaimobile.ui.chat

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.qaimobile.data.model.network.ConversationSessionDto
import com.app.qaimobile.data.model.network.toDto
import com.app.qaimobile.domain.repository.ConversationRepository
import com.app.qaimobile.util.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChatViewModel @Inject constructor(
    private val conversationRepository: ConversationRepository
) : ViewModel() {

    private val _uiEvent = MutableSharedFlow<ChatUiEvent>()
    val uiEvent: SharedFlow<ChatUiEvent> = _uiEvent

    private val _state = MutableStateFlow(ChatState())
    val state: StateFlow<ChatState> = _state

    private val _conversations = MutableStateFlow<List<ConversationSessionDto>>(emptyList())
    val conversations: StateFlow<List<ConversationSessionDto>> = _conversations

    private val _selectedConversationMessages = MutableStateFlow<String?>(null)
    val selectedConversationMessages: StateFlow<String?> = _selectedConversationMessages

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
                val result = conversationRepository.syncConversations()
                when (result) {
                    is Result.Success -> {
                        conversationRepository.allConversations.collect { conversations ->
                            val conversationDtos = conversations.map { it.toDto() }
                            _conversations.value = conversationDtos
                            _uiEvent.emit(ChatUiEvent.ConversationsLoaded(conversationDtos))
                            _state.value = ChatState(isLoading = false) // Set isLoading to false after successful synchronization
                        }
                    }
                    is Result.Error -> {
                        _uiEvent.emit(ChatUiEvent.ShowError("Failed to load conversations: ${result.exception.localizedMessage}"))
                        _state.value = ChatState(isLoading = false) // Set isLoading to false after failed synchronization
                    }
                    is Result.Loading -> {
                        // Handle loading state if needed
                    }
                }
            } catch (e: Exception) {
                _uiEvent.emit(ChatUiEvent.ShowError("Error: ${e.localizedMessage}"))
                _state.value = ChatState(isLoading = false) // Set isLoading to false after exception
            }
        }
    }

    fun selectConversation(sessionId: String) {
        viewModelScope.launch {
            conversationRepository.getConversationSessionById(sessionId).collect { session ->
                _selectedConversationMessages.value = session.messages
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
