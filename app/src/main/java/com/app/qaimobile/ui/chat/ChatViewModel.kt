package com.app.qaimobile.ui.chat

import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.qaimobile.data.datastore.DataStoreManager
import com.app.qaimobile.data.datastore.PreferencesKeys
import com.app.qaimobile.data.model.network.ConversationSessionDto
import com.app.qaimobile.data.model.network.Message
import com.app.qaimobile.data.model.network.MessageContent
import com.app.qaimobile.data.model.network.MessageText
import com.app.qaimobile.data.model.network.toDto
import com.app.qaimobile.data.model.network.toMessageList
import com.app.qaimobile.data.remote.ApiService
import com.app.qaimobile.data.remote.SendMessageRequest
import com.app.qaimobile.domain.repository.ConversationRepository
import com.app.qaimobile.util.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChatViewModel @Inject constructor(
    private val conversationRepository: ConversationRepository,
    private val dataStoreManager: DataStoreManager,
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

    private val _selectedModel = MutableStateFlow<String>("3.5") // Default model
    val selectedModel: StateFlow<String> = _selectedModel.asStateFlow()

    fun onEvent(event: ChatUiEvent) {
        when (event) {
            is ChatUiEvent.ShowError -> {
                // Handle error events
            }
            is ChatUiEvent.ConversationsLoaded -> {
                _conversations.value = event.conversations
            }
            is ChatUiEvent.SelectConversation -> {
                viewModelScope.launch {
                    refreshConversations()
                    dataStoreManager.dataStore.edit { preferences ->
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
                // Handle success events
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
                            _state.value = ChatState(isLoading = false)
                        }
                    }
                    is Result.Error -> {
                        _uiEvent.emit(ChatUiEvent.ShowError("Failed to load conversations: ${result.exception.localizedMessage}"))
                        _state.value = ChatState(isLoading = false)
                    }
                    is Result.Loading -> {
                        // Handle loading state if needed
                    }
                }
            } catch (e: Exception) {
                _uiEvent.emit(ChatUiEvent.ShowError("Error: ${e.localizedMessage}"))
                _state.value = ChatState(isLoading = false)
            }
        }
    }

    private fun selectConversation(sessionId: String) {
        viewModelScope.launch {
            conversationRepository.getConversationSessionById(sessionId).collect { session ->
                Log.d("ChatViewModel", "Selected conversation with threadId: ${session.threadId}")
                val messages = session.messages.toMessageList().sortedBy { it.createdAt }
                _selectedConversationMessages.value = messages
            }
        }
    }

    private fun sendMessage(conversationId: String, message: String) {
        viewModelScope.launch {
            // Create a new user message object
            val userMessage = Message(
                id = generateId(),
                assistantId = null,
                attachments = emptyList(),
                completedAt = null,
                content = listOf(
                    MessageContent(
                        text = MessageText(
                            annotations = emptyList(),
                            value = message
                        ),
                        type = "text"
                    )
                ),
                createdAt = System.currentTimeMillis(),
                incompleteAt = null,
                incompleteDetails = null,
                metadata = emptyMap(),
                objectType = "thread.message",
                role = "user",
                runId = null,
                status = null,
                threadId = conversationId
            )

            // Update UI immediately with user's message
            _selectedConversationMessages.update { it?.plus(userMessage) }

            try {
                // Retrieve the actual string values from the Flow objects
                val personality = dataStoreManager.getPersonality().firstOrNull() ?: ""
                val selectedModel = dataStoreManager.getSelectedModel().firstOrNull() ?: "3.5"

                // Create the SendMessageRequest with the string values
                val request = SendMessageRequest(conversationId, message, personality, selectedModel)

                // Send the message to the server
                val response = apiService.sendMessage(request)

                if (response.isSuccessful) {
                    // If the response is successful, retrieve the assistant's message
                    val assistantMessage = response.body()?.assistantMessage

                    if (assistantMessage != null) {
                        // Update UI with assistant's message
                        _selectedConversationMessages.update { it?.plus(assistantMessage) }
                        _uiEvent.emit(ChatUiEvent.Success)
                    } else {
                        _uiEvent.emit(ChatUiEvent.ShowError("Failed to receive assistant's message"))
                    }
                } else {
                    _uiEvent.emit(ChatUiEvent.ShowError("Failed to send message: ${response.message()}"))
                }
            } catch (e: Exception) {
                _uiEvent.emit(ChatUiEvent.ShowError("Error: ${e.localizedMessage}"))
            }
        }
    }

    private fun refreshConversations() {
        viewModelScope.launch {
            try {
                conversationRepository.refreshConversations("userId") // Pass the userId as needed
            } catch (e: Exception) {
                _uiEvent.emit(ChatUiEvent.ShowError("Error: ${e.localizedMessage}"))
            }
        }
    }

    private fun generateId(): String {
        return java.util.UUID.randomUUID().toString()
    }

    fun saveSelectedModel(model: String) {
        viewModelScope.launch {
            _selectedModel.value = model
            dataStoreManager.saveSelectedModel(model)
        }
    }

    init {
        viewModelScope.launch {
            // Retrieve the selected model from DataStore
            val savedModel = dataStoreManager.getSelectedModel()
            savedModel?.let {
                _selectedModel.value = it.toString()
            }
        }
    }
}