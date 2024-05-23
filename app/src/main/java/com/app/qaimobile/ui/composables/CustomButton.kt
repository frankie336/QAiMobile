package com.app.qaimobile.ui.composables

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.app.qaimobile.R
import com.app.qaimobile.ui.theme.CustomBoldTextStyle

@Composable
fun CustomButton(
    modifier: Modifier = Modifier,
    btnText: String,
    contentColor: Color = MaterialTheme.colorScheme.onSecondary,
    containerColor: Color = MaterialTheme.colorScheme.secondary,
    borderRadius: Dp = 4.dp,
    contentPadding: PaddingValues = PaddingValues(16.dp),
    textStyle: TextStyle = CustomBoldTextStyle.copy(fontSize = 18.sp),
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        shape = RoundedCornerShape(borderRadius),
        modifier = modifier,
        colors = ButtonDefaults.buttonColors(
            containerColor = containerColor,
            contentColor = contentColor
        ),
        contentPadding = contentPadding
    ) {
        ComposeTextView(
            text = btnText, style = textStyle
        )
    }
}