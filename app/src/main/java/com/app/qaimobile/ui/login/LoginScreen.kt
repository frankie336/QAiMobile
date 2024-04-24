package com.app.qaimobile.ui.login

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun LoginScreen(loginViewModel: LoginViewModel = viewModel()) {
    var usernameState by remember { mutableStateOf("") }
    var passwordState by remember { mutableStateOf("") }

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Login",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(vertical = 16.dp)
        )
        TextField(
            value = usernameState,
            onValueChange = { usernameState = it },
            label = { Text("Username") },
            modifier = Modifier.padding(vertical = 8.dp)
        )
        TextField(
            value = passwordState,
            onValueChange = { passwordState = it },
            label = { Text("Password") },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.padding(vertical = 8.dp)
        )
        Button(
            onClick = {
                val username = usernameState
                val password = passwordState
                //loginViewModel.login(username, password)
            },
            modifier = Modifier.padding(vertical = 8.dp)
        ) {
            Text(text = "Login")
        }
    }
}

@Preview
@Composable
fun PreviewLoginScreen() {
    LoginScreen()
}