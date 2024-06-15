package com.app.qaimobile.ui.image_handling

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.app.qaimobile.data.remote.FileUploadService
import com.app.qaimobile.ui.theme.QAiMobileTheme
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class ImageHandlingActivity : ComponentActivity() {

    @Inject
    lateinit var fileUploadService: FileUploadService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            QAiMobileTheme {
                // Ensure this line matches the method signature in MainActivity
                ImageHandlingScreen(fileUploadService = fileUploadService, threadId = null)
            }
        }
    }
}
