package com.childrenbooks.feature.world

import com.childrenbooks.core.designsystem.components.pictureScale
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material.icons.rounded.StarOutline
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.childrenbooks.R
import com.childrenbooks.appViewModelFactory
import com.childrenbooks.core.designsystem.AndikaFamily
import com.childrenbooks.core.designsystem.KidColors
import com.childrenbooks.core.designsystem.KidSizes
import com.childrenbooks.core.designsystem.KidType
import com.childrenbooks.core.designsystem.WorldPalette
import com.childrenbooks.core.designsystem.components.KidCard
import com.childrenbooks.core.designsystem.components.KidHomeButton
import com.childrenbooks.core.designsystem.components.KidTopBar
import com.childrenbooks.core.designsystem.components.StarCounter
import com.childrenbooks.core.designsystem.palette
import com.childrenbooks.data.content.ContentRepository
import com.childrenbooks.data.content.CountCard
import com.childrenbooks.data.content.LetterCard
import com.childrenbooks.data.content.WorldId
import com.childrenbooks.data.progress.ProgressRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

data class SetNode(
    val index: Int,
    val title: String,
    @param:DrawableRes val cover: Int,
    val glyph: String?,
    val stars: Int?,
    val isCurrent: Boolean,
)

data class WorldUiState(
    val worldId: WorldId,
    @param:StringRes val title: Int,
    @param:DrawableRes val art: Int?,
    val glyph: String?,
    val stars: Int = 0,
    val setsDone: Int = 0,
    val nodes: List<SetNode> = emptyList(),
)

class WorldViewModel(
    savedStateHandle: SavedStateHandle,
    content: ContentRepository,
    progressRepository: ProgressRepository,
) : ViewModel() {
    private val world = content.world(
        WorldId.fromKey(savedStateHandle.get<String>(WorldIdArg).orEmpty()) ?: WorldId.Letters,
    )

    private val initial = WorldUiState(world.id, world.title, world.art, world.glyph)

    val uiState: StateFlow<WorldUiState> = progressRepository.progress.map { progress ->
        val current = world.sets.firstOrNull { !progress.isSetDone(it.key) }?.index
        initial.copy(
            stars = progress.stars,
            setsDone = world.sets.count { progress.isSetDone(it.key) },
            nodes = world.sets.map { set ->
                val first = set.cards.first()
                SetNode(
                    index = set.index,
                    title = set.title,
                    cover = set.cover,
                    glyph = when (first) {
                        is LetterCard -> first.upper
                        is CountCard -> first.numeral
                        else -> null
                    },
                    stars = progress.setStars[set.key],
                    isCurrent = set.index == current,
                )
            },
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), initial)

    companion object {
        const val WorldIdArg = "worldId"

        val Factory = appViewModelFactory { container ->
            WorldViewModel(createSavedStateHandle(), container.contentRepository, container.progressRepository)
        }
    }
}

@Composable
fun WorldRoute(
    onHome: () -> Unit,
    onOpenSet: (WorldId, Int) -> Unit,
    viewModel: WorldViewModel = viewModel(factory = WorldViewModel.Factory),
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    WorldScreen(state = state, onHome = onHome, onOpenSet = { onOpenSet(state.worldId, it) })
}

@Composable
fun WorldScreen(
    state: WorldUiState,
    onHome: () -> Unit,
    onOpenSet: (Int) -> Unit,
) {
    val palette = state.worldId.palette()
    Column(
        Modifier
            .fillMaxSize()
            .background(KidColors.Background),
    ) {
        WorldHeader(state, palette, onHome)
        Box(Modifier.fillMaxSize()) {
            Canvas(Modifier.fillMaxSize()) {
                drawLine(
                    color = palette.accent.copy(alpha = 0.35f),
                    start = Offset(size.width / 2f, 0f),
                    end = Offset(size.width / 2f, size.height),
                    strokeWidth = 6.dp.toPx(),
                    cap = StrokeCap.Round,
                    pathEffect = PathEffect.dashPathEffect(floatArrayOf(1f, 14.dp.toPx())),
                )
            }
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .navigationBarsPadding(),
                contentPadding = PaddingValues(horizontal = 32.dp, vertical = 24.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp),
            ) {
                itemsIndexed(state.nodes, key = { _, node -> node.index }) { position, node ->
                    SetNodeRow(
                        node = node,
                        palette = palette,
                        alignEnd = position % 2 == 1,
                        onClick = { onOpenSet(node.index) },
                    )
                }
            }
        }
    }
}

@Composable
private fun WorldHeader(state: WorldUiState, palette: WorldPalette, onHome: () -> Unit) {
    val total = state.nodes.size.coerceAtLeast(1)
    Column(
        Modifier
            .fillMaxWidth()
            .background(palette.container, RoundedCornerShape(bottomStart = 36.dp, bottomEnd = 36.dp))
            .statusBarsPadding()
            .padding(bottom = 24.dp),
    ) {
        KidTopBar(
            leading = { KidHomeButton(onClick = onHome) },
            trailing = { StarCounter(stars = state.stars, compact = true) },
        )
        Row(
            modifier = Modifier.padding(horizontal = 24.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(stringResource(state.title), style = KidType.Title.copy(fontSize = KidType.Title.fontSize * 1.2f), color = palette.ink)
                Text(stringResource(R.string.world_sets_done, state.setsDone, state.nodes.size), style = KidType.Caption, color = palette.ink)
                Box(
                    Modifier
                        .padding(top = 6.dp)
                        .width(170.dp)
                        .height(12.dp)
                        .background(KidColors.Surface, CircleShape),
                ) {
                    Box(
                        Modifier
                            .fillMaxWidth(state.setsDone.toFloat() / total)
                            .height(12.dp)
                            .background(palette.accent, CircleShape),
                    )
                }
            }
            Box(
                modifier = Modifier
                    .size(128.dp)
                    .background(KidColors.Surface, CircleShape),
                contentAlignment = Alignment.Center,
            ) {
                if (state.glyph != null) {
                    Text(state.glyph, style = KidType.Display.copy(fontFamily = AndikaFamily), color = palette.accent)
                } else if (state.art != null) {
                    Image(painterResource(state.art), contentDescription = null, modifier = Modifier.size(80.dp))
                }
            }
        }
    }
}

@Composable
private fun SetNodeRow(
    node: SetNode,
    palette: WorldPalette,
    alignEnd: Boolean,
    onClick: () -> Unit,
) {
    val done = node.stars != null
    val status = when {
        done -> stringResource(R.string.set_status_done, node.stars ?: 0)
        node.isCurrent -> stringResource(R.string.set_status_play_now)
        else -> stringResource(R.string.set_status_new)
    }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .semantics(mergeDescendants = true) { contentDescription = "${node.title}, $status" },
        horizontalArrangement = if (alignEnd) Arrangement.End else Arrangement.Start,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (alignEnd) {
            NodeLabel(node, palette, alignEnd = true)
            Spacer(Modifier.width(16.dp))
        }
        NodeCircle(node, palette, onClick)
        if (!alignEnd) {
            Spacer(Modifier.width(16.dp))
            NodeLabel(node, palette, alignEnd = false)
        }
    }
}

@Composable
private fun NodeCircle(node: SetNode, palette: WorldPalette, onClick: () -> Unit) {
    val done = node.stars != null
    val size: Dp = when {
        node.isCurrent -> 96.dp
        done -> 88.dp
        else -> 80.dp
    }
    Box(contentAlignment = Alignment.Center) {
        if (node.isCurrent) {
            val pulse by rememberInfiniteTransition(label = "nodePulse").animateFloat(
                initialValue = 1f,
                targetValue = 1.18f,
                animationSpec = infiniteRepeatable(tween(900), RepeatMode.Reverse),
                label = "nodePulseScale",
            )
            Box(
                Modifier
                    .size(116.dp)
                    .graphicsLayer {
                        scaleX = pulse
                        scaleY = pulse
                    }
                    .border(BorderStroke(4.dp, KidColors.Secondary.copy(alpha = 0.45f)), CircleShape),
            )
        }
        KidCard(
            onClick = onClick,
            modifier = Modifier.size(width = size, height = size + KidSizes.Lip),
            color = when {
                node.isCurrent -> KidColors.Secondary
                done -> palette.accent
                else -> KidColors.Surface
            },
            lipColor = when {
                node.isCurrent -> KidColors.SecondaryLip
                done -> KidColors.Ink.copy(alpha = 0.35f)
                else -> palette.lip
            },
            shape = CircleShape,
            contentAlignment = Alignment.Center,
        ) {
            when {
                node.isCurrent -> Icon(Icons.Rounded.PlayArrow, contentDescription = null, tint = KidColors.OnSecondary, modifier = Modifier.size(44.dp))
                done -> Icon(Icons.Rounded.Check, contentDescription = null, tint = KidColors.Surface, modifier = Modifier.size(40.dp))
                node.glyph != null -> Text(node.glyph, style = KidType.Title.copy(fontFamily = AndikaFamily), color = palette.ink)
                else -> {
                    val coverPainter = painterResource(node.cover)
                    val coverScale = coverPainter.pictureScale(1f)
                    Image(
                        painter = coverPainter,
                        contentDescription = null,
                        contentScale = coverScale,
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(6.dp)
                            .clip(CircleShape)
                            .then(if (coverScale == ContentScale.Fit) Modifier.padding(8.dp) else Modifier),
                    )
                }
            }
        }
    }
}

@Composable
private fun NodeLabel(node: SetNode, palette: WorldPalette, alignEnd: Boolean) {
    Column(horizontalAlignment = if (alignEnd) Alignment.End else Alignment.Start) {
        Text(node.title, style = KidType.Label, color = palette.ink, maxLines = 2)
        when {
            node.stars != null -> Row(horizontalArrangement = Arrangement.spacedBy(2.dp)) {
                repeat(3) { index ->
                    Icon(
                        imageVector = if (index < node.stars) Icons.Rounded.Star else Icons.Rounded.StarOutline,
                        contentDescription = null,
                        tint = KidColors.Star,
                        modifier = Modifier.size(20.dp),
                    )
                }
            }
            node.isCurrent -> Text(
                text = stringResource(R.string.set_status_play_now),
                style = KidType.Small,
                color = KidColors.OnSecondaryContainer,
                modifier = Modifier
                    .padding(top = 4.dp)
                    .background(KidColors.SecondaryContainer, CircleShape)
                    .padding(horizontal = 10.dp, vertical = 2.dp),
            )
        }
    }
}
