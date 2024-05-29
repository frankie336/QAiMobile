package com.app.qaimobile.ui.chat

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.qaimobile.data.datastore.PreferencesKeys
import com.app.qaimobile.data.model.network.ConversationSessionDto
import com.app.qaimobile.data.model.network.Message
import com.app.qaimobile.data.model.network.toDto
import com.app.qaimobile.data.model.network.toMessageList
import com.app.qaimobile.data.remote.ApiService
import com.app.qaimobile.data.remote.SendMessageRequest
import com.app.qaimobile.util.Result
import com.app.qaimobile.domain.repository.ConversationRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit

@HiltViewModel
class ChatViewModel @Inject constructor(
    private val conversationRepository: ConversationRepository,
    private val dataStore: DataStore<Preferences>,
    private val apiService: ApiService
) : ViewModel() {

    private val _uiEvent = MutableSharedFlow<ChatUiEvent>()
    val uiEvent: SharedFlow<ChatUiEvent> = _uiEvent

    private val _state = MutableStateFlow(ChatState())
    val state: StateFlow<ChatState> = _state

    private val _conversations = MutableStateFlow<List<ConversationSessionDto>>(emptyList())
    val conversations: StateFlow<List<ConversationSessionDto>> = _conversations

    private val _selectedConversationMessages = MutableStateFlow<List<Message>?>(null)
    val selectedConversationMessages: StateFlow<List<Message>?> = _selectedConversationMessages

    fun onEvent(event: ChatUiEvent) {
        when (event) {
            is ChatUiEvent.ShowError -> {
                // Handle error
            }
            is ChatUiEvent.ConversationsLoaded -> {
                _conversations.value = event.conversations
            }
            is ChatUiEvent.SelectConversation -> {
                viewModelScope.launch {
                    dataStore.edit { preferences ->
                        preferences[PreferencesKeys.SELECTED_CONVERSATION_ID] = event.conversationId
                    }
                    Log.d("ChatViewModel", "Active Conversation ID: ${event.conversationId}")
                    selectConversation(event.conversationId)
                }
            }
            is ChatUiEvent.SendMessage -> {
                sendMessage(event.conversationId, event.message)
            }
            is ChatUiEvent.Success -> {
                // Handle success
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

    private fun selectConversation(sessionId: String) {
        viewModelScope.launch {
            conversationRepository.getConversationSessionById(sessionId).collect { session ->
                Log.d("ChatViewModel", "Selected conversation with threadId: ${session.threadId}")
                val messages = session.messages.toMessageList().sortedBy { it.createdAt } // Sort messages by created_at
                _selectedConversationMessages.value = messages
            }
        }
    }

    private fun sendMessage(conversationId: String, message: String) {
        viewModelScope.launch {
            try {
                val request = SendMessageRequest(conversationId, message)
                val response = apiService.sendMessage(request)
                if (response.isSuccessful) {
                    _uiEvent.emit(ChatUiEvent.Success)
                } else {
                    _uiEvent.emit(ChatUiEvent.ShowError("Failed to send message: ${response.message()}"))
                }
            } catch (e: Exception) {
                _uiEvent.emit(ChatUiEvent.ShowError("Error: ${e.localizedMessage}"))
            }
        }
    }
}
