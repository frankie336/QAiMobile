package com.app.qaimobile.ui.composables

import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.window.Dialog

@Composable
fun LoadingDialog() {
    Dialog(onDismissRequest = {}) {
        CircularProgressIndicator()
    }
}