package com.childrenbooks.feature.lesson

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.rounded.KeyboardArrowRight
import androidx.compose.material.icons.automirrored.rounded.VolumeUp
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.text.style.LineHeightStyle
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.repeatOnLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.childrenbooks.R
import com.childrenbooks.core.designsystem.AndikaFamily
import com.childrenbooks.core.designsystem.KidColors
import com.childrenbooks.core.designsystem.KidShapes
import com.childrenbooks.core.designsystem.KidType
import com.childrenbooks.core.designsystem.WorldPalette
import com.childrenbooks.core.designsystem.components.KidButton
import com.childrenbooks.core.designsystem.components.KidButtonStyle
import com.childrenbooks.core.designsystem.components.KidCard
import com.childrenbooks.core.designsystem.components.KidHomeButton
import com.childrenbooks.core.designsystem.components.KidIconButton
import com.childrenbooks.core.designsystem.components.KidTitleChip
import com.childrenbooks.core.designsystem.components.KidTopBar
import com.childrenbooks.core.designsystem.components.SetDots
import com.childrenbooks.core.designsystem.components.StarCounter
import com.childrenbooks.core.designsystem.palette
import com.childrenbooks.data.content.Card
import com.childrenbooks.data.content.CountCard
import com.childrenbooks.data.content.LetterCard
import com.childrenbooks.data.content.PictureCard
import com.childrenbooks.data.content.WorldId

@Composable
fun LessonRoute(
    onHome: () -> Unit,
    onOpenPractice: (WorldId, Int) -> Unit,
    viewModel: LessonViewModel = viewModel(factory = LessonViewModel.Factory),
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val haptics = LocalHapticFeedback.current
    val lifecycle = LocalLifecycleOwner.current.lifecycle
    LaunchedEffect(viewModel, lifecycle) {
        lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
            viewModel.effects.collect { effect ->
                when (effect) {
                    LessonEffect.PageTick -> haptics.performHapticFeedback(HapticFeedbackType.SegmentTick)
                    is LessonEffect.OpenPractice -> onOpenPractice(effect.worldId, effect.setIndex)
                }
            }
        }
    }
    LessonScreen(state = state, onEvent = viewModel::onEvent, onHome = onHome)
}

@Composable
fun LessonScreen(
    state: LessonUiState,
    onEvent: (LessonEvent) -> Unit,
    onHome: () -> Unit,
) {
    val palette = state.worldId.palette()
    val pagerState = rememberPagerState(initialPage = state.index) { state.cards.size }

    LaunchedEffect(pagerState) {
        snapshotFlow { pagerState.settledPage }.collect { onEvent(LessonEvent.CardSettled(it)) }
    }
    LaunchedEffect(state.index) {
        if (pagerState.settledPage != state.index) pagerState.animateScrollToPage(state.index)
    }

    Column(
        Modifier
            .fillMaxSize()
            .background(KidColors.Background)
            .statusBarsPadding(),
    ) {
        KidTopBar(
            leading = { KidHomeButton(onClick = onHome) },
            center = {
                KidTitleChip(
                    text = stringResource(state.worldTitle),
                    container = palette.container,
                    content = palette.ink,
                )
            },
            trailing = { StarCounter(stars = state.stars, compact = true) },
        )

        HorizontalPager(
            state = pagerState,
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            key = { page -> state.cards[page].id },
        ) { page ->
            LessonCardView(
                card = state.cards[page],
                palette = palette,
                speaking = state.isSpeaking && page == state.index,
                onTap = { onEvent(LessonEvent.CardTapped) },
                modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp),
            )
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            SetDots(count = state.cards.size, current = state.index, activeColor = palette.accent)
            Text(
                text = stringResource(R.string.lesson_set_position, state.setTitle, state.index + 1, state.cards.size),
                style = KidType.Caption,
                color = KidColors.InkVariant,
            )
        }

        LessonActionBar(
            isFirst = state.index == 0,
            isLast = state.isLast,
            onBack = { onEvent(LessonEvent.BackTapped) },
            onHear = { onEvent(LessonEvent.CardTapped) },
            onNext = { onEvent(LessonEvent.NextTapped) },
        )
    }
}

@Composable
private fun LessonCardView(
    card: Card,
    palette: WorldPalette,
    speaking: Boolean,
    onTap: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val description = when (card) {
        is LetterCard -> stringResource(R.string.cd_letter_card, card.upper, card.phrase)
        is CountCard -> stringResource(R.string.cd_count_card, card.numeral, card.word)
        is PictureCard -> card.word
    }
    KidCard(
        onClick = onTap,
        onClickLabel = stringResource(R.string.cd_hear_again),
        modifier = modifier
            .fillMaxSize()
            .semantics(mergeDescendants = true) { contentDescription = description },
        color = KidColors.Surface,
        lipColor = palette.lip,
        shape = KidShapes.Card,
        contentAlignment = Alignment.Center,
    ) {
        // The glyph and word keep their size; the picture takes whatever height is left,
        // so short screens shrink the picture instead of clipping the word.
        BoxWithConstraints(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp),
        ) {
            val fit = (maxHeight / CardDesignHeight).coerceIn(MinCardFit, 1f)
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp * fit, Alignment.CenterVertically),
            ) {
                when (card) {
                    is LetterCard -> LetterContent(card, palette, fit)
                    is CountCard -> CountContent(card, palette, fit)
                    is PictureCard -> PictureContent(card, palette)
                }
                SpeakingIndicator(active = speaking, color = palette.accent)
            }
        }
    }
}

/** Height the card content is designed for; shorter cards scale the glyph down. */
private val CardDesignHeight = 440.dp
private const val MinCardFit = 0.6f
private val GlyphSize = 108.dp
private val PictureMax = 212.dp

/**
 * A big teaching glyph. Andika's line box is much taller than its letters, so the text sits
 * in a fixed-height box sized to the ink (cap height + descender) and may overflow it.
 */
@Composable
private fun Glyph(text: String, size: Dp, color: Color) {
    val density = LocalDensity.current
    Box(Modifier.height(size), contentAlignment = Alignment.Center) {
        Text(
            text = text,
            style = KidType.Display.copy(
                fontFamily = AndikaFamily,
                fontSize = with(density) { size.toSp() },
                lineHeight = with(density) { size.toSp() },
                lineHeightStyle = LineHeightStyle(
                    alignment = LineHeightStyle.Alignment.Proportional,
                    trim = LineHeightStyle.Trim.Both,
                ),
            ),
            color = color,
            maxLines = 1,
            softWrap = false,
            modifier = Modifier.wrapContentHeight(unbounded = true),
        )
    }
}

@Composable
private fun ColumnScope.LetterContent(card: LetterCard, palette: WorldPalette, fit: Float) {
    Row(horizontalArrangement = Arrangement.spacedBy(22.dp), verticalAlignment = Alignment.CenterVertically) {
        Glyph(card.upper, GlyphSize * fit, palette.accent)
        Glyph(card.lower, GlyphSize * fit, palette.ink)
    }
    PictureCircle(card.image, palette)
    Text(
        text = buildAnnotatedString {
            if (card.phrase.startsWith(card.upper)) {
                withStyle(SpanStyle(color = palette.accent)) { append(card.upper) }
                append(card.phrase.removePrefix(card.upper))
            } else {
                append(card.phrase)
            }
        },
        style = KidType.Word,
        color = KidColors.Ink,
        textAlign = TextAlign.Center,
        maxLines = 2,
    )
}

@Composable
private fun ColumnScope.CountContent(card: CountCard, palette: WorldPalette, fit: Float) {
    Glyph(card.numeral, GlyphSize * fit, palette.accent)
    PictureCircle(card.image, palette)
    Text(card.word, style = KidType.Word, color = KidColors.Ink, textAlign = TextAlign.Center, maxLines = 2)
}

@Composable
private fun ColumnScope.PictureContent(card: PictureCard, palette: WorldPalette) {
    // Photos are 4:3 scenes with their own background, so they fill a rounded 4:3 tile edge to
    // edge; the world tint only shows behind pictures that have transparent areas.
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .weight(1f, fill = false)
            .heightIn(max = 300.dp),
        contentAlignment = Alignment.Center,
    ) {
        Image(
            painter = painterResource(card.image),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .aspectRatio(4f / 3f)
                .clip(KidShapes.Tile)
                .background(palette.container),
        )
    }
    Text(
        text = card.word,
        style = KidType.Word.copy(fontSize = KidType.Word.fontSize * 1.2f),
        color = KidColors.Ink,
        textAlign = TextAlign.Center,
        maxLines = 2,
    )
}

/** Square picture on a tinted circle; up to 212 dp, smaller when the card is short. */
@Composable
private fun ColumnScope.PictureCircle(image: Int, palette: WorldPalette) {
    Box(
        modifier = Modifier
            .weight(1f, fill = false)
            .sizeIn(maxWidth = PictureMax, maxHeight = PictureMax)
            .aspectRatio(1f, matchHeightConstraintsFirst = true)
            .background(palette.container, CircleShape),
        contentAlignment = Alignment.Center,
    ) {
        Image(
            painter = painterResource(image),
            contentDescription = null,
            contentScale = ContentScale.Fit,
            modifier = Modifier.fillMaxSize(0.83f),
        )
    }
}

@Composable
private fun SpeakingIndicator(active: Boolean, color: androidx.compose.ui.graphics.Color) {
    val transition = rememberInfiniteTransition(label = "speaking")
    val wave by transition.animateFloat(
        initialValue = 0.4f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(320), RepeatMode.Reverse),
        label = "speakingWave",
    )
    Row(
        modifier = Modifier.height(20.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalAlignment = Alignment.Bottom,
    ) {
        listOf(0.45f, 1f, 0.7f).forEachIndexed { index, base ->
            val factor = if (active) (if (index == 1) wave else 1.4f - wave) else base
            Box(
                Modifier
                    .width(4.dp)
                    .height((20 * factor.coerceIn(0.3f, 1f)).dp)
                    .background(color.copy(alpha = if (active) 1f else 0.4f), KidShapes.Pill),
            )
        }
    }
}

@Composable
private fun LessonActionBar(
    isFirst: Boolean,
    isLast: Boolean,
    onBack: () -> Unit,
    onHear: () -> Unit,
    onNext: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(KidColors.Surface, KidShapes.Sheet)
            .navigationBarsPadding()
            .padding(horizontal = 24.dp, vertical = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        KidIconButton(
            icon = Icons.AutoMirrored.Rounded.KeyboardArrowLeft,
            contentDescription = stringResource(R.string.cd_back),
            onClick = onBack,
            enabled = !isFirst,
            container = KidColors.SurfaceMuted,
        )
        KidIconButton(
            icon = Icons.AutoMirrored.Rounded.VolumeUp,
            contentDescription = stringResource(R.string.cd_hear_again),
            onClick = onHear,
            size = 72.dp,
            container = KidColors.Primary,
            contentColor = KidColors.OnPrimary,
            lipColor = KidColors.PrimaryLip,
        )
        KidButton(
            text = stringResource(if (isLast) R.string.lesson_lets_play else R.string.action_next),
            onClick = onNext,
            style = KidButtonStyle.Secondary,
            trailingIcon = Icons.AutoMirrored.Rounded.KeyboardArrowRight,
        )
    }
}
