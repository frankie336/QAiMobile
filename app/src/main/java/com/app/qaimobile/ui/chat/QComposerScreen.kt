package com.app.qaimobile.ui.chat

import android.content.Intent
import android.content.res.Configuration
import android.net.Uri
import android.provider.MediaStore
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.app.qaimobile.data.model.network.toEntity
import com.app.qaimobile.navigation.Destinations
import com.app.qaimobile.ui.composables.AnimatedOrb
import com.app.qaimobile.ui.composables.ChatBubble
import com.app.qaimobile.ui.composables.LoadingDialog
import com.app.qaimobile.ui.home.ThreadsSidebar
import com.app.qaimobile.ui.image_handling.ImageViewModel
import com.app.qaimobile.ui.viewmodel.RunStatusViewModel
import com.app.qaimobile.util.Constants.DEFAULT_MODEL
import com.app.qaimobile.util.showToast
import com.ramcosta.composedestinations.annotation.Destination
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Destination(route = Destinations.CHAT_ROUTE)
@Composable
fun QComposerScreen(
    viewModel: ChatViewModel = hiltViewModel(),
    runStatusViewModel: RunStatusViewModel = hiltViewModel(),
    imageViewModel: ImageViewModel = hiltViewModel(),
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
    val selectedModel by viewModel.selectedModel.collectAsState(initial = DEFAULT_MODEL)
    var showSidebar by remember { mutableStateOf(false) }
    var expanded by remember { mutableStateOf(false) }
    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()

    val modelMapping = mapOf(
        "gpt-4o" to "4o",
        "gpt-4-turbo-2024-04-09" to "4",
        "gpt-3.5-turbo" to "3.5"
    )

    var selectedThreadId by remember { mutableStateOf<String?>(null) }
    var showBorder by remember { mutableStateOf(false) }

    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE

    val imageUri by imageViewModel.imageUri.collectAsState()

    // Initialize image pickers
    val selectImageLauncher = rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == android.app.Activity.RESULT_OK && result.data != null) {
            imageViewModel.updateImageUri(result.data!!.data)
        }
    }

    val captureImageLauncher = rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == android.app.Activity.RESULT_OK) {
            imageViewModel.imageUri?.let { /* Handle the captured image URI */ }
        }
    }

    LaunchedEffect(Unit) {
        viewModel.fetchConversations()
        uiEvent.collect { event ->
            when (event) {
                is ChatUiEvent.ShowError -> showToast(context, event.message)
                ChatUiEvent.Success -> {}
                is ChatUiEvent.ConversationsLoaded -> {}
                is ChatUiEvent.SelectConversation -> {
                    selectedThreadId = event.conversationId
                }
                is ChatUiEvent.SendMessage -> {}
            }
        }
        imageViewModel.initialize(captureImageLauncher, selectImageLauncher)
    }

    LaunchedEffect(selectedMessages) {
        selectedMessages?.let { messages ->
            if (messages.isNotEmpty()) {
                listState.animateScrollToItem(messages.size - 1)
            }
        }
    }

    if (state.isLoading) {
        LoadingDialog()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    var modelDropdownExpanded by remember { mutableStateOf(false) }
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = modelMapping[selectedModel] ?: DEFAULT_MODEL,
                            modifier = Modifier.clickable { modelDropdownExpanded = !modelDropdownExpanded },
                            color = Color.Gray
                        )
                        DropdownMenu(
                            expanded = modelDropdownExpanded,
                            onDismissRequest = { modelDropdownExpanded = false }
                        ) {
                            modelMapping.keys.forEach { key ->
                                DropdownMenuItem(
                                    text = { Text(modelMapping[key] ?: key) },
                                    onClick = {
                                        viewModel.saveSelectedModel(key)
                                        modelDropdownExpanded = false
                                    }
                                )
                            }
                        }
                        Spacer(modifier = Modifier.weight(1f))
                        AnimatedOrb(runStatusViewModel = runStatusViewModel)
                        Spacer(modifier = Modifier.weight(1f))
                    }
                },
                navigationIcon = {
                    IconButton(onClick = { showSidebar = !showSidebar }) {
                        Icon(Icons.Default.Menu, contentDescription = "Toggle Sidebar", tint = Color.Gray)
                    }
                },
                actions = {
                    Row {
                        IconButton(onClick = {
                            viewModel.createSession()
                            selectedThreadId = null
                        }) {
                            Icon(Icons.Default.Add, contentDescription = "Create Session", tint = Color.Gray)
                        }
                        IconButton(onClick = { expanded = !expanded }) {
                            Icon(Icons.Default.MoreVert, contentDescription = "Menu", tint = Color.Gray)
                        }
                    }
                    DropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false },
                        offset = DpOffset((-160).dp, (-16).dp)
                    ) {
                        DropdownMenuItem(
                            text = { Text("Files", color = Color.Gray) },
                            onClick = {
                                expanded = false
                                navHostController.navigate(Destinations.FILE_LIST_ROUTE) // Navigate to FileListScreen
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Settings", color = Color.Gray) },
                            onClick = {
                                expanded = false
                            }
                        )

                        DropdownMenuItem(
                            text = { Text("Profile", color = Color.Gray) },
                            onClick = {
                                expanded = false
                            }
                        )

                        DropdownMenuItem(
                            text = { Text("Personality", color = Color.Gray) },
                            onClick = {
                                expanded = false
                                navHostController.navigate(Destinations.PERSONALITY_SELECTION_ROUTE)
                            }
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White,
                    titleContentColor = Color.Gray
                )
            )
        }
    ) { paddingValues ->
        GlowingBorder(runStatusViewModel = runStatusViewModel, showBorder = showBorder)
        ConstraintLayout(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            val (sidebar, content, row) = createRefs()

            Box(
                modifier = Modifier
                    .constrainAs(content) {
                        top.linkTo(parent.top)
                        start.linkTo(if (isLandscape && showSidebar) sidebar.end else parent.start)
                        end.linkTo(parent.end)
                        bottom.linkTo(row.top)
                        height = Dimension.fillToConstraints
                    }
            ) {
                LazyColumn(
                    state = listState,
                    modifier = Modifier.fillMaxSize()
                ) {
                    selectedMessages?.let { messages ->
                        items(messages.size) { index ->
                            ChatBubble(messages[index])
                        }
                    }
                }
            }

            if (showSidebar) {
                ThreadsSidebar(
                    conversations = conversations.map { it.toEntity() },
                    onThreadClick = { sessionId ->
                        showSidebar = false
                        onEvent(ChatUiEvent.SelectConversation(sessionId))
                        selectedThreadId = sessionId
                    },
                    modifier = Modifier
                        .fillMaxHeight()
                        .width(if (isLandscape) 300.dp else 200.dp)
                        .background(color = MaterialTheme.colorScheme.surface)
                        .border(width = 1.dp, color = Color.LightGray)
                        .constrainAs(sidebar) {
                            top.linkTo(parent.top)
                            start.linkTo(parent.start)
                            bottom.linkTo(parent.bottom)
                        }
                )
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
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    ImagePreview(imageUri = imageUri, onRemove = { imageViewModel.updateImageUri(null) })

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
                                selectImageLauncher.launch(Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI))
                                coroutineScope.launch {
                                    imageViewModel.uploadSelectedImage(selectedThreadId)
                                }
                            },
                            modifier = Modifier
                                .size(40.dp)
                                .background(color = Color(0xFFff6c00), shape = CircleShape)
                                .padding(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Image,
                                contentDescription = "Select Image",
                                tint = Color.White
                            )
                        }

                        IconButton(
                            onClick = {
                                if (message.isNotBlank()) {
                                    coroutineScope.launch {
                                        onEvent(ChatUiEvent.SendMessage(selectedThreadId ?: "", message))
                                        message = ""
                                        keyboardController?.hide()
                                        showBorder = true

                                        val timeoutMillis = 30000L // 30 seconds timeout
                                        val startTime = System.currentTimeMillis()

                                        while (System.currentTimeMillis() - startTime < timeoutMillis) {
                                            runStatusViewModel.fetchRunStatus(selectedThreadId ?: "")
                                            when (runStatusViewModel.status.value) {
                                                "completed" -> {
                                                    showBorder = false
                                                    break
                                                }
                                                "error" -> {
                                                    showBorder = false
                                                    showToast(context, "Error fetching run status")
                                                    break
                                                }
                                            }
                                            delay(100)
                                        }

                                        if (runStatusViewModel.status.value != "completed") {
                                            showBorder = false
                                            //showToast(context, "Run status timed out")
                                        }
                                    }
                                } else {
                                    showToast(context, "Please enter a message")
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
}

@Composable
fun ImagePreview(imageUri: Uri?, onRemove: () -> Unit) {
    if (imageUri != null) {
        Box(
            modifier = Modifier
                .size(60.dp)
                .background(Color.LightGray, RoundedCornerShape(8.dp))
        ) {
            AsyncImage(
                model = imageUri,
                contentDescription = null,
                modifier = Modifier
                    .size(60.dp)
                    .align(Alignment.Center)
                    .padding(4.dp),
                contentScale = ContentScale.Crop
            )
            IconButton(
                onClick = onRemove,
                modifier = Modifier
                    .size(16.dp)
                    .align(Alignment.TopEnd)
                    .background(Color.Red, CircleShape)
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Remove Image",
                    tint = Color.White,
                    modifier = Modifier.size(16.dp)
                )
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
    }
}

@Composable
fun GlowingBorder(runStatusViewModel: RunStatusViewModel, showBorder: Boolean) {
    val status by runStatusViewModel.status.collectAsState()
    val colorMap = mapOf(
        "idle" to Color.Transparent,
        "queued" to Color(0xFFFAFAD2),      // Pastel Yellow (Light Goldenrod Yellow)
        "failed" to Color(0xFFFFA07A),      // Pastel Red (Light Salmon)
        "in_progress" to Color(0xFFADD8E6), // Pastel Blue (Light Blue)
        "completed" to Color(0xFF90EE90),   // Pastel Green (Light Green)
        "cancelled" to Color(0xFFD3D3D3),   // Pastel Gray (Light Gray)
        "expired" to Color(0xFFDA70D6)      // Pastel Purple (Orchid)
    )

    val color by animateColorAsState(
        targetValue = colorMap[status] ?: Color.Transparent,
        animationSpec = tween(durationMillis = 1000, easing = LinearEasing)
    )

    if (showBorder) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            drawRoundRect(
                color = color,
                size = size,
                style = Stroke(width = 10f)
            )
        }
    }
}