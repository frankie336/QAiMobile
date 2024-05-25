package com.app.qaimobile.ui.chat

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import com.app.qaimobile.navigation.Destinations
import com.app.qaimobile.ui.composables.LoadingDialog
import com.app.qaimobile.util.rememberActivityOrNull
import com.app.qaimobile.util.showToast
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow

@OptIn(ExperimentalMaterial3Api::class)
@Destination(route = Destinations.CHAT_ROUTE)
@Composable
fun QComposerScreen(
    state: ChatState = ChatState(),
    onEvent: (ChatViewModelEvent) -> Unit = {},
    uiEvent: SharedFlow<ChatUiEvent> = MutableSharedFlow(),
    navHostController: DestinationsNavigator? = null
) {

    val context = LocalContext.current
    val activity = rememberActivityOrNull()
    val keyboardController = LocalSoftwareKeyboardController.current
    val (message, setMessage) = remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        uiEvent.collect {
            when (it) {
                is ChatUiEvent.ShowError -> {
                    showToast(context, it.message)
                }

                ChatUiEvent.Success -> {
                    // handle success event
                }
            }
        }
    }

    if (state.isLoading) {
        LoadingDialog()
    }

    Scaffold { paddingValues ->
        ConstraintLayout(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            val (column, row) = createRefs()
            Column(modifier = Modifier
                .constrainAs(column) {
                    top.linkTo(parent.top)
                    bottom.linkTo(row.top)
                    start.linkTo(parent.start, 16.dp)
                    end.linkTo(parent.end, 16.dp)
                    width = Dimension.fillToConstraints
                    height = Dimension.fillToConstraints
                }
                .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.Center) {
                // Chat message list would go here
                TextField(
                    value = message,
                    onValueChange = setMessage,
                    placeholder = { Text("Type a message") },
                    modifier = Modifier.fillMaxWidth().padding(8.dp),
                    keyboardActions = KeyboardActions(onDone = {
                        keyboardController?.hide()
                    })
                )
            }
            Row(verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.constrainAs(row) {
                    width = Dimension.matchParent
                    bottom.linkTo(parent.bottom, 16.dp)
                }) {
                Button(onClick = {
                    onEvent(ChatViewModelEvent.SendMessage(message))
                    setMessage("")
                }) { Text(text = "Send") }
            }
        }
    }
}