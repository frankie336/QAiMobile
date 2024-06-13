package com.app.qaimobile.ui.image_handling

import android.app.Application
import android.content.Intent
import android.net.Uri
import android.provider.MediaStore
import androidx.activity.result.ActivityResultLauncher
import androidx.core.content.FileProvider
import androidx.lifecycle.AndroidViewModel
import com.app.qaimobile.BuildConfig
import java.io.File

class ImageViewModel(application: Application) : AndroidViewModel(application) {
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
}
