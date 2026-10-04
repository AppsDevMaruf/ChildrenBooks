package com.childrenbooks.core.designsystem.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.childrenbooks.R
import com.childrenbooks.core.designsystem.KidColors
import com.childrenbooks.core.designsystem.KidShapes
import com.childrenbooks.core.designsystem.KidSizes
import com.childrenbooks.core.designsystem.KidType

/** Visual roles for kid buttons. */
enum class KidButtonStyle(val container: Color, val content: Color, val lip: Color) {
    Primary(KidColors.Primary, KidColors.OnPrimary, KidColors.PrimaryLip),
    Secondary(KidColors.Secondary, KidColors.OnSecondary, KidColors.SecondaryLip),
    Neutral(KidColors.Surface, KidColors.Ink, KidColors.Lip),
}

/** Pill button for the kid zone. Primary actions are 64 dp tall. */
@Composable
fun KidButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    style: KidButtonStyle = KidButtonStyle.Primary,
    leadingIcon: ImageVector? = null,
    trailingIcon: ImageVector? = null,
    height: Dp = KidSizes.TargetPrimary,
    enabled: Boolean = true,
) {
    KidCard(
        onClick = onClick,
        modifier = modifier.alpha(if (enabled) 1f else 0.4f),
        color = style.container,
        lipColor = style.lip,
        shape = KidShapes.Pill,
        enabled = enabled,
        contentAlignment = Alignment.Center,
    ) {
        Row(
            modifier = Modifier
                .height(height)
                .padding(horizontal = 24.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            if (leadingIcon != null) {
                Icon(leadingIcon, contentDescription = null, tint = style.content, modifier = Modifier.size(24.dp))
            }
            Text(
                text = text,
                style = KidType.Label,
                color = style.content,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            if (trailingIcon != null) {
                Icon(trailingIcon, contentDescription = null, tint = style.content, modifier = Modifier.size(24.dp))
            }
        }
    }
}

/** Round icon button; [contentDescription] is required because the button has no text. */
@Composable
fun KidIconButton(
    icon: ImageVector,
    contentDescription: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    size: Dp = KidSizes.TargetSecondary,
    container: Color = KidColors.Surface,
    contentColor: Color = KidColors.Ink,
    lipColor: Color = KidColors.Lip,
    enabled: Boolean = true,
) {
    KidCard(
        onClick = onClick,
        modifier = modifier.alpha(if (enabled) 1f else 0.4f),
        color = container,
        lipColor = lipColor,
        shape = CircleShape,
        enabled = enabled,
        contentAlignment = Alignment.Center,
    ) {
        Box(Modifier.size(size), contentAlignment = Alignment.Center) {
            Icon(
                imageVector = icon,
                contentDescription = contentDescription,
                tint = contentColor,
                modifier = Modifier.size(size * 0.46f),
            )
        }
    }
}

@Composable
fun KidHomeButton(onClick: () -> Unit, modifier: Modifier = Modifier) {
    KidIconButton(
        icon = Icons.Rounded.Home,
        contentDescription = stringResource(R.string.cd_home),
        onClick = onClick,
        modifier = modifier,
    )
}
