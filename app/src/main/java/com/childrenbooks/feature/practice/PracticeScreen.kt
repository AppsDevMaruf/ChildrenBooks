package com.childrenbooks.feature.practice

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.VolumeUp
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
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
import com.childrenbooks.core.designsystem.components.AnswerTile
import com.childrenbooks.core.designsystem.components.KidCard
import com.childrenbooks.core.designsystem.components.KidHomeButton
import com.childrenbooks.core.designsystem.components.KidIconButton
import com.childrenbooks.core.designsystem.components.KidTopBar
import com.childrenbooks.core.designsystem.components.StarCounter
import com.childrenbooks.data.content.WorldId

@Composable
fun PracticeRoute(
    onHome: () -> Unit,
    onFinished: (WorldId, Int, Int, String?) -> Unit,
    viewModel: PracticeViewModel = viewModel(factory = PracticeViewModel.Factory),
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val haptics = LocalHapticFeedback.current
    val lifecycle = LocalLifecycleOwner.current.lifecycle
    LaunchedEffect(viewModel, lifecycle) {
        lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
            viewModel.effects.collect { effect ->
                when (effect) {
                    PracticeEffect.Correct -> haptics.performHapticFeedback(HapticFeedbackType.Confirm)
                    PracticeEffect.Miss -> haptics.performHapticFeedback(HapticFeedbackType.Reject)
                    is PracticeEffect.Finished -> onFinished(effect.worldId, effect.setIndex, effect.stars, effect.stickerId)
                }
            }
        }
    }
    PracticeScreen(
        state = state,
        onPick = viewModel::onPick,
        onReplay = viewModel::onReplayPrompt,
        onHome = onHome,
    )
}

@Composable
fun PracticeScreen(
    state: PracticeUiState,
    onPick: (String) -> Unit,
    onReplay: () -> Unit,
    onHome: () -> Unit,
) {
    Column(
        Modifier
            .fillMaxSize()
            .background(KidColors.Background)
            .statusBarsPadding()
            .navigationBarsPadding(),
    ) {
        KidTopBar(
            leading = { KidHomeButton(onClick = onHome) },
            center = { QuestionProgress(current = state.questionIndex, count = state.questionCount) },
            trailing = { StarCounter(stars = state.stars, compact = true) },
        )
        if (state.loading) return@Column

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            PromptCard(state = state, onReplay = onReplay)
            state.tiles.chunked(2).forEach { row ->
                Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    row.forEach { tile ->
                        AnswerTile(
                            image = tile.image,
                            label = tile.label,
                            glyph = tile.glyph,
                            contentDescription = tile.label ?: tile.glyph.orEmpty(),
                            state = tile.state,
                            attempt = state.attempt,
                            onClick = { onPick(tile.cardId) },
                            modifier = Modifier.weight(1f),
                        )
                    }
                    if (row.size == 1) Spacer(Modifier.weight(1f))
                }
            }
            FeedbackLine(state)
        }
    }
}

@Composable
private fun QuestionProgress(current: Int, count: Int) {
    val description = stringResource(R.string.cd_question_position, current + 1, count.coerceAtLeast(1))
    Row(
        modifier = Modifier.semantics(mergeDescendants = true) { contentDescription = description },
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        repeat(count) { index ->
            val modifier = Modifier
                .width(44.dp)
                .height(12.dp)
            if (index <= current) {
                Box(modifier.background(KidColors.Primary, KidShapes.Pill))
            } else {
                Box(modifier.border(BorderStroke(2.dp, KidColors.Outline), KidShapes.Pill))
            }
        }
    }
}

@Composable
private fun PromptCard(state: PracticeUiState, onReplay: () -> Unit) {
    KidCard(onClick = null, role = null, shape = KidShapes.Tile) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 96.dp)
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            KidIconButton(
                icon = Icons.AutoMirrored.Rounded.VolumeUp,
                contentDescription = stringResource(R.string.cd_hear_question),
                onClick = onReplay,
                size = 64.dp,
                container = KidColors.Primary,
                contentColor = KidColors.OnPrimary,
                lipColor = KidColors.PrimaryLip,
            )
            Text(
                text = state.prompt,
                style = KidType.Headline,
                color = KidColors.Ink,
                modifier = Modifier.weight(1f),
            )
            when {
                state.promptGlyph != null -> Box(
                    modifier = Modifier
                        .size(60.dp)
                        .background(KidColors.PrimaryContainer, KidShapes.Chip),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(state.promptGlyph, style = KidType.Title.copy(fontFamily = AndikaFamily), color = KidColors.Primary)
                }
                state.promptImage != null -> Image(
                    painter = painterResource(state.promptImage),
                    contentDescription = null,
                    modifier = Modifier.size(88.dp),
                )
            }
        }
    }
}

@Composable
private fun FeedbackLine(state: PracticeUiState) {
    val (text, color) = when (state.feedback) {
        PracticeFeedback.Asking -> stringResource(R.string.practice_tap_answer) to KidColors.InkVariant
        PracticeFeedback.TryAgain -> stringResource(R.string.feedback_try_again) to KidColors.OnTryAgainContainer
        PracticeFeedback.Hint -> stringResource(R.string.feedback_hint, state.feedbackWord) to KidColors.OnTryAgainContainer
        PracticeFeedback.Correct -> stringResource(R.string.feedback_great) to KidColors.OnCorrectContainer
        PracticeFeedback.Revealed -> stringResource(R.string.feedback_reveal, state.feedbackWord) to KidColors.Ink
    }
    val big = state.feedback == PracticeFeedback.Correct
    Text(
        text = text,
        style = if (big) KidType.Display.copy(fontSize = KidType.Title.fontSize * 1.2f) else KidType.Label,
        color = color,
        textAlign = TextAlign.Center,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .semantics { liveRegion = LiveRegionMode.Polite },
    )
}
