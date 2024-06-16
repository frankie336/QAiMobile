package com.app.qaimobile.ui.image_handling

import android.app.Application
import android.content.ContentResolver
import android.content.Intent
import android.net.Uri
import android.provider.MediaStore
import android.util.Log
import androidx.activity.result.ActivityResultLauncher
import androidx.core.content.FileProvider
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.app.qaimobile.data.remote.DeleteFileRequest
import com.app.qaimobile.data.remote.FileUploadService
import com.app.qaimobile.data.remote.UploadFilesResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import javax.inject.Inject

@HiltViewModel
class ImageViewModel @Inject constructor(
    application: Application,
    private val fileUploadService: FileUploadService
) : AndroidViewModel(application) {

    // MutableStateFlow to hold the list of image URIs
    private val _imageUris = MutableStateFlow<List<Uri>>(emptyList())
    val imageUris: StateFlow<List<Uri>> = _imageUris.asStateFlow()

    // MutableStateFlow to track the upload success status
    private val _uploadSuccess = MutableStateFlow(false)
    val uploadSuccess: StateFlow<Boolean> = _uploadSuccess.asStateFlow()

    // Map to store the relationship between URI and file names
    private val uriToFileMap = mutableMapOf<Uri, String>()

    // Launchers for capturing and selecting images
    lateinit var captureImageResultLauncher: ActivityResultLauncher<Intent>
    lateinit var selectImageResultLauncher: ActivityResultLauncher<Intent>

    // Initialize the launchers for capturing and selecting images
    fun initialize(
        captureImageResultLauncher: ActivityResultLauncher<Intent>,
        selectImageResultLauncher: ActivityResultLauncher<Intent>
    ) {
        this.captureImageResultLauncher = captureImageResultLauncher
        this.selectImageResultLauncher = selectImageResultLauncher
        Log.d("ImageViewModel", "Initialized with launchers")
    }

    // Capture an image using the device camera
    fun captureImage() {
        val captureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        val imageFile = File(getApplication<Application>().getExternalFilesDir(null), "image.jpg")
        val imageUri = FileProvider.getUriForFile(
            getApplication(),
            "${getApplication<Application>().packageName}.fileprovider",
            imageFile
        )
        captureIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri)
        captureImageResultLauncher.launch(captureIntent)
        Log.d("ImageViewModel", "captureImage called, URI: $imageUri")
    }

    // Select an image from the gallery
    fun selectImageFromGallery() {
        val selectIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        selectIntent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
        selectImageResultLauncher.launch(selectIntent)
        Log.d("ImageViewModel", "selectImageFromGallery called")
    }

    // Update the list of image URIs and start the upload process
    fun updateImageUris(uris: List<Uri>, threadId: String?) {
        val currentList = _imageUris.value.toMutableList()
        currentList.addAll(uris)
        _imageUris.value = currentList
        Log.d("ImageViewModel", "Image URIs added: $uris")
        uploadSelectedImages(threadId)  // Ensure upload starts with threadId
    }

    // Clear the list of image URIs
    fun clearImageUris() {
        _imageUris.value = emptyList()
    }

    // Remove an image URI and delete the file from the backend
    fun removeImageUri(uri: Uri, threadId: String?) {
        val currentList = _imageUris.value.toMutableList()
        currentList.remove(uri)
        _imageUris.value = currentList
        Log.d("ImageViewModel", "Image URI removed: $uri")
        deleteFileFromBackend(uri, threadId)
        uriToFileMap.remove(uri)  // Remove the mapping
    }

    // Convert a URI to a file and store the mapping
    private fun uriToFile(uri: Uri): File {
        val contentResolver: ContentResolver = getApplication<Application>().contentResolver
        val inputStream: InputStream? = contentResolver.openInputStream(uri)
        val fileName = "tempImageFile${System.currentTimeMillis()}.jpg"
        val file = File(getApplication<Application>().cacheDir, fileName)
        val outputStream = FileOutputStream(file)
        inputStream?.use { input ->
            outputStream.use { output ->
                input.copyTo(output)
            }
        }
        uriToFileMap[uri] = fileName
        return file
    }

    // Upload the selected images to the backend
    fun uploadSelectedImages(threadId: String?) {
        _imageUris.value.forEach { uri ->
            viewModelScope.launch {
                try {
                    Log.d("ImageViewModel", "Starting image upload for URI: $uri")
                    val file = uriToFile(uri)
                    val requestBody = file.asRequestBody("image/*".toMediaTypeOrNull())
                    val filePart = MultipartBody.Part.createFormData("files", file.name, requestBody)

                    Log.d("ImageViewModel", "Prepared file part for upload: $filePart")

                    val tabNamesRequestBody = MultipartBody.Part.createFormData("tabNames", "image")
                    val userIdRequestBody = MultipartBody.Part.createFormData("userId", "user123")
                    val threadIdRequestBody = threadId?.let { MultipartBody.Part.createFormData("threadId", it) }

                    Log.d("ImageViewModel", "Prepared tabNamesRequestBody: $tabNamesRequestBody")
                    Log.d("ImageViewModel", "Prepared userIdRequestBody: $userIdRequestBody")
                    Log.d("ImageViewModel", "Prepared threadIdRequestBody: $threadIdRequestBody")

                    // Making the external API call to the backend to upload the image asynchronously
                    fileUploadService.uploadFiles(
                        files = listOf(filePart),
                        tabNames = listOf("image"),
                        userId = "user123",
                        threadId = threadId
                    ).enqueue(object : Callback<UploadFilesResponse> {
                        override fun onResponse(
                            call: Call<UploadFilesResponse>,
                            response: Response<UploadFilesResponse>
                        ) {
                            Log.d("ImageViewModel", "Upload response: $response")

                            if (response.isSuccessful) {
                                Log.d(
                                    "ImageViewModel",
                                    "Image uploaded successfully: ${response.body()}"
                                )
                                _uploadSuccess.value = true
                            } else {
                                Log.e(
                                    "ImageViewModel",
                                    "Image upload failed: ${response.errorBody()?.string()}"
                                )
                                _uploadSuccess.value = false
                            }
                        }

                        override fun onFailure(call: Call<UploadFilesResponse>, t: Throwable) {
                            Log.e("ImageViewModel", "Exception occurred during image upload", t)
                            _uploadSuccess.value = false
                        }
                    })
                } catch (e: Exception) {
                    Log.e("ImageViewModel", "Exception occurred during image upload", e)
                    _uploadSuccess.value = false
                }
            }
        }
    }

    // Delete a file from the backend
    private fun deleteFileFromBackend(uri: Uri, threadId: String?) {
        viewModelScope.launch {
            try {
                val fileName = uriToFileMap[uri] ?: uri.lastPathSegment ?: ""
                val response = fileUploadService.deleteFile(
                    DeleteFileRequest(
                        userId = "user123",
                        threadId = threadId,
                        tabName = "image",
                        filename = fileName
                    )
                )
                if (response.isSuccessful) {
                    Log.d("ImageViewModel", "File deleted successfully from backend: $uri")
                } else {
                    Log.e("ImageViewModel", "Failed to delete file from backend: ${response.errorBody()?.string()}")
                }
            } catch (e: Exception) {
                Log.e("ImageViewModel", "Exception occurred while deleting file from backend", e)
            }
        }
    }

    // Placeholder function, can be removed if not needed
    fun updateImageUris(uris: List<Uri>) {
        // Implementation needed or can be removed if not used
    }
}
