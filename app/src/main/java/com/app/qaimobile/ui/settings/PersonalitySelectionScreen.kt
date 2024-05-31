package com.app.qaimobile.ui.settings

import android.annotation.SuppressLint
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.app.qaimobile.data.datastore.DataStoreManager
import kotlinx.coroutines.launch

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PersonalitySelectionScreen(navController: NavController) {
    val context = LocalContext.current
    val dataStoreManager = DataStoreManager(context)
    val coroutineScope = rememberCoroutineScope()

    val personalityMapping = mapOf(
        "Muse" to "artistic_personality",
        "Educator" to "educational_personality",
        "Quantum" to "scientific_technological_personality",
        "Explorer" to "general_base_personality"
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Select Personality") },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.LightGray,
                    titleContentColor = Color.Black
                )
            )
        },
        content = { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                personalityMapping.keys.chunked(2).forEach { rowItems ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        rowItems.forEach { personality ->
                            PersonalityButton(personality = personality) {
                                // Save personality and show toast
                                coroutineScope.launch {
                                    dataStoreManager.savePersonality(personalityMapping[personality] ?: "default_personality")
                                    Toast.makeText(context, "$personality selected", Toast.LENGTH_SHORT).show()
                                    // Navigate back or any other action if needed
                                }
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        }
    )
}

@Composable
fun PersonalityButton(personality: String, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .height(100.dp)
            .width(150.dp),
        shape = RoundedCornerShape(16.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color.LightGray
        ),
        elevation = ButtonDefaults.elevatedButtonElevation(8.dp)
    ) {
        Text(
            text = personality,
            fontSize = 18.sp,
            color = Color.Black,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )
    }
}
