package com.childrenbooks.core.designsystem

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.childrenbooks.R

/** UI and headings. Bundled (not a downloadable font) so it renders the same on every device. */
val NunitoFamily = FontFamily(
    Font(R.font.nunito_regular, FontWeight.Normal),
    Font(R.font.nunito_bold, FontWeight.Bold),
    Font(R.font.nunito_extrabold, FontWeight.ExtraBold),
)

/**
 * Anything a child is learning to read: letters, words under pictures, trace guides.
 * Andika (SIL, OFL) has the single-storey "a" and "g" children are taught to write.
 */
val AndikaFamily = FontFamily(
    Font(R.font.andika_regular, FontWeight.Normal),
    Font(R.font.andika_bold, FontWeight.Bold),
)

/** Numbers in the grown-ups report: the platform font with tabular figures. */
private val NumericFamily = FontFamily.Default
private const val TabularFigures = "tnum"

object KidType {
    val Display = TextStyle(fontFamily = NunitoFamily, fontWeight = FontWeight.ExtraBold, fontSize = 44.sp, lineHeight = 52.sp)
    val Word = TextStyle(fontFamily = AndikaFamily, fontWeight = FontWeight.Bold, fontSize = 32.sp, lineHeight = 40.sp)
    val Title = TextStyle(fontFamily = NunitoFamily, fontWeight = FontWeight.ExtraBold, fontSize = 28.sp, lineHeight = 36.sp)
    val Headline = TextStyle(fontFamily = NunitoFamily, fontWeight = FontWeight.ExtraBold, fontSize = 22.sp, lineHeight = 28.sp)
    val Label = TextStyle(fontFamily = NunitoFamily, fontWeight = FontWeight.Bold, fontSize = 20.sp, lineHeight = 26.sp)
    val Body = TextStyle(fontFamily = NunitoFamily, fontWeight = FontWeight.Bold, fontSize = 18.sp, lineHeight = 26.sp)
    val Caption = TextStyle(fontFamily = NunitoFamily, fontWeight = FontWeight.Bold, fontSize = 16.sp, lineHeight = 22.sp)
    val Small = TextStyle(fontFamily = NunitoFamily, fontWeight = FontWeight.Bold, fontSize = 14.sp, lineHeight = 20.sp)
}

object GrownType {
    val MetricXl = TextStyle(fontFamily = NumericFamily, fontWeight = FontWeight.Bold, fontSize = 40.sp, lineHeight = 48.sp, fontFeatureSettings = TabularFigures)
    val Metric = TextStyle(fontFamily = NumericFamily, fontWeight = FontWeight.Bold, fontSize = 26.sp, lineHeight = 32.sp, fontFeatureSettings = TabularFigures)
    val Title = TextStyle(fontFamily = NunitoFamily, fontWeight = FontWeight.Bold, fontSize = 20.sp, lineHeight = 28.sp)
    val Section = TextStyle(fontFamily = NunitoFamily, fontWeight = FontWeight.Bold, fontSize = 16.sp, lineHeight = 22.sp)
    val Body = TextStyle(fontFamily = NunitoFamily, fontWeight = FontWeight.Normal, fontSize = 16.sp, lineHeight = 24.sp)
    val Label = TextStyle(fontFamily = NunitoFamily, fontWeight = FontWeight.Bold, fontSize = 14.sp, lineHeight = 20.sp)
    val Caption = TextStyle(fontFamily = NumericFamily, fontWeight = FontWeight.Medium, fontSize = 12.sp, lineHeight = 16.sp, fontFeatureSettings = TabularFigures)
}

internal val ChildrenBooksTypography = Typography(
    displayLarge = KidType.Display,
    displayMedium = KidType.Display.copy(fontSize = 36.sp, lineHeight = 44.sp),
    displaySmall = KidType.Title.copy(fontSize = 32.sp, lineHeight = 40.sp),
    headlineLarge = KidType.Title,
    headlineMedium = KidType.Headline,
    headlineSmall = KidType.Label,
    titleLarge = GrownType.Title,
    titleMedium = KidType.Body,
    titleSmall = GrownType.Section,
    bodyLarge = GrownType.Body,
    bodyMedium = GrownType.Body.copy(fontSize = 14.sp, lineHeight = 20.sp),
    bodySmall = GrownType.Body.copy(fontSize = 12.sp, lineHeight = 16.sp),
    labelLarge = GrownType.Label,
    labelMedium = GrownType.Label.copy(fontSize = 12.sp, lineHeight = 16.sp),
    labelSmall = GrownType.Label.copy(fontSize = 11.sp, lineHeight = 16.sp),
)
