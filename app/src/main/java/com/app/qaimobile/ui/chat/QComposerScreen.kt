package com.app.qaimobile.ui.chat

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
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
        uiEvent.collect { event ->
            when (event) {
                is ChatUiEvent.ShowError -> {
                    showToast(context, event.message)
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

            Column(
                modifier = Modifier
                    .constrainAs(column) {
                        top.linkTo(parent.top)
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                        bottom.linkTo(row.top)
                        height = Dimension.fillToConstraints
                    }
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.Center
            ) {
                // Chat message list would go here
            }

            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
                    .background(color = Color.White, shape = RoundedCornerShape(24.dp))
                    .padding(horizontal = 16.dp, vertical = 8.dp)
                    .constrainAs(row) {
                        bottom.linkTo(parent.bottom, 16.dp)
                    },
                shape = RoundedCornerShape(24.dp),
                color = Color.White,
                border = BorderStroke(1.dp, Color.LightGray)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    TextField(
                        value = message,
                        onValueChange = setMessage,
                        placeholder = { Text("Type a message") },
                        modifier = Modifier
                            .weight(1f)
                            .background(color = Color.Transparent, shape = RoundedCornerShape(24.dp))
                            .padding(horizontal = 16.dp, vertical = 8.dp),
                        colors = TextFieldDefaults.textFieldColors(
                            containerColor = Color.Transparent,
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent
                        ),
                        keyboardActions = KeyboardActions(onDone = { keyboardController?.hide() })
                    )

                    IconButton(
                        onClick = {
                            onEvent(ChatViewModelEvent.SendMessage(message))
                            setMessage("")
                        },
                        modifier = Modifier
                            .size(40.dp)
                            .background(color = Color(0xFFff6c00), shape = CircleShape) // Change the color here
                            .padding(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Send,
                            contentDescription = "Send",
                            tint = Color.White
                        )
                    }
                }
            }
        }
    }
}