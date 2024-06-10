package com.app.qaimobile.util

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.sp

fun parseMarkdownContent(content: String): AnnotatedString {
    return buildAnnotatedString {
        val lines = content.split("\n")
        var inCodeBlock = false

        for (line in lines) {
            when {
                line.startsWith("```") -> {
                    if (inCodeBlock) {
                        inCodeBlock = false
                    } else {
                        inCodeBlock = true
                    }
                }
                inCodeBlock -> {
                    append(line)
                    addStyle(
                        style = SpanStyle(
                            fontFamily = FontFamily.Monospace,
                            background = Color(0xFF333333),
                            color = Color.White
                        ),
                        start = length - line.length,
                        end = length
                    )
                    append("\n")
                }
                line.startsWith("######") -> {
                    append(line.removePrefix("######"))
                    addStyle(
                        style = SpanStyle(fontSize = 12.sp, fontWeight = FontWeight.Bold),
                        start = length - line.length,
                        end = length
                    )
                }
                line.startsWith("#####") -> {
                    append(line.removePrefix("#####"))
                    addStyle(
                        style = SpanStyle(fontSize = 14.sp, fontWeight = FontWeight.Bold),
                        start = length - line.length,
                        end = length
                    )
                }
                line.startsWith("####") -> {
                    append(line.removePrefix("####"))
                    addStyle(
                        style = SpanStyle(fontSize = 16.sp, fontWeight = FontWeight.Bold),
                        start = length - line.length,
                        end = length
                    )
                }
                line.startsWith("###") -> {
                    append(line.removePrefix("###"))
                    addStyle(
                        style = SpanStyle(fontSize = 18.sp, fontWeight = FontWeight.Bold),
                        start = length - line.length,
                        end = length
                    )
                }
                line.startsWith("##") -> {
                    append(line.removePrefix("##"))
                    addStyle(
                        style = SpanStyle(fontSize = 20.sp, fontWeight = FontWeight.Bold),
                        start = length - line.length,
                        end = length
                    )
                }
                line.startsWith("#") -> {
                    append(line.removePrefix("#"))
                    addStyle(
                        style = SpanStyle(fontSize = 24.sp, fontWeight = FontWeight.Bold),
                        start = length - line.length,
                        end = length
                    )
                }
                line.startsWith("**") && line.endsWith("**") -> {
                    append(line.removeSurrounding("**"))
                    addStyle(
                        style = SpanStyle(fontWeight = FontWeight.Bold),
                        start = length - line.length,
                        end = length
                    )
                }
                line.startsWith("*") && line.endsWith("*") -> {
                    append(line.removeSurrounding("*"))
                    addStyle(
                        style = SpanStyle(fontStyle = FontStyle.Italic),
                        start = length - line.length,
                        end = length
                    )
                }
                line.startsWith("~~") && line.endsWith("~~") -> {
                    append(line.removeSurrounding("~~"))
                    addStyle(
                        style = SpanStyle(textDecoration = TextDecoration.LineThrough),
                        start = length - line.length,
                        end = length
                    )
                }
                line.startsWith("1. ") || line.matches(Regex("^\\d+\\.\\s.*")) -> {
                    append(line.replaceFirst(Regex("^\\d+\\.\\s"), "• "))
                }
                line.startsWith("- ") || line.startsWith("* ") -> {
                    append(line.replaceFirst(Regex("^[-*]\\s"), "• "))
                }
                else -> {
                    val urlRegex = Regex("https?://\\S+")
                    var lastIndex = 0

                    urlRegex.findAll(line).forEach { result ->
                        append(line.substring(lastIndex, result.range.first))
                        pushStringAnnotation(
                            tag = "URL",
                            annotation = result.value
                        )
                        withStyle(style = SpanStyle(color = Color.Blue, textDecoration = TextDecoration.Underline)) {
                            append(result.value)
                        }
                        pop()
                        lastIndex = result.range.last + 1
                    }
                    append(line.substring(lastIndex))
                }
            }
            if (!inCodeBlock) {
                append("\n")
            }
        }
    }
}
