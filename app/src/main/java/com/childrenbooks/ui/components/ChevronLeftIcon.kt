package com.childrenbooks.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ChevronLeft
import androidx.compose.material3.Icon
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun ChevronLeftIcon(
    modifier: Modifier = Modifier,
    color: Color = Color(0xFF156FEA),
    size: Dp = 22.dp,
) {
    Icon(
        imageVector = Icons.Rounded.ChevronLeft,
        contentDescription = null,
        modifier = modifier.size(size),
        tint = color,
    )
}
