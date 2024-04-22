package com.qai.qaimobile.ui.login

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.qai.qaimobile.data.model.LoginRequest
import com.qai.qaimobile.data.remote.ApiService
import com.qai.qaimobile.ui.theme.QAiMobileTheme

@Composable
fun LoginScreen(onLoginSuccess: () -> Unit) {
    // ... (login screen composable code from the previous response)
}

@Preview(showBackground = true)
@Composable
fun LoginScreenPreview() {
    QAiMobileTheme {
        LoginScreen(onLoginSuccess = {})
    }
}