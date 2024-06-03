package com.app.qaimobile.ui.composables

import androidx.compose.animation.animateColor
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.app.qaimobile.ui.viewmodel.RunStatusViewModel

@Composable
fun AnimatedOrb(runStatusViewModel: RunStatusViewModel) {
    val status by runStatusViewModel.status.collectAsState()

    val infiniteTransition = rememberInfiniteTransition()

    // Default colors for steady state
    val defaultColor1 = Color.Cyan
    val defaultColor2 = Color.Magenta

    // Colors based on status
    val color1 = when (status) {
        "queued", "failed" -> Color(0xFFFFB3B3) // Pastel Red
        "in_progress" -> Color(0xFFFFCC66) // Pastel Orange
        "completed" -> Color(0xFFB3FFB3) // Pastel Green
        "cancelled", "expired" -> Color(0xFFD9D9D9) // Pastel Gray
        else -> defaultColor1 // Default to steady state colors
    }

    val color2 = when (status) {
        "queued", "failed" -> Color(0xFFFFE0B3) // Pastel Orange
        "in_progress" -> Color(0xFFB3FFB3) // Pastel Green
        "completed" -> Color(0xFFD9D9D9) // Pastel Gray
        "cancelled", "expired" -> Color(0xFFFFB3B3) // Pastel Red
        else -> defaultColor2 // Default to steady state colors
    }

    // Determine the colors based on the status
    val animatedColor1 by infiniteTransition.animateColor(
        initialValue = if (status == "idle") defaultColor1 else color1,
        targetValue = if (status == "idle") defaultColor2 else color2,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 2000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        )
    )
    val animatedColor2 by infiniteTransition.animateColor(
        initialValue = if (status == "idle") defaultColor2 else color2,
        targetValue = if (status == "idle") defaultColor1 else color1,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 2000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        )
    )

    val scale by infiniteTransition.animateFloat(
        initialValue = 0.5f,
        targetValue = 1.5f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 2000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        )
    )

    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .size(48.dp)
            .padding(8.dp)
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val canvasWidth = size.width
            val canvasHeight = size.height

            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(animatedColor1, animatedColor2),
                    center = Offset(canvasWidth / 2, canvasHeight / 2),
                    radius = canvasWidth * 0.3f * scale
                ),
                radius = canvasWidth * 0.2f * scale,
                center = Offset(canvasWidth / 2, canvasHeight / 2)
            )

            drawCircle(
                color = Color.White.copy(alpha = 0.5f),
                radius = canvasWidth * 0.1f * scale,
                center = Offset(canvasWidth / 2, canvasHeight / 2)
            )
        }
    }
}
