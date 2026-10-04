package com.childrenbooks.core.designsystem.components

import androidx.annotation.DrawableRes
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.keyframes
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.Replay
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.childrenbooks.R
import com.childrenbooks.core.designsystem.AndikaFamily
import com.childrenbooks.core.designsystem.KidColors
import com.childrenbooks.core.designsystem.KidShapes
import com.childrenbooks.core.designsystem.KidType

enum class AnswerTileState { Idle, Correct, TryAgain, Dimmed, Hinted }

/**
 * The kid's "input field": a big picture choice. Never shows a red X or the word "wrong";
 * a miss wobbles, turns coral and resets; the right answer turns green with a check badge.
 *
 * [attempt] should change on every tap so a second miss on the same tile wobbles again.
 */
@Composable
fun AnswerTile(
    @DrawableRes image: Int,
    label: String?,
    contentDescription: String,
    state: AnswerTileState,
    attempt: Int,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    glyph: String? = null,
) {
    val rotation = remember { Animatable(0f) }
    LaunchedEffect(state, attempt) {
        if (state == AnswerTileState.TryAgain) {
            rotation.snapTo(0f)
            rotation.animateTo(
                targetValue = 0f,
                animationSpec = keyframes {
                    durationMillis = 600
                    -6f at 100
                    6f at 250
                    -3f at 400
                },
            )
        }
    }
    val hintAlpha by rememberInfiniteTransition(label = "hint").animateFloat(
        initialValue = 0.35f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(700), RepeatMode.Reverse),
        label = "hintAlpha",
    )

    val container = when (state) {
        AnswerTileState.Correct -> KidColors.CorrectContainer
        AnswerTileState.TryAgain -> KidColors.TryAgainContainer
        else -> KidColors.Surface
    }
    val border = when (state) {
        AnswerTileState.Correct -> BorderStroke(3.dp, KidColors.Correct)
        AnswerTileState.TryAgain -> BorderStroke(3.dp, KidColors.TryAgain)
        AnswerTileState.Hinted -> BorderStroke(3.dp, KidColors.Primary.copy(alpha = hintAlpha))
        else -> null
    }
    val stateText = when (state) {
        AnswerTileState.Correct -> stringResource(R.string.state_correct)
        AnswerTileState.TryAgain -> stringResource(R.string.state_try_again)
        else -> ""
    }

    KidCard(
        onClick = onClick,
        modifier = modifier
            .alpha(if (state == AnswerTileState.Dimmed) 0.4f else 1f)
            .graphicsLayer { rotationZ = rotation.value }
            .semantics(mergeDescendants = true) {
                this.contentDescription = contentDescription
                if (stateText.isNotEmpty()) stateDescription = stateText
            },
        color = container,
        shape = KidShapes.Answer,
        border = border,
        contentAlignment = Alignment.Center,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 168.dp)
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(6.dp, Alignment.CenterVertically),
        ) {
            if (glyph != null) {
                Text(text = glyph, style = KidType.Display.copy(fontFamily = AndikaFamily), color = KidColors.Ink)
            } else {
                Image(
                    painter = painterResource(image),
                    contentDescription = null,
                    contentScale = ContentScale.Fit,
                    modifier = Modifier.size(112.dp),
                )
            }
            if (label != null) {
                Text(
                    text = label,
                    style = KidType.Word.copy(fontSize = KidType.Label.fontSize, lineHeight = KidType.Label.lineHeight),
                    color = KidColors.Ink,
                    textAlign = TextAlign.Center,
                    maxLines = 1,
                )
            }
        }
        when (state) {
            AnswerTileState.Correct -> TileBadge(Icons.Rounded.Check, KidColors.Correct, Modifier.align(Alignment.TopEnd))
            AnswerTileState.TryAgain -> TileBadge(Icons.Rounded.Replay, KidColors.TryAgain, Modifier.align(Alignment.TopEnd))
            else -> Unit
        }
    }
}

@Composable
private fun TileBadge(icon: ImageVector, color: androidx.compose.ui.graphics.Color, modifier: Modifier) {
    Box(
        modifier = modifier
            .padding(10.dp)
            .size(34.dp)
            .background(color, CircleShape),
        contentAlignment = Alignment.Center,
    ) {
        Icon(icon, contentDescription = null, tint = KidColors.Surface, modifier = Modifier.size(20.dp))
    }
}
