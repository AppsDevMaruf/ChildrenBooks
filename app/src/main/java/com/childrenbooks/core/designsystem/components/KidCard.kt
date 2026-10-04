package com.childrenbooks.core.designsystem.components

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.IntOffset
import com.childrenbooks.core.designsystem.KidColors
import com.childrenbooks.core.designsystem.KidShapes
import com.childrenbooks.core.designsystem.KidSizes

private const val PressDurationMillis = 90
private const val PressedScale = 0.97f

/**
 * The kid zone's tactile surface: a solid face sitting on a 4 dp "lip" in a darker tone.
 * Pressing compresses the lip to 1 dp and scales the card slightly, so every tap is felt.
 * No blur shadows: they lower contrast and cost GPU time on low-end phones.
 *
 * When [onClick] is null the card is static (no press feedback, no click semantics).
 */
@Composable
fun KidCard(
    onClick: (() -> Unit)?,
    modifier: Modifier = Modifier,
    color: Color = KidColors.Surface,
    lipColor: Color = KidColors.Lip,
    shape: Shape = KidShapes.Card,
    border: BorderStroke? = null,
    enabled: Boolean = true,
    onClickLabel: String? = null,
    role: Role? = Role.Button,
    contentAlignment: Alignment = Alignment.TopStart,
    content: @Composable BoxScope.() -> Unit,
) {
    val interactionSource = remember { MutableInteractionSource() }
    val pressed by interactionSource.collectIsPressedAsState()
    val interactive = onClick != null && enabled
    val lip by animateDpAsState(
        targetValue = if (pressed && interactive) KidSizes.LipPressed else KidSizes.Lip,
        animationSpec = tween(PressDurationMillis),
        label = "kidCardLip",
    )
    val scale by animateFloatAsState(
        targetValue = if (pressed && interactive) PressedScale else 1f,
        animationSpec = tween(PressDurationMillis),
        label = "kidCardScale",
    )

    Box(
        modifier = modifier.graphicsLayer {
            scaleX = scale
            scaleY = scale
        },
        propagateMinConstraints = true,
    ) {
        Box(
            Modifier
                .matchParentSize()
                .padding(top = KidSizes.Lip)
                .background(lipColor, shape),
        )
        Box(
            modifier = Modifier
                .padding(bottom = KidSizes.Lip)
                .offset { IntOffset(0, (KidSizes.Lip - lip).roundToPx()) }
                .clip(shape)
                .background(color)
                .then(if (border != null) Modifier.border(border, shape) else Modifier)
                .then(
                    if (onClick != null) {
                        Modifier.clickable(
                            interactionSource = interactionSource,
                            indication = LocalIndication.current,
                            enabled = enabled,
                            onClickLabel = onClickLabel,
                            role = role,
                            onClick = onClick,
                        )
                    } else {
                        Modifier
                    },
                ),
            contentAlignment = contentAlignment,
            content = content,
        )
    }
}
