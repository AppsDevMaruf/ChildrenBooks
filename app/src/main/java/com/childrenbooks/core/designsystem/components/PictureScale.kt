package com.childrenbooks.core.designsystem.components

import androidx.compose.ui.geometry.isUnspecified
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import kotlin.math.abs

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
