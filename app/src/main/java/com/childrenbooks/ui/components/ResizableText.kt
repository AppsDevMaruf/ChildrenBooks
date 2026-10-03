package com.childrenbooks.ui.components

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.sp

@Composable
fun ResizableText(
    text: String,
    modifier: Modifier = Modifier,
    color: Color = Color.Unspecified,
    style: TextStyle = MaterialTheme.typography.titleLarge,
    fontWeight: FontWeight? = null,
    textAlign: TextAlign? = null,
    maxLines: Int = 1,
    minTextSize: TextUnit = 12.sp,
    maxTextSize: TextUnit = style.fontSize.takeIf { it != TextUnit.Unspecified } ?: 24.sp,
    resizeStep: TextUnit = 1.sp,
) {
    var textSize by remember(text, maxTextSize) { mutableStateOf(maxTextSize) }

    Text(
        text = text,
        modifier = modifier,
        color = color,
        style = style.copy(fontSize = textSize),
        fontWeight = fontWeight,
        textAlign = textAlign,
        maxLines = maxLines,
        overflow = TextOverflow.Clip,
        onTextLayout = { result ->
            if (result.hasVisualOverflow && textSize.value > minTextSize.value) {
                textSize = (textSize.value - resizeStep.value)
                    .coerceAtLeast(minTextSize.value)
                    .sp
            }
        },
    )
}
