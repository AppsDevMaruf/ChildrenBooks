package com.childrenbooks.core.designsystem

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val ChildrenBooksColorScheme = lightColorScheme(
    primary = KidColors.Primary,
    onPrimary = KidColors.OnPrimary,
    primaryContainer = KidColors.PrimaryContainer,
    onPrimaryContainer = KidColors.OnPrimaryContainer,
    secondary = KidColors.Secondary,
    onSecondary = KidColors.OnSecondary,
    secondaryContainer = KidColors.SecondaryContainer,
    onSecondaryContainer = KidColors.OnSecondaryContainer,
    tertiary = KidColors.Tertiary,
    onTertiary = KidColors.OnTertiary,
    tertiaryContainer = KidColors.TertiaryContainer,
    onTertiaryContainer = KidColors.OnTertiaryContainer,
    background = KidColors.Background,
    onBackground = KidColors.Ink,
    surface = KidColors.Surface,
    onSurface = KidColors.Ink,
    surfaceVariant = KidColors.SurfaceMuted,
    onSurfaceVariant = KidColors.InkVariant,
    outline = KidColors.Outline,
    outlineVariant = KidColors.Divider,
    error = KidColors.Error,
    scrim = KidColors.Scrim,
)

@Composable
fun ChildrenBooksTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = ChildrenBooksColorScheme,
        typography = ChildrenBooksTypography,
        content = content,
    )
}
