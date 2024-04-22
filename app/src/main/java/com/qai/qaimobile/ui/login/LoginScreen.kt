//java/com/qai/qaimobile/ui/login/LoginScreen.kt
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
import kotlinx.coroutines.launch

@Composable
fun LoginScreen(onLoginSuccess: () -> Unit) {
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }

    val coroutineScope = rememberCoroutineScope()
    val apiService = // Initialize your API service here

        Column(
            // ... (existing code)
        ) {
            // ... (existing code)

            Button(
                onClick = {
                    isLoading = true
                    errorMessage = ""
                    coroutineScope.launch {
                        try {
                            val response = apiService.login(LoginRequest(username, password))
                            if (response.success) {
                                // Login successful, navigate to the next screen
                                onLoginSuccess()
                            } else {
                                errorMessage = response.message
                            }
                        } catch (e: Exception) {
                            errorMessage = "Login failed. Please try again."
                        } finally {
                            isLoading = false
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp)
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                } else {
                    Text("Login")
                }
            }

            if (errorMessage.isNotEmpty()) {
                Text(
                    text = errorMessage,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }
        }
}

@Preview(showBackground = true)
@Composable
fun LoginScreenPreview() {
    QAiMobileTheme {
        LoginScreen(onLoginSuccess = {})
    }
}