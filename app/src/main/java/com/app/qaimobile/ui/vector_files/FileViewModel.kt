package com.app.qaimobile.ui.vector_files

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.qaimobile.data.model.network.FileMetadata
import com.app.qaimobile.data.remote.ApiService
import com.app.qaimobile.data.datastore.DataStoreManager
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import javax.inject.Inject

class FileViewModel @Inject constructor(
    private val apiService: ApiService,
    private val dataStoreManager: DataStoreManager
) : ViewModel() {

    private val _files = MutableLiveData<List<FileMetadata>>()
    val files: LiveData<List<FileMetadata>> = _files

    fun fetchFiles() {
        viewModelScope.launch {
            val token = dataStoreManager.getAccessToken().firstOrNull()
            if (token != null) {
                try {
                    val response = apiService.getFiles("Bearer $token")
                    if (response.isSuccessful) {
                        _files.value = response.body()?.files ?: emptyList()
                    } else {
                        Log.e("FileViewModel", "Failed to fetch files: ${response.errorBody()?.string()}")
                    }
                } catch (e: Exception) {
                    Log.e("FileViewModel", "Exception fetching files", e)
                }
            } else {
                Log.e("FileViewModel", "Access token is null")
            }
        }
    }
}
