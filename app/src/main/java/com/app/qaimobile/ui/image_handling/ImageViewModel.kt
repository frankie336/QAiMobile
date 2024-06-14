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
import com.app.qaimobile.data.remote.FileUploadService
import com.app.qaimobile.data.remote.UploadFilesResponse
import dagger.hilt.android.lifecycle.HiltViewModel
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
    var imageUri: Uri? = null

    lateinit var captureImageResultLauncher: ActivityResultLauncher<Intent>
    lateinit var selectImageResultLauncher: ActivityResultLauncher<Intent>

    fun initialize(
        captureImageResultLauncher: ActivityResultLauncher<Intent>,
        selectImageResultLauncher: ActivityResultLauncher<Intent>
    ) {
        this.captureImageResultLauncher = captureImageResultLauncher
        this.selectImageResultLauncher = selectImageResultLauncher
        Log.d("ImageViewModel", "Initialized with launchers")
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
        Log.d("ImageViewModel", "captureImage called, URI: $imageUri")
    }

    fun selectImageFromGallery() {
        val selectIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        selectImageResultLauncher.launch(selectIntent)
        Log.d("ImageViewModel", "selectImageFromGallery called")
    }

    fun updateImageUri(uri: Uri?) {
        imageUri = uri
        Log.d("ImageViewModel", "Image URI updated: $uri")
    }

    private fun uriToFile(uri: Uri): File {
        val contentResolver: ContentResolver = getApplication<Application>().contentResolver
        val inputStream: InputStream? = contentResolver.openInputStream(uri)
        val file = File(getApplication<Application>().cacheDir, "tempImageFile.jpg")
        val outputStream = FileOutputStream(file)
        inputStream?.use { input ->
            outputStream.use { output ->
                input.copyTo(output)
            }
        }
        return file
    }

    fun uploadSelectedImage(threadId: String?) {
        imageUri?.let { uri ->
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
                        override fun onResponse(call: Call<UploadFilesResponse>, response: Response<UploadFilesResponse>) {
                            Log.d("ImageViewModel", "Upload response: $response")

                            if (response.isSuccessful) {
                                Log.d("ImageViewModel", "Image uploaded successfully: ${response.body()}")
                            } else {
                                Log.e("ImageViewModel", "Image upload failed: ${response.errorBody()?.string()}")
                            }
                        }

                        override fun onFailure(call: Call<UploadFilesResponse>, t: Throwable) {
                            Log.e("ImageViewModel", "Exception occurred during image upload", t)
                        }
                    })
                } catch (e: Exception) {
                    Log.e("ImageViewModel", "Exception occurred during image upload", e)
                }
            }
        }
    }
}
