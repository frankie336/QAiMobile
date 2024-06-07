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

    fun fetchRunStatus(threadId: String): StateFlow<String> {
        viewModelScope.launch {
            while (true) {
                try {
                    Log.d("RunStatusViewModel", "Fetching run status for thread: $threadId")
                    val response = RetrofitInstance.api.getRunStatus(threadId)
                    _status.value = response.status
                    Log.d("RunStatusViewModel", "Run status: ${response.status}")
                    if (response.status in listOf("completed", "failed", "cancelled", "expired")) {
                        break
                    }
                } catch (e: Exception) {
                    _status.value = "error"
                    Log.e("RunStatusViewModel", "Error fetching run status: ${e.message}")
                    break
                }
                delay(1000)
            }
            // Set the status back to "idle" after the message run is finished
            _status.value = "idle"
        }
        return _status
    }
}