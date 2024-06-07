package com.app.qaimobile.ui.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import com.app.qaimobile.data.remote.RetrofitInstance
import javax.inject.Inject

@HiltViewModel
class RunStatusViewModel @Inject constructor() : ViewModel() {
    private val _status = MutableStateFlow("idle")
    val status: StateFlow<String> = _status

    fun pollRunStatus(threadId: String, timeoutMillis: Long = 30000L) {
        viewModelScope.launch {
            val startTime = System.currentTimeMillis()

            while (System.currentTimeMillis() - startTime < timeoutMillis) {
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

            if (_status.value != "completed") {
                _status.value = "timeout"
            }
        }
    }
}