package com.app.qaimobile.ui.vector_files

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.qaimobile.data.model.network.FileMetadata
import com.app.qaimobile.data.remote.ApiService
import com.app.qaimobile.data.datastore.DataStoreManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
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
                //val formattedToken = if (token.startsWith("Bearer ")) token else "Bearer $token"
                Log.d("FileViewModel", "Formatted token: $token")
                try {
                    val response = apiService.getFiles()
                    if (response.isSuccessful) {
                        Log.d("FileViewModel", "Response is successful")
                        response.body()?.let {
                            Log.d("FileViewModel", "Files retrieved: ${it.files.size}")
                            _files.value = it.files
                        } ?: run {
                            Log.e("FileViewModel", "Response body is null")
                        }
                    } else {
                        Log.e("FileViewModel", "Failed to fetch files: ${response.errorBody()?.string()}")
                        Log.e("FileViewModel", "Response headers: ${response.headers()}")
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
