package com.childrenbooks.core.designsystem.components

import androidx.compose.ui.geometry.isUnspecified
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.coerceAtLeast
import androidx.compose.ui.unit.dp
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.sqrt

/**
 * Scene photos whose shape is close to the frame's fill it edge to edge (crop). Pictures with a
 * very different shape, such as the wide word banners for days and colours, are fitted instead
 * so their words are never cut off.
 */
fun Painter.pictureScale(frameAspectRatio: Float, tolerance: Float = 0.45f): ContentScale {
    val size = intrinsicSize
    if (size.isUnspecified || size.width <= 0f || size.height <= 0f) return ContentScale.Fit
    val ratio = size.width / size.height
    return if (abs(ratio - frameAspectRatio) <= tolerance) ContentScale.Crop else ContentScale.Fit
}

/**
 * Padding for a picture fitted inside a circle of [diameter]. Instead of one fixed inset, a wide
 * word banner grows until its corners sit [margin] inside the edge, so its word is as big as the
 * circle allows.
 */
fun Painter.circleFitPadding(diameter: Dp, margin: Dp = 4.dp): Dp {
    val size = intrinsicSize
    if (size.isUnspecified || size.width <= 0f || size.height <= 0f) return diameter * 0.15f
    val ratio = max(size.width / size.height, size.height / size.width)
    // A ratio:1 box touches the circle when its long side is d / sqrt(1 + 1 / ratio²).
    val longSide = (diameter - margin * 2) / sqrt(1f + 1f / (ratio * ratio))
    return ((diameter - longSide) / 2).coerceAtLeast(margin)
}
