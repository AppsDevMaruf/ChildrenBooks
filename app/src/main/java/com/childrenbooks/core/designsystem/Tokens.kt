package com.childrenbooks.core.designsystem

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

/**
 * Colour tokens for the kid and grown-up zones. Every text pair here was checked
 * against WCAG 2.1 AA (4.5:1 for text, 3:1 for UI strokes); see the redesign blueprint.
 * Screens must use these tokens instead of raw colour literals.
 */
object KidColors {
    val Primary = Color(0xFF086AD8)
    val OnPrimary = Color(0xFFFFFFFF)
    val PrimaryContainer = Color(0xFFDCEBFF)
    val OnPrimaryContainer = Color(0xFF001B4C)
    val PrimaryLip = Color(0xFF0B2E66)

    val Secondary = Color(0xFFFFB020)
    val OnSecondary = Color(0xFF2C1A00)
    val SecondaryLip = Color(0xFFB86E00)
    val SecondaryContainer = Color(0xFFFFE9B8)
    val OnSecondaryContainer = Color(0xFF4A2C00)

    val Tertiary = Color(0xFF6A3DF0)
    val OnTertiary = Color(0xFFFFFFFF)
    val TertiaryContainer = Color(0xFFECE3FF)
    val OnTertiaryContainer = Color(0xFF2E1470)

    val Background = Color(0xFFF7FAFF)
    val Surface = Color(0xFFFFFFFF)
    val SurfaceMuted = Color(0xFFF2F4F7)
    val Ink = Color(0xFF101828)
    val InkVariant = Color(0xFF475467)
    val Outline = Color(0xFF667085)
    val Divider = Color(0xFFE4E7EC)
    val Track = Color(0xFFD0D5DD)
    val Lip = Color(0x1F101828)

    val Correct = Color(0xFF1F8A3A)
    val CorrectContainer = Color(0xFFDDF7E3)
    val OnCorrectContainer = Color(0xFF0F5A23)

    val TryAgain = Color(0xFFE0602A)
    val TryAgainContainer = Color(0xFFFFE3D6)
    val OnTryAgainContainer = Color(0xFF8A2E0C)

    val Error = Color(0xFFB42318)

    val Star = Color(0xFFFFB020)
    val StarStroke = Color(0xFFB86E00)

    val ChartBar = Color(0xFF4C86D9)
    val ChartToday = Color(0xFF0B2E66)

    val Sticker = Color(0xFFC2336A)
    val Scrim = Color(0x9E101828)
}

/** Warm, low-blue-light palette used by Listen in Bedtime mode. */
object BedtimeColors {
    val Background = Color(0xFF1B1424)
    val Surface = Color(0xFF271D33)
    val SurfaceHigh = Color(0xFF3A2D4A)
    val Text = Color(0xFFF5E9D7)
    val Muted = Color(0xFFC9B9A6)
    val Accent = Color(0xFFFFC56B)
    val OnAccent = Color(0xFF2A1A00)
}

/**
 * One learning world's colours: [ink] is for text on [container]; [accent] is for strokes,
 * icons and progress fills only, never small text.
 */
@Immutable
data class WorldPalette(
    val container: Color,
    val ink: Color,
    val accent: Color,
) {
    val lip: Color get() = ink.copy(alpha = 0.18f)
}

object WorldPalettes {
    val Letters = WorldPalette(Color(0xFFDCEBFF), Color(0xFF0B2E66), Color(0xFF086AD8))
    val Numbers = WorldPalette(Color(0xFFFFE9B8), Color(0xFF4A2C00), Color(0xFFB86E00))
    val Time = WorldPalette(Color(0xFFD6F5EC), Color(0xFF0B4A3A), Color(0xFF0E8A6B))
    val Colours = WorldPalette(Color(0xFFECE3FF), Color(0xFF2E1470), Color(0xFF6A3DF0))
    val Nature = WorldPalette(Color(0xFFDFF5D8), Color(0xFF1D4A12), Color(0xFF2F7D1F))
    val Food = WorldPalette(Color(0xFFFFE3D6), Color(0xFF6B2410), Color(0xFFC4501F))
    val Things = WorldPalette(Color(0xFFFFE0EC), Color(0xFF6B1238), Color(0xFFC2336A))
    val Listen = WorldPalette(Color(0xFFE3E6FF), Color(0xFF1C2366), Color(0xFF4A57D6))
}

object KidShapes {
    val Card = RoundedCornerShape(28.dp)
    val Tile = RoundedCornerShape(24.dp)
    val Answer = RoundedCornerShape(20.dp)
    val Chip = RoundedCornerShape(16.dp)
    val Pill = RoundedCornerShape(percent = 50)
    val GrownCard = RoundedCornerShape(16.dp)
    val GrownChip = RoundedCornerShape(8.dp)
    val Sheet = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp)
}

object KidSpacing {
    val Xs = 4.dp
    val S = 8.dp
    val M = 16.dp
    val L = 24.dp
    val Xl = 32.dp
    val Xxl = 48.dp
    val Gutter = 20.dp
}

object KidSizes {
    val TargetPrimary = 64.dp
    val TargetSecondary = 56.dp
    val TargetGrown = 48.dp
    val Lip = 4.dp
    val LipPressed = 1.dp
}
