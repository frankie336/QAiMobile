package com.app.qaimobile.ui.composables

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.app.qaimobile.ui.viewmodel.RunStatusViewModel
import androidx.compose.runtime.collectAsState
import kotlinx.coroutines.flow.MutableStateFlow

@Composable
fun AnimatedOrb(runStatusViewModel: RunStatusViewModel) {
    val status by runStatusViewModel.status.collectAsState()

    val color = when (status) {
        "queued" -> Color.Yellow
        "in_progress" -> Color.Blue
        "completed" -> Color.Green
        "failed" -> Color.Red
        else -> Color.Gray
    }

    val infiniteTransition = rememberInfiniteTransition()
    val scale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.2f,
        animationSpec = infiniteRepeatable(
            tween(durationMillis = 1000, easing = FastOutSlowInEasing),
            RepeatMode.Reverse
        )
    )

    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .size(48.dp)
            .padding(8.dp)
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            if (status == "idle") {
                drawRoundRect(
                    color = Color.Gray,
                    size = Size(size.width * 0.6f, size.height * 0.6f), // Elliptical shape
                    cornerRadius = CornerRadius(20f, 10f)
                )
            } else {
                drawRoundRect(
                    color = color,
                    size = Size(size.width * scale, size.height * 0.6f), // Elliptical shape
                    cornerRadius = CornerRadius(20f, 10f)
                )
            }
        }
    }
}