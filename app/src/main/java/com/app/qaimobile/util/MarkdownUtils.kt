package com.app.qaimobile.util

import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.compose.ui.graphics.Color

fun parseMarkdownContent(content: String): AnnotatedString {
    return buildAnnotatedString {
        val lines = content.split("\n")
        var inCodeBlock = false
        var language = ""

        for (line in lines) {
            when {
                line.startsWith("```") -> {
                    if (inCodeBlock) {
                        // End of code block
                        inCodeBlock = false
                    } else {
                        // Start of code block
                        inCodeBlock = true
                        language = line.removePrefix("```")
                    }
                }
                inCodeBlock -> {
                    append(line)
                    addStyle(
                        style = SpanStyle(
                            fontFamily = FontFamily.Monospace,
                            background = Color(0xFF333333), // Dark background color
                            color = Color.White // White text color
                        ),
                        start = length - line.length,
                        end = length
                    )
                    append("\n")
                }
                line.startsWith("**") -> {
                    append(line.removeSurrounding("**"))
                    addStyle(
                        style = SpanStyle(fontWeight = FontWeight.Bold),
                        start = length - line.length,
                        end = length
                    )
                }
                line.startsWith("# ") -> {
                    append(line.removePrefix("# "))
                    addStyle(
                        style = SpanStyle(fontSize = 24.sp, fontWeight = FontWeight.Bold),
                        start = length - line.length,
                        end = length
                    )
                }
                else -> {
                    append(line)
                }
            }
            if (!inCodeBlock) {
                append("\n")
            }
        }
    }
}
