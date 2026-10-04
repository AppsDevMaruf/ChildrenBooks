// pluralStringResource was @ExperimentalComposeUiApi in older Compose UI; opting in is harmless if it is stable.
@file:OptIn(ExperimentalComposeUiApi::class)

package com.childrenbooks.core.designsystem.components

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateIntAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.childrenbooks.R
import com.childrenbooks.core.designsystem.KidColors
import com.childrenbooks.core.designsystem.KidShapes
import com.childrenbooks.core.designsystem.KidType

/** Three-slot top bar: leading (Home), center (title chip or progress), trailing (stars or lock). */
@Composable
fun KidTopBar(
    modifier: Modifier = Modifier,
    horizontalPadding: Dp = 20.dp,
    leading: @Composable () -> Unit = {},
    center: @Composable () -> Unit = {},
    trailing: @Composable () -> Unit = {},
) {
    // The centre slot is measured last and limited to the space left between the two side slots
    // (kept symmetric so it stays visually centred), so a long title can never slide under them.
    Layout(
        content = {
            Box { leading() }
            Box { center() }
            Box { trailing() }
        },
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = horizontalPadding, vertical = 12.dp)
            .heightIn(min = 60.dp),
    ) { measurables, constraints ->
        val loose = constraints.copy(minWidth = 0, minHeight = 0)
        val lead = measurables[0].measure(loose)
        val trail = measurables[2].measure(loose)
        val gap = 8.dp.roundToPx()
        val side = maxOf(lead.width, trail.width)
        val centreMax = (constraints.maxWidth - 2 * (side + gap)).coerceAtLeast(0)
        val mid = measurables[1].measure(loose.copy(maxWidth = centreMax))
        val height = maxOf(constraints.minHeight, lead.height, trail.height, mid.height)
        layout(constraints.maxWidth, height) {
            lead.placeRelative(0, (height - lead.height) / 2)
            trail.placeRelative(constraints.maxWidth - trail.width, (height - trail.height) / 2)
            mid.placeRelative((constraints.maxWidth - mid.width) / 2, (height - mid.height) / 2)
        }
    }
}

/** The only number a child sees. Rolls up when the count changes. */
@Composable
fun StarCounter(
    stars: Int,
    modifier: Modifier = Modifier,
    compact: Boolean = false,
) {
    val animated by animateIntAsState(stars, animationSpec = tween(600), label = "starCounter")
    val description = pluralStringResource(R.plurals.stars_count, stars, stars)
    KidCard(
        onClick = null,
        modifier = modifier.clearAndSetSemantics { contentDescription = description },
        color = KidColors.SecondaryContainer,
        lipColor = KidColors.OnSecondaryContainer.copy(alpha = 0.16f),
        shape = KidShapes.Pill,
        role = null,
        contentAlignment = Alignment.Center,
    ) {
        Row(
            modifier = Modifier
                .height(if (compact) 40.dp else 44.dp)
                .padding(start = 12.dp, end = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(
                imageVector = Icons.Rounded.Star,
                contentDescription = null,
                tint = KidColors.Star,
                modifier = Modifier.size(if (compact) 22.dp else 26.dp),
            )
            Text(
                text = animated.toString(),
                style = KidType.Label.copy(fontWeight = FontWeight.ExtraBold, fontFeatureSettings = "tnum"),
                color = KidColors.OnSecondaryContainer,
            )
        }
    }
}

/** Title chip used in the centre of the top bar. */
@Composable
fun KidTitleChip(
    text: String,
    container: Color,
    content: Color,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .background(container, KidShapes.Pill)
            .padding(horizontal = 18.dp, vertical = 8.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(text = text, style = KidType.Caption, color = content, maxLines = 1, overflow = TextOverflow.Ellipsis)
    }
}

/** Position within a lesson set: the current card is a wider pill. */
@Composable
fun SetDots(
    count: Int,
    current: Int,
    modifier: Modifier = Modifier,
    activeColor: Color = KidColors.Primary,
) {
    val description = stringResource(R.string.cd_card_position, current + 1, count)
    Row(
        modifier = modifier.semantics { contentDescription = description },
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        repeat(count) { index ->
            val width by animateDpAsState(
                targetValue = if (index == current) 28.dp else 12.dp,
                animationSpec = tween(250),
                label = "setDot",
            )
            val color = when {
                index == current -> activeColor
                index < current -> activeColor.copy(alpha = 0.45f)
                else -> KidColors.Track
            }
            Box(
                Modifier
                    .width(width)
                    .height(12.dp)
                    .background(color, KidShapes.Pill),
            )
        }
    }
}
