package com.app.qaimobile.ui.chat

import android.util.Log
import androidx.datastore.preferences.core.edit
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.qaimobile.util.Constants.DEFAULT_MODEL
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

    private val _selectedModel = MutableStateFlow<String>(DEFAULT_MODEL) // Default model
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
                    Log.d("ChatViewModel", "SelectConversation event received: ${event.conversationId}")
                    refreshConversations()
                    dataStoreManager.dataStore.edit { preferences ->
                        preferences[PreferencesKeys.SELECTED_CONVERSATION_ID] = event.conversationId
                    }
                    Log.d("ChatViewModel", "Active Conversation ID set: ${event.conversationId}")
                    selectConversation(event.conversationId)
                }
            }
            is ChatUiEvent.SendMessage -> {
                Log.d("ChatViewModel", "SendMessage event received: ${event.message}")
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
                            Log.d("ChatViewModel", "Conversations fetched and loaded: ${_conversations.value}")
                        }
                    }
                    is Result.Error -> {
                        _uiEvent.emit(ChatUiEvent.ShowError("Failed to load conversations: ${result.exception.localizedMessage}"))
                        _state.value = ChatState(isLoading = false)
                        Log.e("ChatViewModel", "Error loading conversations: ${result.exception}")
                    }
                    is Result.Loading -> {
                        // Handle loading state if needed
                    }
                }
            } catch (e: Exception) {
                _uiEvent.emit(ChatUiEvent.ShowError("Error: ${e.localizedMessage}"))
                _state.value = ChatState(isLoading = false)
                Log.e("ChatViewModel", "Exception loading conversations: ${e.localizedMessage}")
            }
        }
    }

    private fun selectConversation(sessionId: String) {
        viewModelScope.launch {
            conversationRepository.getConversationSessionById(sessionId).collect { session ->
                if (session != null) { // Check if session is not null
                    Log.d("ChatViewModel", "Selected conversation with threadId: ${session.threadId}")
                    val messages = session.messages.toMessageList().sortedBy { it.createdAt }
                    _selectedConversationMessages.value = messages
                } else {
                    Log.d("ChatViewModel", "Conversation session is null for sessionId: $sessionId")
                    _selectedConversationMessages.value = emptyList()
                }
            }
        }
    }

    private fun sendMessage(conversationId: String?, message: String) {


        val modelMapping = mapOf(
            "gpt-4o" to "4o",
            "gpt-4-turbo-2024-04-09" to "4",
            "gpt-3.5-turbo" to "3.5"
        )

        viewModelScope.launch {
            val finalConversationId = conversationId ?: generateId()

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
                threadId = finalConversationId
            )

            // Update UI immediately with user's message
            _selectedConversationMessages.update { it?.plus(userMessage) }

            try {
                // Retrieve the actual string values from the Flow objects
                val personality = dataStoreManager.getPersonality().firstOrNull() ?: ""
                val selectedModel = dataStoreManager.getSelectedModel().firstOrNull() ?: DEFAULT_MODEL

                // Create the SendMessageRequest with the string values
                val request = SendMessageRequest(finalConversationId, message, personality, selectedModel)

                // Send the message to the server
                val response = apiService.sendMessage(request)

                if (response.isSuccessful) {
                    val responseBody = response.body()
                    if (responseBody != null) {
                        val receivedConversationId = responseBody.conversationId
                        val receivedThreadId = responseBody.threadId

                        if (!receivedConversationId.isNullOrEmpty() && !receivedThreadId.isNullOrEmpty()) {
                            Log.d("ChatViewModel", "Received conversation_id: $receivedConversationId, thread_id: $receivedThreadId")
                            dataStoreManager.dataStore.edit { preferences ->
                                preferences[PreferencesKeys.SELECTED_CONVERSATION_ID] = receivedConversationId
                            }

                            // Emit a SelectConversation event with the received conversation ID
                            _uiEvent.emit(ChatUiEvent.SelectConversation(receivedConversationId))

                            // Update selectedConversationMessages with the response
                            selectConversation(receivedConversationId)
                        } else {
                            Log.d("ChatViewModel", "Conversation ID or Thread ID is null or empty")
                        }

                        refreshConversations()
                        _uiEvent.emit(ChatUiEvent.Success)
                    } else {
                        Log.d("ChatViewModel", "Response body is null")
                    }
                } else {
                    _uiEvent.emit(ChatUiEvent.ShowError("Failed to send message: ${response.message()}"))
                }
            } catch (e: Exception) {
                _uiEvent.emit(ChatUiEvent.ShowError("Error: ${e.localizedMessage}"))
            }

            if (conversationId == null) {
                dataStoreManager.dataStore.edit { preferences ->
                    preferences[PreferencesKeys.SELECTED_CONVERSATION_ID] = finalConversationId
                    Log.d("ChatViewModel", "Conversation ID or Thread ID is null or empty")
                }
            }
        }
    }

    private fun refreshConversations() {
        viewModelScope.launch {
            try {
                conversationRepository.refreshConversations("userId") // Pass the userId as needed
                Log.d("ChatViewModel", "Conversations refreshed")
            } catch (e: Exception) {
                _uiEvent.emit(ChatUiEvent.ShowError("Error: ${e.localizedMessage}"))
                Log.e("ChatViewModel", "Error refreshing conversations: ${e.localizedMessage}")
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

    fun createSession() {
        viewModelScope.launch {
            dataStoreManager.dataStore.edit { preferences ->
                preferences.remove(PreferencesKeys.SELECTED_CONVERSATION_ID)
            }
            _selectedConversationMessages.value = emptyList()
            _uiEvent.emit(ChatUiEvent.Success)
            Log.d("ChatViewModel", "New session created, conversation ID cleared")
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
