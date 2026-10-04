package com.childrenbooks.feature.listen

import androidx.core.view.WindowCompat
import androidx.compose.ui.platform.LocalView
import androidx.compose.runtime.DisposableEffect
import android.content.ContextWrapper
import android.content.Context
import android.app.Activity
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.rounded.KeyboardArrowRight
import androidx.compose.material.icons.rounded.Bedtime
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.LightMode
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material.icons.rounded.Stop
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.text.style.TextOverflow
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
import com.childrenbooks.core.designsystem.BedtimeColors
import com.childrenbooks.core.designsystem.KidColors
import com.childrenbooks.core.designsystem.KidShapes
import com.childrenbooks.core.designsystem.KidType
import com.childrenbooks.core.designsystem.WorldPalettes
import com.childrenbooks.core.designsystem.components.KidCard
import com.childrenbooks.core.designsystem.components.KidHomeButton
import com.childrenbooks.core.designsystem.components.KidIconButton
import com.childrenbooks.core.designsystem.components.KidTopBar
import com.childrenbooks.data.content.AudioClip
import com.childrenbooks.data.progress.ProgressRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

/** Colours for Listen; Bedtime swaps to a warm, low-blue-light dark palette. */
@Immutable
data class ListenColors(
    val background: Color,
    val surface: Color,
    val text: Color,
    val muted: Color,
    val accent: Color,
    val onAccent: Color,
    val lip: Color,
) {
    companion object {
        val Day = ListenColors(
            background = KidColors.Background,
            surface = KidColors.Surface,
            text = KidColors.Ink,
            muted = KidColors.InkVariant,
            accent = WorldPalettes.Listen.accent,
            onAccent = KidColors.Surface,
            lip = WorldPalettes.Listen.lip,
        )
        val Bedtime = ListenColors(
            background = BedtimeColors.Background,
            surface = BedtimeColors.Surface,
            text = BedtimeColors.Text,
            muted = BedtimeColors.Muted,
            accent = BedtimeColors.Accent,
            onAccent = BedtimeColors.OnAccent,
            lip = Color(0x59000000),
        )
    }
}

data class ListenUiState(
    val shelf: ListenShelf? = null,
    val items: List<ListenItem> = emptyList(),
    val bedtime: Boolean = false,
    val playingTitle: String? = null,
) {
    val colors: ListenColors get() = if (bedtime) ListenColors.Bedtime else ListenColors.Day
}

class ListenViewModel(
    savedStateHandle: SavedStateHandle,
    private val progressRepository: ProgressRepository,
    private val narrator: Narrator,
) : ViewModel() {
    private val shelf = savedStateHandle.get<String>(ShelfArg)?.let { ListenShelf.fromRouteId(it) }
    private val playing = MutableStateFlow<String?>(null)

    val uiState: StateFlow<ListenUiState> = combine(
        progressRepository.settings.onEach { narrator.setBedtime(it.bedtime) },
        playing,
        narrator.isSpeaking,
    ) { settings, title, speaking ->
        ListenUiState(
            shelf = shelf,
            items = shelf?.let(ListenContent::itemsFor).orEmpty(),
            bedtime = settings.bedtime,
            playingTitle = if (speaking) title else null,
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), ListenUiState(shelf = shelf))

    fun toggle(item: ListenItem) {
        if (uiState.value.playingTitle == item.title) {
            narrator.stop()
            playing.value = null
        } else {
            playing.value = item.title
            narrator.speak(AudioClip.Spoken(item.script))
        }
    }

    fun setBedtime(enabled: Boolean) {
        viewModelScope.launch { progressRepository.updateSettings { it.copy(bedtime = enabled) } }
    }

    override fun onCleared() = narrator.stop()

    companion object {
        const val ShelfArg = "shelf"

        val Factory = appViewModelFactory { container ->
            ListenViewModel(createSavedStateHandle(), container.progressRepository, container.narrator)
        }
    }
}

@Composable
fun ListenHomeRoute(
    onHome: () -> Unit,
    onOpenShelf: (ListenShelf) -> Unit,
    viewModel: ListenViewModel = viewModel(factory = ListenViewModel.Factory),
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    DarkSystemBars(enabled = state.bedtime)
    val colors = state.colors
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(colors.background)
            .statusBarsPadding()
            .navigationBarsPadding(),
        contentPadding = PaddingValues(start = 20.dp, end = 20.dp, bottom = 24.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        item {
            KidTopBar(
                horizontalPadding = 0.dp,
                leading = { ListenNavButton(isHome = true, colors = colors, onClick = onHome) },
                trailing = { BedtimeToggle(state.bedtime, colors) { viewModel.setBedtime(!state.bedtime) } },
            )
        }
        item {
            Column(Modifier.padding(bottom = 8.dp)) {
                Text(stringResource(R.string.nav_listen), style = KidType.Title, color = colors.text)
                Text(stringResource(R.string.listen_subtitle), style = KidType.Caption, color = colors.muted)
            }
        }
        items(ListenShelf.entries, key = { it.routeId }) { shelf ->
            ShelfCard(shelf = shelf, colors = colors, onClick = { onOpenShelf(shelf) })
        }
    }
}

@Composable
fun ListenShelfRoute(
    onBack: () -> Unit,
    viewModel: ListenViewModel = viewModel(factory = ListenViewModel.Factory),
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    DarkSystemBars(enabled = state.bedtime)
    val colors = state.colors
    val shelf = state.shelf ?: ListenShelf.AnimalSounds
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(colors.background)
            .statusBarsPadding()
            .navigationBarsPadding(),
        contentPadding = PaddingValues(start = 20.dp, end = 20.dp, bottom = 24.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        item {
            KidTopBar(
                horizontalPadding = 0.dp,
                leading = { ListenNavButton(isHome = false, colors = colors, onClick = onBack) },
                trailing = { BedtimeToggle(state.bedtime, colors) { viewModel.setBedtime(!state.bedtime) } },
            )
        }
        item {
            Text(stringResource(shelf.titleRes()), style = KidType.Title, color = colors.text, modifier = Modifier.padding(bottom = 8.dp))
        }
        items(state.items, key = { it.title }) { item ->
            ListenItemRow(
                item = item,
                playing = state.playingTitle == item.title,
                colors = colors,
                onClick = { viewModel.toggle(item) },
            )
        }
    }
}

private fun ListenShelf.titleRes(): Int = when (this) {
    ListenShelf.AnimalSounds -> R.string.listen_animal_sounds
    ListenShelf.KidsStories -> R.string.listen_kids_stories
    ListenShelf.Rhymes -> R.string.listen_rhymes
}

private fun ListenShelf.countRes(): Int = when (this) {
    ListenShelf.AnimalSounds -> R.string.listen_animal_sounds_count
    ListenShelf.KidsStories -> R.string.listen_kids_stories_count
    ListenShelf.Rhymes -> R.string.listen_rhymes_count
}

@Composable
private fun ListenNavButton(isHome: Boolean, colors: ListenColors, onClick: () -> Unit) {
    if (isHome && colors == ListenColors.Day) {
        KidHomeButton(onClick = onClick)
    } else {
        KidIconButton(
            icon = if (isHome) Icons.Rounded.Home else Icons.AutoMirrored.Rounded.KeyboardArrowLeft,
            contentDescription = stringResource(if (isHome) R.string.cd_home else R.string.cd_back),
            onClick = onClick,
            container = colors.surface,
            contentColor = colors.text,
            lipColor = colors.lip,
        )
    }
}

@Composable
private fun BedtimeToggle(enabled: Boolean, colors: ListenColors, onToggle: () -> Unit) {
    val label = stringResource(R.string.listen_bedtime)
    KidCard(
        onClick = onToggle,
        modifier = Modifier.semantics {
            stateDescription = if (enabled) "On" else "Off"
        },
        color = if (enabled) colors.accent else colors.surface,
        lipColor = colors.lip,
        shape = KidShapes.Pill,
        role = Role.Switch,
        contentAlignment = Alignment.Center,
    ) {
        Row(
            modifier = Modifier
                .heightIn(min = 48.dp)
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Icon(
                imageVector = if (enabled) Icons.Rounded.Bedtime else Icons.Rounded.LightMode,
                contentDescription = null,
                tint = if (enabled) colors.onAccent else colors.accent,
                modifier = Modifier.size(22.dp),
            )
            Text(label, style = KidType.Caption, color = if (enabled) colors.onAccent else colors.text)
        }
    }
}

@Composable
private fun ShelfCard(shelf: ListenShelf, colors: ListenColors, onClick: () -> Unit) {
    val title = stringResource(shelf.titleRes())
    val count = stringResource(shelf.countRes())
    KidCard(
        onClick = onClick,
        modifier = Modifier.semantics(mergeDescendants = true) { contentDescription = "$title, $count" },
        color = colors.surface,
        lipColor = colors.lip,
        shape = KidShapes.Tile,
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 96.dp)
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Image(
                painter = painterResource(shelf.image),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(72.dp)
                    .clip(RoundedCornerShape(16.dp)),
            )
            Column(Modifier.weight(1f)) {
                Text(title, style = KidType.Label, color = colors.text)
                Text(count, style = KidType.Small, color = colors.muted)
            }
            Icon(Icons.AutoMirrored.Rounded.KeyboardArrowRight, contentDescription = null, tint = colors.muted)
        }
    }
}

@Composable
private fun ListenItemRow(item: ListenItem, playing: Boolean, colors: ListenColors, onClick: () -> Unit) {
    KidCard(
        onClick = onClick,
        onClickLabel = stringResource(if (playing) R.string.cd_stop else R.string.cd_play),
        modifier = Modifier.semantics(mergeDescendants = true) { contentDescription = "${item.title}, ${item.subtitle}" },
        color = colors.surface,
        lipColor = colors.lip,
        shape = KidShapes.Tile,
        border = if (playing) BorderStroke(3.dp, colors.accent) else null,
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 88.dp)
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            Image(
                painter = painterResource(item.image),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(64.dp)
                    .clip(RoundedCornerShape(14.dp)),
            )
            Column(Modifier.weight(1f)) {
                Text(item.title, style = KidType.Body, color = colors.text, maxLines = 2, overflow = TextOverflow.Ellipsis)
                Text(item.subtitle, style = KidType.Small, color = colors.muted, maxLines = 1, overflow = TextOverflow.Ellipsis)
            }
            Box(
                modifier = Modifier
                    .size(52.dp)
                    .background(colors.accent, CircleShape),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = if (playing) Icons.Rounded.Stop else Icons.Rounded.PlayArrow,
                    contentDescription = null,
                    tint = colors.onAccent,
                    modifier = Modifier.size(28.dp),
                )
            }
        }
    }
}

/**
 * Bedtime is the only dark screen, so it switches the status and navigation bar icons to light
 * while it is shown. Requests are counted because Listen and a shelf overlap during transitions.
 */
private object DarkBarsRequests {
    var count = 0
}

@Composable
private fun DarkSystemBars(enabled: Boolean) {
    val view = LocalView.current
    DisposableEffect(enabled, view) {
        val window = view.context.findActivity()?.window
        if (!enabled || window == null) return@DisposableEffect onDispose { }
        val controller = WindowCompat.getInsetsController(window, view)
        DarkBarsRequests.count++
        controller.isAppearanceLightStatusBars = false
        controller.isAppearanceLightNavigationBars = false
        onDispose {
            DarkBarsRequests.count = (DarkBarsRequests.count - 1).coerceAtLeast(0)
            if (DarkBarsRequests.count == 0) {
                controller.isAppearanceLightStatusBars = true
                controller.isAppearanceLightNavigationBars = true
            }
        }
    }
}

private tailrec fun Context.findActivity(): Activity? = when (this) {
    is Activity -> this
    is ContextWrapper -> baseContext.findActivity()
    else -> null
}
