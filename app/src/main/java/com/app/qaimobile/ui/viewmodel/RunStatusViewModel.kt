package com.app.qaimobile.ui.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import com.app.qaimobile.data.remote.RetrofitInstance

class RunStatusViewModel : ViewModel() {
    private val _status = MutableStateFlow("queued")
    val status: StateFlow<String> = _status

    fun fetchRunStatus(threadId: String) {
        viewModelScope.launch {
            while (true) {
                try {
                    Log.d("RunStatusViewModel", "Fetching run status for thread: $threadId")
                    val response = RetrofitInstance.api.getRunStatus(threadId)
                    _status.value = response.status
                    Log.d("RunStatusViewModel", "Run status: ${response.status}")
                    if (response.status == "completed" || response.status == "failed" || response.status == "cancelled" || response.status == "expired") {
                        break
                    }
                } catch (e: Exception) {
                    _status.value = "error"
                    Log.e("RunStatusViewModel", "Error fetching run status: ${e.message}")
                    break
                }
                delay(1000)
            }
        }
    }
}
