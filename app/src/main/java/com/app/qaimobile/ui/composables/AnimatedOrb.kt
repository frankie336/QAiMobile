package com.app.qaimobile.ui.composables

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun AnimatedOrb(modifier: Modifier = Modifier) {
    val infiniteTransition = rememberInfiniteTransition()
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
        modifier = modifier
            .size(48.dp)
            .padding(8.dp)
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val canvasWidth = size.width
            val canvasHeight = size.height

            drawCircle(
                color = Color.Cyan,
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
