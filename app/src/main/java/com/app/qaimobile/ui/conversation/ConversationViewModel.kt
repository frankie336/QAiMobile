package com.app.qaimobile.ui.conversation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.qaimobile.data.repository.ConversationRepository
import com.app.qaimobile.util.Result
import kotlinx.coroutines.launch
import javax.inject.Inject

class ConversationViewModel @Inject constructor(
    private val conversationRepository: ConversationRepository
) : ViewModel() {

    private val _syncStatus = MutableLiveData<Result<Unit>>()
    val syncStatus: LiveData<Result<Unit>> = _syncStatus

    fun syncConversations() {
        viewModelScope.launch {
            _syncStatus.value = Result.Loading
            val result = conversationRepository.syncConversations()
            _syncStatus.value = result
        }
    }
}