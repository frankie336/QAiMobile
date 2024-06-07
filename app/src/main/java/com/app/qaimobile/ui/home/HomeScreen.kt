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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.app.qaimobile.navigation.Destinations
import com.app.qaimobile.util.showToast
import com.ramcosta.composedestinations.annotation.Destination
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow

@Destination(route = Destinations.HOME_ROUTE)
@Composable
fun HomeScreen(
    onEvent: (HomeViewModelEvent) -> Unit = {},
    uiEvent: SharedFlow<HomeUiEvent> = MutableSharedFlow(),
    navHostController: NavController? = null
) {
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        uiEvent.collect {
            when (it) {
                is HomeUiEvent.ShowMessage -> {
                    showToast(context, it.message)
                }

                is HomeUiEvent.Navigate -> {
                    navHostController?.navigate(it.route, builder = it.navOptionsBuilder)
                }
            }
        }
    }

    Scaffold { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {


            Text(
                text = "Welcome to Q",
                style = MaterialTheme.typography.headlineLarge,
                modifier = Modifier.padding(16.dp)
            )


            Text(
                text = "Q is your humble assistant, designed and built by Project David Ltd.\n",

                        style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(horizontal = 16.dp)
            )


            Text(
                text = "This official app is free, syncs your history across devices, and brings you the latest model improvements from our team.",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(horizontal = 16.dp)
            )


            Text(
                text = "Q can be inaccurate",
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.padding(horizontal = 16.dp)
            )


            Text(
                text = "Q may provide inaccurate information about people, places, or facts.",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(horizontal = 16.dp)
            )


            Text(
                text = "Control your chat history",
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.padding(horizontal = 16.dp)
            )

            Text(
                text = "Decide whether new chats on this device will appear in your history and be used to improve our systems.",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(horizontal = 16.dp)
            )






            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = {
                onEvent(HomeViewModelEvent.Logout)
            }) {
                Text(text = "Logout")
            }
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = {
                navHostController?.navigate(Destinations.CHAT_ROUTE)
            }) {
                Text(text = "Go to Chat")
            }
        }
    }
}