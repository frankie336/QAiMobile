package com.app.qaimobile.ui.image_handling

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.app.qaimobile.data.remote.FileUploadService

@Composable
fun ImageHandlingScreen(
    fileUploadService: FileUploadService,
    viewModel: ImageViewModel = viewModel(factory = ImageViewModelFactory(fileUploadService))
) {
    val captureImageLauncher = rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == android.app.Activity.RESULT_OK) {
            viewModel.imageUri?.let { /* Handle the captured image URI */ }
        }
    }

    val selectImageLauncher = rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == android.app.Activity.RESULT_OK && result.data != null) {
            viewModel.imageUri = result.data!!.data
        }
    }

    LaunchedEffect(Unit) {
        viewModel.initialize(captureImageLauncher, selectImageLauncher)
    }

    LaunchedEffect(viewModel.imageUri) {
        viewModel.imageUri?.let {
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
        viewModel.imageUri?.let {
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

