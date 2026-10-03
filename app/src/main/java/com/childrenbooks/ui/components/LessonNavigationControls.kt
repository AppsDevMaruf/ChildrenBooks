package com.childrenbooks.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.automirrored.rounded.ArrowForward
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.childrenbooks.R
import com.childrenbooks.ui.theme.AppColors

@Composable
fun LessonNavigationButton(
    direction: LessonNavigationDirection,
    enabled: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    height: Dp = 50.dp,
    cornerRadius: Dp = 20.dp,
    containerColor: Color = if (direction == LessonNavigationDirection.Next) {
        AppColors.BrandBlue
    } else {
        Color(0xFFF1ECFF)
    },
    contentColor: Color = if (direction == LessonNavigationDirection.Next) {
        Color.White
    } else {
        AppColors.BrandPurple
    },
    disabledContainerColor: Color = Color(0xFFF2F3F7),
    disabledContentColor: Color = AppColors.Disabled,
    showRewardStar: Boolean = direction == LessonNavigationDirection.Next,
) {
    val isNext = direction == LessonNavigationDirection.Next

    Button(
        enabled = enabled,
        onClick = onClick,
        modifier = modifier.height(height),
        shape = RoundedCornerShape(cornerRadius),
        contentPadding = PaddingValues(horizontal = 14.dp, vertical = 0.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = containerColor,
            contentColor = contentColor,
            disabledContainerColor = disabledContainerColor,
            disabledContentColor = disabledContentColor,
        ),
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(7.dp),
        ) {
            if (!isNext) {
                Icon(
                    imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp),
                )
            }
            Text(
                text = stringResource(
                    if (isNext) R.string.action_next else R.string.action_previous,
                ),
                fontWeight = FontWeight.ExtraBold,
                maxLines = 1,
            )
            if (isNext) {
                Icon(
                    imageVector = Icons.AutoMirrored.Rounded.ArrowForward,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp),
                )
                if (showRewardStar) {
                    Icon(
                        imageVector = Icons.Rounded.Star,
                        contentDescription = null,
                        modifier = Modifier.size(17.dp),
                        tint = AppColors.BrandYellow,
                    )
                }
            }
        }
    }
}

enum class LessonNavigationDirection {
    Previous,
    Next,
}

@Composable
fun CircleTextButton(
    label: String,
    modifier: Modifier = Modifier,
    color: Color = AppColors.BrandBlue,
    size: Dp = 66.dp,
    chevronSize: Dp = 28.dp,
    onClick: () -> Unit,
) {
    TextButton(
        onClick = onClick,
        modifier = modifier
            .size(size)
            .clip(CircleShape)
            .background(Color.White),
    ) {
        when (label) {
            "‹" -> ChevronLeftIcon(color = color, size = chevronSize)
            "›" -> ChevronRightIcon(color = color, size = chevronSize)
            else -> Text(
                text = label,
                color = color,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.ExtraBold,
            )
        }
    }
}

@Composable
fun PagerControls(
    previousEnabled: Boolean,
    nextEnabled: Boolean,
    onPrevious: () -> Unit,
    onNext: () -> Unit,
    modifier: Modifier = Modifier,
    previousContainerColor: Color = Color.White,
    previousContentColor: Color = AppColors.BrandBlue,
    previousDisabledContainerColor: Color = Color.White,
    disabledContentColor: Color = AppColors.Disabled,
    nextContainerColor: Color = AppColors.BrandBlue,
    nextContentColor: Color = Color.White,
    topPadding: Dp = 6.dp,
    horizontalSpacing: Dp = 14.dp,
    buttonHeight: Dp = 52.dp,
    cornerRadius: Dp = 24.dp,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(top = topPadding),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(horizontalSpacing),
    ) {
        LessonNavigationButton(
            direction = LessonNavigationDirection.Previous,
            enabled = previousEnabled,
            onClick = onPrevious,
            modifier = Modifier.weight(1f),
            height = buttonHeight,
            cornerRadius = cornerRadius,
            containerColor = previousContainerColor,
            contentColor = previousContentColor,
            disabledContainerColor = previousDisabledContainerColor,
            disabledContentColor = disabledContentColor,
        )
        LessonNavigationButton(
            direction = LessonNavigationDirection.Next,
            enabled = nextEnabled,
            onClick = onNext,
            modifier = Modifier.weight(1f),
            height = buttonHeight,
            cornerRadius = cornerRadius,
            containerColor = nextContainerColor,
            contentColor = nextContentColor,
            disabledContentColor = disabledContentColor,
        )
    }
}
