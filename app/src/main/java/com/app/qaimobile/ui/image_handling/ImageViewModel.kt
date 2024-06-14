package com.app.qaimobile.ui.image_handling

import android.app.Application
import android.content.ContentResolver
import android.content.Intent
import android.net.Uri
import android.provider.MediaStore
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import coil.compose.AsyncImage
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

    private val _imageUri = MutableStateFlow<Uri?>(null)
    var imageUri: StateFlow<Uri?> = _imageUri.asStateFlow()

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
        _imageUri.value = FileProvider.getUriForFile(
            getApplication(),
            "${getApplication<Application>().packageName}.fileprovider",
            imageFile
        )
        captureIntent.putExtra(MediaStore.EXTRA_OUTPUT, _imageUri.value)
        captureImageResultLauncher.launch(captureIntent)
        Log.d("ImageViewModel", "captureImage called, URI: ${_imageUri.value}")
    }

    fun selectImageFromGallery() {
        val selectIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        selectImageResultLauncher.launch(selectIntent)
        Log.d("ImageViewModel", "selectImageFromGallery called")
    }

    fun updateImageUri(uri: Uri?) {
        _imageUri.value = uri
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
        _imageUri.value?.let { uri ->
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

@Composable
fun ImageHandlingScreen(
    viewModel: ImageViewModel = hiltViewModel()
) {
    val captureImageLauncher = rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == android.app.Activity.RESULT_OK) {
            viewModel.imageUri.value?.let { /* Handle the captured image URI */ }
        }
    }

    val selectImageLauncher = rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == android.app.Activity.RESULT_OK && result.data != null) {
            viewModel.updateImageUri(result.data!!.data)
        }
    }

    LaunchedEffect(Unit) {
        viewModel.initialize(captureImageLauncher, selectImageLauncher)
    }

    val imageUri by viewModel.imageUri.collectAsState()

    LaunchedEffect(imageUri) {
        imageUri?.let {
            viewModel.uploadSelectedImage(threadId = null) // Pass the threadId if available
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        imageUri?.let {
            AsyncImage(
                model = it,
                contentDescription = null,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(300.dp),
                contentScale = ContentScale.Crop
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = { viewModel.captureImage() }) {
            Text("Capture Image")
        }

        Spacer(modifier = Modifier.height(8.dp))

        Button(onClick = { viewModel.selectImageFromGallery() }) {
            Text("Select Image from Gallery")
        }
    }
}