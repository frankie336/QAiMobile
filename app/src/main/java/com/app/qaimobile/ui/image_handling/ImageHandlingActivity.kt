package com.app.qaimobile.ui.image_handling

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.app.qaimobile.ui.theme.QAiMobileTheme

class ImageHandlingActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            QAiMobileTheme {
                ImageHandlingScreen()
            }
        }
    }
}
