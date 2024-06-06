package com.app.qaimobile.ui.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import com.app.qaimobile.data.remote.RetrofitInstance
import javax.inject.Inject

@HiltViewModel
class RunStatusViewModel @Inject constructor() : ViewModel() {
    private val _status = MutableStateFlow("idle")
    val status: StateFlow<String> = _status

    suspend fun fetchRunStatus(threadId: String): Boolean {
        var isCompleted = false
        try {
            Log.d("RunStatusViewModel", "Fetching run status for thread: $threadId")
            val response = RetrofitInstance.api.getRunStatus(threadId)
            _status.value = response.status
            Log.d("RunStatusViewModel", "Run status: ${_status.value}")
            isCompleted = _status.value in listOf("completed", "failed", "cancelled", "expired")
        } catch (e: Exception) {
            _status.value = "error"
            Log.e("RunStatusViewModel", "Exception fetching run status: ${e.message}")
        }
        return isCompleted
    }
}
