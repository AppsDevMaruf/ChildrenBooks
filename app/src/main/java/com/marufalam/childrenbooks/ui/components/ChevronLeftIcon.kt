package com.marufalam.childrenbooks.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun ChevronLeftIcon(
    modifier: Modifier = Modifier,
    color: Color = Color(0xFF156FEA),
    size: Dp = 22.dp,
) {
    Canvas(modifier = modifier.size(size)) {
        val strokeWidth = this.size.minDimension * 0.16f
        drawLine(
            color = color,
            start = Offset(this.size.width * 0.62f, this.size.height * 0.22f),
            end = Offset(this.size.width * 0.34f, this.size.height * 0.50f),
            strokeWidth = strokeWidth,
            cap = StrokeCap.Round,
        )
        drawLine(
            color = color,
            start = Offset(this.size.width * 0.34f, this.size.height * 0.50f),
            end = Offset(this.size.width * 0.62f, this.size.height * 0.78f),
            strokeWidth = strokeWidth,
            cap = StrokeCap.Round,
        )
    }
}
