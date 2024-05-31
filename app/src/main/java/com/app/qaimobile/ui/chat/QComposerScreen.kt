package com.app.qaimobile.ui.chat

import android.util.Log
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.app.qaimobile.data.model.network.toEntity
import com.app.qaimobile.navigation.Destinations
import com.app.qaimobile.ui.composables.ChatBubble
import com.app.qaimobile.ui.composables.LoadingDialog
import com.app.qaimobile.ui.home.ThreadsSidebar
import com.app.qaimobile.util.showToast
import com.ramcosta.composedestinations.annotation.Destination
import kotlinx.coroutines.flow.SharedFlow

@OptIn(ExperimentalMaterial3Api::class)
@Destination(route = Destinations.CHAT_ROUTE)
@Composable
fun QComposerScreen(
    viewModel: ChatViewModel = hiltViewModel(),
    uiEvent: SharedFlow<ChatUiEvent> = viewModel.uiEvent,
    navHostController: NavController,
    onEvent: (ChatUiEvent) -> Unit
) {
    val context = LocalContext.current
    val keyboardController = LocalSoftwareKeyboardController.current
    var message by remember { mutableStateOf("") }
    val state by viewModel.state.collectAsState()
    val conversations by viewModel.conversations.collectAsState()
    val selectedMessages by viewModel.selectedConversationMessages.collectAsState()
    var showSidebar by remember { mutableStateOf(false) }
    var selectedThreadId by remember { mutableStateOf<String?>(null) }
    var expanded by remember { mutableStateOf(false) } // For menu expansion state

    LaunchedEffect(Unit) {
        viewModel.fetchConversations()
        uiEvent.collect { event ->
            when (event) {
                is ChatUiEvent.ShowError -> showToast(context, event.message)
                ChatUiEvent.Success -> {}
                is ChatUiEvent.ConversationsLoaded -> {}
                is ChatUiEvent.SelectConversation -> {
                    selectedThreadId = event.conversationId
                    Log.d("QComposerScreen", "Selected Conversation ID: $selectedThreadId")
                }
                is ChatUiEvent.SendMessage -> TODO()  // Properly handle SendMessage event
            }
        }
    }

    if (state.isLoading) {
        LoadingDialog()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Chat") },
                navigationIcon = {
                    IconButton(onClick = { showSidebar = !showSidebar }) {
                        Icon(Icons.Default.Menu, contentDescription = "Toggle Sidebar")
                    }
                },
                actions = {
                    Box {
                        IconButton(onClick = { expanded = !expanded }) {
                            Icon(Icons.Default.MoreVert, contentDescription = "Menu")
                        }
                        DropdownMenu(
                            expanded = expanded,
                            onDismissRequest = { expanded = false },
                            offset = DpOffset((-160).dp, (-16).dp) // Adjust the offset to position the menu correctly
                        ) {
                            DropdownMenuItem(
                                text = { Text("Files") },
                                onClick = {
                                    expanded = false
                                    Log.d("QComposerScreen", "Files clicked")
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("Settings") },
                                onClick = {
                                    expanded = false
                                    Log.d("QComposerScreen", "Settings clicked")
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("Models") },
                                onClick = {
                                    expanded = false
                                    Log.d("QComposerScreen", "Models clicked")
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("Profile") },
                                onClick = {
                                    expanded = false
                                    Log.d("QComposerScreen", "Profile clicked")
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("Select Personality") },
                                onClick = {
                                    expanded = false
                                    navHostController.navigate(Destinations.PERSONALITY_SELECTION_ROUTE) // Navigate to the PersonalitySelectionScreen
                                    Log.d("QComposerScreen", "Select Personality clicked")
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("Logout") },
                                onClick = {
                                    expanded = false
                                    Log.d("QComposerScreen", "Logout clicked")
                                }
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.LightGray,
                    titleContentColor = Color.Black
                )
            )
        }
    ) { paddingValues ->
        ConstraintLayout(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            val (sidebar, content, row) = createRefs()

            if (showSidebar) {
                ThreadsSidebar(
                    conversations = conversations.map { it.toEntity() },
                    onThreadClick = { sessionId ->
                        showSidebar = false
                        onEvent(ChatUiEvent.SelectConversation(sessionId))
                        selectedThreadId = sessionId
                        Log.d("QComposerScreen", "Thread Clicked: $selectedThreadId")
                    },
                    modifier = Modifier.constrainAs(sidebar) {
                        top.linkTo(parent.top)
                        start.linkTo(parent.start)
                        bottom.linkTo(row.top)
                        height = Dimension.fillToConstraints
                    }
                )
            }

            Column(
                modifier = Modifier
                    .constrainAs(content) {
                        top.linkTo(parent.top)
                        start.linkTo(parent.start, if (showSidebar) 200.dp else 0.dp)
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
                    selectedMessages?.let { messages ->
                        messages.forEach { message ->
                            ChatBubble(message)
                        }
                    } ?: Text("No conversation selected", modifier = Modifier.align(Alignment.CenterHorizontally))
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
                            .background(color = Color.Transparent, shape = RoundedCornerShape(24.dp)),
                        colors = TextFieldDefaults.textFieldColors(
                            containerColor = Color.Transparent,
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent
                        ),
                        keyboardActions = KeyboardActions(onDone = { keyboardController?.hide() })
                    )

                    IconButton(
                        onClick = {
                            if (selectedThreadId != null) {
                                Log.d("QComposerScreen", "Sending Message: $message to $selectedThreadId")
                                onEvent(ChatUiEvent.SendMessage(selectedThreadId!!, message))
                                message = ""
                                keyboardController?.hide()
                            } else {
                                showToast(context, "Please select a conversation first")
                            }
                        },
                        modifier = Modifier
                            .size(40.dp)
                            .background(color = Color(0xFFff6c00), shape = CircleShape)
                            .padding(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.ArrowUpward,
                            contentDescription = "Send",
                            tint = Color.White
                        )
                    }
                }
            }
        }
    }
}
