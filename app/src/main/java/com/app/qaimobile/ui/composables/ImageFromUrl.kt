// ui/composables/ImageFromUrl.kt
package com.app.qaimobile.ui.composables

import androidx.compose.foundation.Image
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import coil.compose.rememberImagePainter

@Composable
fun ImageFromUrl(url: String, modifier: Modifier = Modifier) {
    val painter = rememberImagePainter(url)
    Image(
        painter = painter,
        contentDescription = null,
        modifier = modifier,
        contentScale = ContentScale.Crop
    )
}
