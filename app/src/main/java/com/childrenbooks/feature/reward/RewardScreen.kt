@file:OptIn(ExperimentalComposeUiApi::class)

package com.childrenbooks.feature.reward

import com.childrenbooks.core.designsystem.components.KidIconButton
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.annotation.StringRes
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.KeyboardArrowRight
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.Replay
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material.icons.rounded.StarOutline
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.childrenbooks.R
import com.childrenbooks.appViewModelFactory
import com.childrenbooks.core.audio.Narrator
import com.childrenbooks.core.designsystem.KidColors
import com.childrenbooks.core.designsystem.KidType
import com.childrenbooks.core.designsystem.components.KidButton
import com.childrenbooks.core.designsystem.components.KidButtonStyle
import com.childrenbooks.core.designsystem.components.KidTitleChip
import com.childrenbooks.core.designsystem.components.StarCounter
import com.childrenbooks.core.designsystem.components.StickerImage
import com.childrenbooks.core.designsystem.palette
import com.childrenbooks.data.content.AudioClip
import com.childrenbooks.data.content.ContentRepository
import com.childrenbooks.data.content.Sticker
import com.childrenbooks.data.content.WorldId
import com.childrenbooks.data.progress.ProgressRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlin.random.Random

data class RewardUiState(
    val worldId: WorldId,
    @param:StringRes val worldTitle: Int,
    val setIndex: Int,
    val setTitle: String,
    val starsEarned: Int,
    val sticker: Sticker?,
    val totalStars: Int = 0,
    val childName: String = "",
    val nextSetIndex: Int? = null,
    val nextSetTitle: String? = null,
)

class RewardViewModel(
    savedStateHandle: SavedStateHandle,
    content: ContentRepository,
    progressRepository: ProgressRepository,
    private val narrator: Narrator,
    strings: (Int, Array<out Any>) -> String,
) : ViewModel() {
    private val worldId = WorldId.fromKey(savedStateHandle.get<String>(WorldIdArg).orEmpty()) ?: WorldId.Letters
    private val world = content.world(worldId)
    private val setIndex = (savedStateHandle.get<Int>(SetIndexArg) ?: 0).coerceIn(0, world.sets.lastIndex)
    private val next = world.sets.getOrNull(setIndex + 1)

    private val initial = RewardUiState(
        worldId = worldId,
        worldTitle = world.title,
        setIndex = setIndex,
        setTitle = world.sets[setIndex].title,
        starsEarned = (savedStateHandle.get<Int>(StarsArg) ?: 0).coerceIn(0, 3),
        sticker = savedStateHandle.get<String>(StickerArg)?.takeIf { it.isNotBlank() }?.let(content::sticker),
        nextSetIndex = next?.index,
        nextSetTitle = next?.title,
    )

    val uiState: StateFlow<RewardUiState> = combine(progressRepository.progress, progressRepository.settings) { progress, settings ->
        initial.copy(totalStars = progress.stars, childName = settings.childName)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), initial)

    init {
        viewModelScope.launch {
            val name = progressRepository.settings.first().childName
            val line = if (name.isBlank()) strings(R.string.reward_great_job, emptyArray<Any>()) else strings(R.string.reward_great_job_name, arrayOf(name))
            narrator.speak(AudioClip.Spoken(line))
        }
    }

    override fun onCleared() = narrator.stop()

    companion object {
        const val WorldIdArg = "worldId"
        const val SetIndexArg = "setIndex"
        const val StarsArg = "stars"
        const val StickerArg = "sticker"

        val Factory = appViewModelFactory { container ->
            RewardViewModel(
                savedStateHandle = createSavedStateHandle(),
                content = container.contentRepository,
                progressRepository = container.progressRepository,
                narrator = container.narrator,
                strings = { id, args -> container.resources.getString(id, *args) },
            )
        }
    }
}

@Composable
fun RewardRoute(
    onNextSet: (WorldId, Int) -> Unit,
    onPlayAgain: (WorldId, Int) -> Unit,
    onHome: () -> Unit,
    viewModel: RewardViewModel = viewModel(factory = RewardViewModel.Factory),
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    RewardScreen(
        state = state,
        onNextSet = { state.nextSetIndex?.let { onNextSet(state.worldId, it) } },
        onPlayAgain = { onPlayAgain(state.worldId, state.setIndex) },
        onHome = onHome,
    )
}

@Composable
fun RewardScreen(
    state: RewardUiState,
    onNextSet: () -> Unit,
    onPlayAgain: () -> Unit,
    onHome: () -> Unit,
) {
    val palette = state.worldId.palette()
    Box(
        Modifier
            .fillMaxSize()
            .background(KidColors.Background),
    ) {
        Confetti(Modifier.fillMaxSize())
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            Text(
                text = if (state.childName.isBlank()) stringResource(R.string.reward_great_job) else stringResource(R.string.reward_great_job_name, state.childName),
                style = KidType.Display.copy(fontSize = KidType.Display.fontSize * 0.86f),
                color = KidColors.Ink,
                textAlign = TextAlign.Center,
                modifier = Modifier.semantics { liveRegion = LiveRegionMode.Polite },
            )
            KidTitleChip(
                text = "${stringResource(state.worldTitle)} · ${state.setTitle}",
                container = palette.container,
                content = palette.ink,
            )
            RewardStars(earned = state.starsEarned)
            state.sticker?.let { sticker ->
                StickerImage(
                    image = sticker.image,
                    contentDescription = stringResource(R.string.cd_new_sticker, sticker.name),
                    rotation = -5f,
                    isNew = true,
                    corner = 30.dp,
                    modifier = Modifier
                        .padding(top = 8.dp)
                        .size(width = 236.dp, height = 184.dp),
                )
            }
            StarCounter(stars = state.totalStars)
            if (state.nextSetIndex != null && state.nextSetTitle != null) {
                KidButton(
                    text = stringResource(R.string.reward_next_set, state.nextSetTitle),
                    onClick = onNextSet,
                    trailingIcon = Icons.AutoMirrored.Rounded.KeyboardArrowRight,
                    modifier = Modifier.fillMaxWidth(),
                )
            }
            // Secondary actions are round icon buttons with a label underneath, so the
            // words never get cut off on narrow phones.
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
            ) {
                LabeledIconButton(
                    icon = Icons.Rounded.Replay,
                    label = stringResource(R.string.reward_play_again),
                    onClick = onPlayAgain,
                )
                LabeledIconButton(
                    icon = Icons.Rounded.Home,
                    label = stringResource(R.string.nav_home),
                    onClick = onHome,
                )
            }
        }
    }
}

@Composable
private fun LabeledIconButton(icon: ImageVector, label: String, onClick: () -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        KidIconButton(
            icon = icon,
            contentDescription = label,
            onClick = onClick,
            size = 60.dp,
        )
        // The button already announces the label to TalkBack.
        Text(
            text = label,
            style = KidType.Caption,
            color = KidColors.InkVariant,
            maxLines = 1,
            modifier = Modifier.clearAndSetSemantics { },
        )
    }
}

/** Stars fill one by one with a bouncy pop. */
@Composable
private fun RewardStars(earned: Int) {
    val description = pluralStringResource(R.plurals.stars_earned, earned, earned)
    Row(
        modifier = Modifier.semantics(mergeDescendants = true) { contentDescription = description },
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.Bottom,
    ) {
        repeat(3) { index ->
            val scale = remember { Animatable(0f) }
            LaunchedEffect(Unit) {
                delay(300L + index * 250L)
                scale.animateTo(1f, spring(dampingRatio = 0.55f, stiffness = 380f))
            }
            val big = index == 1
            Icon(
                imageVector = if (index < earned) Icons.Rounded.Star else Icons.Rounded.StarOutline,
                contentDescription = null,
                tint = if (index < earned) KidColors.Star else KidColors.Track,
                modifier = Modifier
                    .padding(bottom = if (big) 14.dp else 0.dp)
                    .size(if (big) 100.dp else 76.dp)
                    .graphicsLayer {
                        scaleX = scale.value
                        scaleY = scale.value
                        rotationZ = when (index) {
                            0 -> -10f
                            2 -> 10f
                            else -> 0f
                        }
                    },
            )
        }
    }
}

private data class Particle(val x: Float, val delay: Float, val speed: Float, val spin: Float, val color: Int, val round: Boolean)

/** A short burst of confetti (1.2 s); with animations turned off it simply does not move. */
@Composable
private fun Confetti(modifier: Modifier = Modifier) {
    val colors = listOf(KidColors.Primary, KidColors.Secondary, KidColors.Tertiary, KidColors.Sticker, KidColors.Correct)
    val particles = remember {
        val random = Random(7)
        List(28) {
            Particle(
                x = random.nextFloat(),
                delay = random.nextFloat() * 0.3f,
                speed = 0.5f + random.nextFloat() * 0.5f,
                spin = random.nextFloat() * 360f,
                color = random.nextInt(5),
                round = random.nextBoolean(),
            )
        }
    }
    val progress = remember { Animatable(0f) }
    LaunchedEffect(Unit) { progress.animateTo(1f, tween(1_400, easing = LinearEasing)) }
    Canvas(modifier) {
        val p = progress.value
        if (p >= 1f) return@Canvas
        particles.forEach { particle ->
            val local = ((p - particle.delay) / (1f - particle.delay)).coerceIn(0f, 1f)
            if (local <= 0f) return@forEach
            val x = particle.x * size.width
            val y = -20f + local * particle.speed * size.height * 0.75f
            val alpha = 1f - local
            val color = colors[particle.color].copy(alpha = alpha)
            if (particle.round) {
                drawCircle(color, radius = 6.dp.toPx(), center = Offset(x, y))
            } else {
                rotate(particle.spin + local * 360f, pivot = Offset(x, y)) {
                    drawRect(color, topLeft = Offset(x - 5.dp.toPx(), y - 9.dp.toPx()), size = Size(10.dp.toPx(), 18.dp.toPx()))
                }
            }
        }
    }
}
