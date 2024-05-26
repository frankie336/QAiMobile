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
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import androidx.hilt.navigation.compose.hiltViewModel
import com.app.qaimobile.data.model.network.ConversationSessionDto
import com.app.qaimobile.navigation.Destinations
import com.app.qaimobile.ui.composables.LoadingDialog
import com.app.qaimobile.util.showToast
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import kotlinx.coroutines.flow.SharedFlow
import kotlin.reflect.KFunction1

@OptIn(ExperimentalMaterial3Api::class)
@Destination(route = Destinations.CHAT_ROUTE)
@Composable
fun QComposerScreen(
    viewModel: ChatViewModel = hiltViewModel(),
    uiEvent: SharedFlow<ChatUiEvent> = viewModel.uiEvent,
    navHostController: DestinationsNavigator? = null,
    onEvent: KFunction1<ChatViewModelEvent, Unit>
) {
    val context = LocalContext.current
    val keyboardController = LocalSoftwareKeyboardController.current
    var message by remember { mutableStateOf("") }
    var conversations by remember { mutableStateOf<List<ConversationSessionDto>?>(null) }
    val state by viewModel.state.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.fetchConversations()
        uiEvent.collect { event ->
            when (event) {
                is ChatUiEvent.ShowError -> showToast(context, event.message)
                is ChatUiEvent.ConversationsLoaded -> conversations = event.conversations
                ChatUiEvent.Success -> {}
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
                if (state.isLoading) {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
                } else {
                    conversations?.let {
                        // Display conversations
                        for (conversation in it) {
                            Text(text = conversation.id, modifier = Modifier.padding(8.dp))
                        }
                    } ?: Text(text = "No conversations available")
                }
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
                        onValueChange = { message = it },
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
                            viewModel.onEvent(ChatViewModelEvent.SendMessage(message))
                            message = ""
                            keyboardController?.hide()
                        },
                        modifier = Modifier
                            .size(40.dp)
                            .background(color = Color(0xFFff6c00), shape = CircleShape)
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
