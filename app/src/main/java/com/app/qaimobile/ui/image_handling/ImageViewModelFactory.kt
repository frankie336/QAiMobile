package com.app.qaimobile.ui.image_handling

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.app.qaimobile.data.remote.FileUploadService

class ImageViewModelFactory(
    private val fileUploadService: FileUploadService
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ImageViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ImageViewModel(Application(), fileUploadService) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
