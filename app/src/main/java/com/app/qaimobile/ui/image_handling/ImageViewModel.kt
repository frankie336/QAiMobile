package com.app.qaimobile.ui.image_handling

import android.app.Application
import android.content.Intent
import android.net.Uri
import android.provider.MediaStore
import androidx.activity.result.ActivityResultLauncher
import androidx.core.content.FileProvider
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.app.qaimobile.BuildConfig
import com.app.qaimobile.data.remote.ApiService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody

@HiltViewModel
class ImageViewModel @Inject constructor(
    application: Application,
    private val apiService: ApiService
) : AndroidViewModel(application) {
    var imageUri: Uri? = null

    lateinit var captureImageResultLauncher: ActivityResultLauncher<Intent>
    lateinit var selectImageResultLauncher: ActivityResultLauncher<Intent>

    fun initialize(
        captureImageResultLauncher: ActivityResultLauncher<Intent>,
        selectImageResultLauncher: ActivityResultLauncher<Intent>
    ) {
        this.captureImageResultLauncher = captureImageResultLauncher
        this.selectImageResultLauncher = selectImageResultLauncher
    }

    fun captureImage() {
        val captureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        val imageFile = File(getApplication<Application>().getExternalFilesDir(null), "image.jpg")
        imageUri = FileProvider.getUriForFile(
            getApplication(),
            "${getApplication<Application>().packageName}.fileprovider",
            imageFile
        )
        captureIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri)
        captureImageResultLauncher.launch(captureIntent)
    }

    fun selectImageFromGallery() {
        val selectIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        selectImageResultLauncher.launch(selectIntent)
    }

    fun updateImageUri(uri: Uri?) {
        imageUri = uri
    }

    fun uploadSelectedImage(threadId: String?) {
        imageUri?.let { uri ->
            viewModelScope.launch {
                try {
                    val file = File(uri.path!!)
                    val requestBody = file.asRequestBody("image/*".toMediaTypeOrNull())
                    val filePart = MultipartBody.Part.createFormData("files", file.name, requestBody)

                    val response = apiService.uploadFiles(
                        files = listOf(filePart),
                        tabNames = listOf("image"),
                        userId = "user123",
                        threadId = threadId
                    )

                    if (response.isSuccessful) {
                        // Image uploaded successfully
                        // Handle the response or update the UI as needed
                    } else {
                        // Image upload failed
                        // Handle the error or show an error message
                    }
                } catch (e: Exception) {
                    // Exception occurred during image upload
                    // Handle the exception or show an error message
                }
            }
        }
    }

    // Define the Factory interface
    interface Factory {
        fun create(application: Application): ImageViewModel
    }
}
