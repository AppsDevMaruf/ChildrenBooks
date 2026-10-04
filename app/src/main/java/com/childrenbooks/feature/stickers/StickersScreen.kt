package com.childrenbooks.feature.stickers

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.VolumeUp
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.childrenbooks.R
import com.childrenbooks.appViewModelFactory
import com.childrenbooks.core.audio.Narrator
import com.childrenbooks.core.designsystem.KidColors
import com.childrenbooks.core.designsystem.KidType
import com.childrenbooks.core.designsystem.WorldPalettes
import com.childrenbooks.core.designsystem.components.KidCard
import com.childrenbooks.core.designsystem.components.KidHomeButton
import com.childrenbooks.core.designsystem.components.KidIconButton
import com.childrenbooks.core.designsystem.components.KidTitleChip
import com.childrenbooks.core.designsystem.components.KidTopBar
import com.childrenbooks.core.designsystem.components.StickerImage
import com.childrenbooks.core.designsystem.components.StickerSlot
import com.childrenbooks.data.content.AudioClip
import com.childrenbooks.data.content.ContentRepository
import com.childrenbooks.data.content.Sticker
import com.childrenbooks.data.progress.ProgressRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class StickersUiState(
    val earned: List<Sticker> = emptyList(),
    val total: Int = 0,
    val newFrom: Int = Int.MAX_VALUE,
)

class StickersViewModel(
    private val progressRepository: ProgressRepository,
    content: ContentRepository,
    private val narrator: Narrator,
    private val strings: (Int) -> String,
) : ViewModel() {
    private var seenAtOpen: Int? = null

    val uiState: StateFlow<StickersUiState> = combine(progressRepository.progress, progressRepository.settings) { progress, settings ->
        val seen = seenAtOpen ?: settings.stickersSeen.also { seenAtOpen = it }
        StickersUiState(
            earned = progress.stickers.mapNotNull(content::sticker),
            total = content.stickers.size,
            newFrom = seen,
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), StickersUiState())

    init {
        viewModelScope.launch {
            val count = progressRepository.progress.first().stickers.size
            progressRepository.updateSettings { it.copy(stickersSeen = count) }
        }
    }

    fun speakTip() = narrator.speak(AudioClip.Spoken(strings(R.string.stickers_tip)))

    override fun onCleared() = narrator.stop()

    companion object {
        val Factory = appViewModelFactory { container ->
            StickersViewModel(container.progressRepository, container.contentRepository, container.narrator, container.resources::getString)
        }
    }
}

private val tilts = listOf(-4f, 3f, -2f, 4f, -3f, 2f, -5f)

@Composable
fun StickersRoute(
    onHome: () -> Unit,
    viewModel: StickersViewModel = viewModel(factory = StickersViewModel.Factory),
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    LazyVerticalGrid(
        columns = GridCells.Fixed(3),
        modifier = Modifier
            .fillMaxSize()
            .background(KidColors.Background)
            .statusBarsPadding()
            .navigationBarsPadding(),
        contentPadding = PaddingValues(start = 20.dp, end = 20.dp, bottom = 24.dp),
        horizontalArrangement = Arrangement.spacedBy(14.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp),
    ) {
        item(span = { GridItemSpan(maxLineSpan) }) {
            KidTopBar(
                horizontalPadding = 0.dp,
                leading = { KidHomeButton(onClick = onHome) },
                trailing = {
                    KidTitleChip(
                        text = stringResource(R.string.stickers_count, state.earned.size, state.total),
                        container = WorldPalettes.Things.container,
                        content = WorldPalettes.Things.ink,
                    )
                },
            )
        }
        item(span = { GridItemSpan(maxLineSpan) }) {
            Column {
                Text(stringResource(R.string.stickers_title), style = KidType.Title, color = KidColors.Ink)
                Text(stringResource(R.string.stickers_subtitle), style = KidType.Caption, color = KidColors.InkVariant)
            }
        }
        itemsIndexed(state.earned, key = { _, sticker -> sticker.id }) { index, sticker ->
            StickerImage(
                image = sticker.image,
                contentDescription = sticker.name,
                rotation = tilts[index % tilts.size],
                isNew = index >= state.newFrom,
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1f),
            )
        }
        val locked = (state.total - state.earned.size).coerceAtLeast(0)
        items(count = locked) { index ->
            StickerSlot(
                isNext = index == 0,
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1f),
            )
        }
        item(span = { GridItemSpan(maxLineSpan) }) {
            KidCard(onClick = null, role = null, modifier = Modifier.padding(top = 8.dp)) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(14.dp),
                ) {
                    Box(
                        Modifier
                            .size(64.dp)
                            .clip(CircleShape),
                    ) {
                        Image(
                            painter = painterResource(R.drawable.bird_owl),
                            contentDescription = null,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize(),
                        )
                    }
                    Text(
                        text = stringResource(R.string.stickers_tip),
                        style = KidType.Body,
                        color = KidColors.Ink,
                        modifier = Modifier.weight(1f),
                    )
                    KidIconButton(
                        icon = Icons.AutoMirrored.Rounded.VolumeUp,
                        contentDescription = stringResource(R.string.cd_hear_again),
                        onClick = viewModel::speakTip,
                        size = 48.dp,
                        container = KidColors.PrimaryContainer,
                        contentColor = KidColors.Primary,
                    )
                }
            }
        }
    }
}
