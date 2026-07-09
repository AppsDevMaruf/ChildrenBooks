package com.marufalam.childrenbooks.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.marufalam.childrenbooks.ui.theme.AppColors
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.roundToInt
import kotlin.math.sin

@Composable
fun StarProgressBar(
    progress: Float,
    label: String,
    modifier: Modifier = Modifier,
    fillColor: Color = AppColors.BrandBlue,
    trackColor: Color = Color.White,
    starColor: Color = AppColors.BrandYellow,
    labelColor: Color = AppColors.BrandBlue,
) {
    val coercedProgress = progress.coerceIn(0f, 1f)
    val density = LocalDensity.current
    val starSize = 42.dp
    val trackHeight = 14.dp
    var trackWidthPx by remember { mutableIntStateOf(0) }
    val starSizePx = with(density) { starSize.toPx() }

    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .weight(1f)
                .height(starSize)
                .onSizeChanged { trackWidthPx = it.width },
            contentAlignment = Alignment.CenterStart,
        ) {
            Canvas(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(trackHeight)
                    .align(Alignment.Center),
            ) {
                val radius = size.height / 2f
                drawRoundRect(
                    color = Color(0x22000000),
                    topLeft = Offset(0f, size.height * 0.16f),
                    size = Size(size.width, size.height),
                    cornerRadius = CornerRadius(radius, radius),
                )
                drawRoundRect(
                    color = trackColor,
                    size = size,
                    cornerRadius = CornerRadius(radius, radius),
                )
                drawRoundRect(
                    color = Color(0xFFEAF2FF),
                    topLeft = Offset(size.height * 0.28f, size.height * 0.24f),
                    size = Size(size.width - size.height * 0.56f, size.height * 0.52f),
                    cornerRadius = CornerRadius(radius, radius),
                )
                val fillWidth = (size.width * coercedProgress).coerceAtLeast(size.height)
                drawRoundRect(
                    color = fillColor,
                    topLeft = Offset(size.height * 0.22f, size.height * 0.18f),
                    size = Size((fillWidth - size.height * 0.44f).coerceAtLeast(0f), size.height * 0.64f),
                    cornerRadius = CornerRadius(radius, radius),
                )
            }

            val starCenterX = ((trackWidthPx - starSizePx) * coercedProgress).roundToInt()
            StarThumb(
                color = starColor,
                modifier = Modifier
                    .offset { IntOffset(starCenterX, 0) }
                    .size(starSize),
            )
        }
        Text(
            text = label,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.ExtraBold,
            color = labelColor,
            modifier = Modifier.offset(x = 12.dp),
        )
    }
}

@Composable
private fun StarThumb(
    color: Color,
    modifier: Modifier = Modifier,
) {
    Canvas(
        modifier = modifier.drawBehind {
            drawCircle(
                color = Color(0x26000000),
                radius = size.minDimension * 0.34f,
                center = Offset(size.width * 0.52f, size.height * 0.58f),
            )
        },
    ) {
        val path = starPath(center = center, outerRadius = size.minDimension * 0.43f, innerRadius = size.minDimension * 0.22f)
        drawPath(path, color = Color(0xFFFFB800))
        drawPath(
            path = starPath(center = center.copy(y = center.y - size.minDimension * 0.025f), outerRadius = size.minDimension * 0.38f, innerRadius = size.minDimension * 0.20f),
            color = color,
        )
        drawPath(
            path = path,
            color = Color(0xFFFFCE45),
            style = Stroke(width = size.minDimension * 0.045f),
        )
    }
}

private fun starPath(center: Offset, outerRadius: Float, innerRadius: Float): Path {
    val path = Path()
    val points = 10
    for (i in 0 until points) {
        val angle = -PI / 2.0 + i * PI / 5.0
        val radius = if (i % 2 == 0) outerRadius else innerRadius
        val x = center.x + cos(angle).toFloat() * radius
        val y = center.y + sin(angle).toFloat() * radius
        if (i == 0) path.moveTo(x, y) else path.lineTo(x, y)
    }
    path.close()
    return path
}
