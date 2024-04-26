package com.app.qaimobile.ui.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.app.qaimobile.navigation.Destinations
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import kotlinx.coroutines.launch

@Destination(route = Destinations.HOME_ROUTE)
@Composable
fun HomeScreen(navHostController: DestinationsNavigator? = null) {
    Scaffold { paddingValues ->
        val coroutineScope = rememberCoroutineScope()
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Home Screen",
                style = MaterialTheme.typography.headlineLarge,
                modifier = Modifier.padding(16.dp)
            )
            Spacer(modifier =Modifier.height(16.dp))
            Button(onClick = {
                coroutineScope.launch {

                    navHostController?.navigate(Destinations.LOGIN_ROUTE){
                        popUpTo(Destinations.HOME_ROUTE){
                            inclusive = true
                        }
                    }
                }
            }) {
                Text(text = "Logout")
            }
        }

    }
}