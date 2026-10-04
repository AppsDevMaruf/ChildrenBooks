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
                    text = "${stringResource(state.worldTitle)} · ${state.setTitle}",
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
                text = stringResource(R.string.lesson_position, state.index + 1, state.cards.size),
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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp, Alignment.CenterVertically),
        ) {
            when (card) {
                is LetterCard -> LetterContent(card, palette)
                is CountCard -> CountContent(card, palette)
                is PictureCard -> PictureContent(card, palette)
            }
            SpeakingIndicator(active = speaking, color = palette.accent)
        }
    }
}

/** Glyphs are a picture-like hero, so they are sized in dp and do not grow with font scale. */
@Composable
private fun glyphStyle(size: Dp) = KidType.Display.copy(
    fontFamily = AndikaFamily,
    fontSize = with(LocalDensity.current) { size.toSp() },
    lineHeight = with(LocalDensity.current) { (size * 1.08f).toSp() },
)

@Composable
private fun LetterContent(card: LetterCard, palette: WorldPalette) {
    Row(horizontalArrangement = Arrangement.spacedBy(22.dp), verticalAlignment = Alignment.Bottom) {
        Text(card.upper, style = glyphStyle(108.dp), color = palette.accent)
        Text(card.lower, style = glyphStyle(108.dp), color = palette.ink)
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
    )
}

@Composable
private fun CountContent(card: CountCard, palette: WorldPalette) {
    Text(card.numeral, style = glyphStyle(108.dp), color = palette.accent)
    PictureCircle(card.image, palette)
    Text(card.word, style = KidType.Word, color = KidColors.Ink, textAlign = TextAlign.Center)
}

@Composable
private fun PictureContent(card: PictureCard, palette: WorldPalette) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(280.dp)
            .background(palette.container, KidShapes.Tile),
        contentAlignment = Alignment.Center,
    ) {
        Image(
            painter = painterResource(card.image),
            contentDescription = null,
            contentScale = ContentScale.Fit,
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
        )
    }
    Spacer(Modifier.height(4.dp))
    Text(card.word, style = KidType.Word.copy(fontSize = KidType.Word.fontSize * 1.2f), color = KidColors.Ink, textAlign = TextAlign.Center)
}

@Composable
private fun PictureCircle(image: Int, palette: WorldPalette) {
    Box(
        modifier = Modifier
            .size(212.dp)
            .background(palette.container, CircleShape),
        contentAlignment = Alignment.Center,
    ) {
        Image(
            painter = painterResource(image),
            contentDescription = null,
            contentScale = ContentScale.Fit,
            modifier = Modifier.size(176.dp),
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
