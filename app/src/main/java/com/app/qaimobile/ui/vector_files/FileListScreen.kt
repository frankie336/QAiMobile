// FileListScreen.kt
package com.app.qaimobile.ui.vector_files

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Sort
import androidx.compose.material.icons.filled.ViewList
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.app.qaimobile.data.model.network.FileMetadata
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FileListScreen(navController: NavController, fileViewModel: FileViewModel = viewModel()) {
    val coroutineScope = rememberCoroutineScope()
    val filesState = fileViewModel.files.observeAsState(initial = emptyList())

    // Fetch the files when the composable is first displayed
    remember {
        coroutineScope.launch {
            fileViewModel.fetchFiles()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Vector store") },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { /* Add sort functionality here */ }) {
                        Icon(Icons.Default.Sort, contentDescription = "Sort")
                    }
                    IconButton(onClick = { /* Add grid/list toggle functionality here */ }) {
                        Icon(Icons.Default.ViewList, contentDescription = "View List")
                    }
                }
            )
        }
    ) { paddingValues ->
        Log.d("FileListScreen", "Rendering file list screen with padding values: $paddingValues")
        LazyColumn(
            contentPadding = paddingValues,
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            items(filesState.value) { file ->
                Log.d("FileListScreen", "Rendering file item: ${file.fileName}")
                FileItem(file = file, onDelete = { fileId ->
                    coroutineScope.launch {
                        fileViewModel.deleteFile(fileId)
                    }
                })
            }
        }
    }
}

@Composable
fun FileItem(file: FileMetadata, onDelete: (String) -> Unit) {
    var showDialog by remember { mutableStateOf(false) }

    if (showDialog) {
        DeleteConfirmationDialog(
            onConfirm = {
                onDelete(file.fileId)
                showDialog = false
            },
            onDismiss = { showDialog = false }
        )
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = Icons.Default.Description,
                contentDescription = "File Icon",
                modifier = Modifier.size(40.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(text = file.fileName, style = MaterialTheme.typography.bodyLarge, color = Color.Black)
                Text(text = "${file.size} bytes", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                Text(text = "Uploaded: ${file.uploadDate}", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                Text(text = "Status: ${file.status}", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
            }
        }
        IconButton(onClick = { showDialog = true }) {
            Icon(Icons.Default.MoreVert, contentDescription = "More Options")
        }
    }
}

@Composable
fun DeleteConfirmationDialog(onConfirm: () -> Unit, onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = "Delete File") },
        text = { Text(text = "Are you sure you want to delete this file?") },
        confirmButton = {
            Button(onClick = onConfirm) {
                Text("Delete")
            }
        },
        dismissButton = {
            Button(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
