// pluralStringResource was @ExperimentalComposeUiApi in older Compose UI; opting in is harmless if it is stable.
@file:OptIn(ExperimentalComposeUiApi::class)

package com.childrenbooks.feature.home

import com.childrenbooks.core.designsystem.components.pictureScale
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Lock
import androidx.compose.material.icons.rounded.MusicNote
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.childrenbooks.R
import com.childrenbooks.core.designsystem.AndikaFamily
import com.childrenbooks.core.designsystem.KidColors
import com.childrenbooks.core.designsystem.KidShapes
import com.childrenbooks.core.designsystem.KidType
import com.childrenbooks.core.designsystem.WorldPalettes
import com.childrenbooks.core.designsystem.components.KidCard
import com.childrenbooks.core.designsystem.components.KidIconButton
import com.childrenbooks.core.designsystem.components.KidTopBar
import com.childrenbooks.core.designsystem.components.StarCounter
import com.childrenbooks.core.designsystem.palette
import com.childrenbooks.data.content.WorldId
import java.util.Calendar

@Composable
fun HomeRoute(
    onContinue: (ContinueItem) -> Unit,
    onOpenWorld: (WorldId) -> Unit,
    onOpenPlay: () -> Unit,
    onOpenListen: () -> Unit,
    onOpenStickers: () -> Unit,
    onOpenGrownups: () -> Unit,
    viewModel: HomeViewModel = viewModel(factory = HomeViewModel.Factory),
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    HomeScreen(
        state = state,
        onContinue = onContinue,
        onOpenWorld = { tile ->
            viewModel.speak(tile.title)
            onOpenWorld(tile.id)
        },
        onOpenPlay = onOpenPlay,
        onOpenListen = onOpenListen,
        onOpenStickers = onOpenStickers,
        onOpenGrownups = onOpenGrownups,
    )
}

@Composable
fun HomeScreen(
    state: HomeUiState,
    onContinue: (ContinueItem) -> Unit,
    onOpenWorld: (WorldTile) -> Unit,
    onOpenPlay: () -> Unit,
    onOpenListen: () -> Unit,
    onOpenStickers: () -> Unit,
    onOpenGrownups: () -> Unit,
) {
    Box(
        Modifier
            .fillMaxSize()
            .background(KidColors.Background),
    ) {
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding(),
            contentPadding = PaddingValues(start = 20.dp, end = 20.dp, bottom = 160.dp),
            horizontalArrangement = Arrangement.spacedBy(14.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            item(span = { GridItemSpan(maxLineSpan) }) {
                KidTopBar(
                    horizontalPadding = 0.dp,
                    leading = { Avatar() },
                    center = { StarCounter(stars = state.stars) },
                    trailing = {
                        KidIconButton(
                            icon = Icons.Rounded.Lock,
                            contentDescription = stringResource(R.string.cd_grownups),
                            onClick = onOpenGrownups,
                            size = 48.dp,
                            contentColor = KidColors.InkVariant,
                        )
                    },
                )
            }
            item(span = { GridItemSpan(maxLineSpan) }) {
                Text(
                    text = greeting(state.childName),
                    style = KidType.Title,
                    color = KidColors.Ink,
                )
            }
            state.continueItem?.let { item ->
                item(span = { GridItemSpan(maxLineSpan) }) {
                    ContinueCard(item = item, onClick = { onContinue(item) })
                }
            }
            item(span = { GridItemSpan(maxLineSpan) }) {
                Text(
                    text = stringResource(R.string.home_worlds),
                    style = KidType.Label,
                    color = KidColors.Ink,
                    modifier = Modifier.padding(top = 8.dp),
                )
            }
            items(state.worlds, key = { it.id.key }) { tile ->
                WorldCard(tile = tile, onClick = { onOpenWorld(tile) })
            }
        }

        HomeDock(
            newStickers = state.newStickers,
            onOpenPlay = onOpenPlay,
            onOpenListen = onOpenListen,
            onOpenStickers = onOpenStickers,
            modifier = Modifier.align(Alignment.BottomCenter),
        )
    }
}

@Composable
private fun greeting(name: String): String {
    val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
    val withName = name.isNotBlank()
    return when {
        hour < 12 -> if (withName) stringResource(R.string.greeting_morning_name, name) else stringResource(R.string.greeting_morning)
        hour < 17 -> if (withName) stringResource(R.string.greeting_afternoon_name, name) else stringResource(R.string.greeting_afternoon)
        else -> if (withName) stringResource(R.string.greeting_evening_name, name) else stringResource(R.string.greeting_evening)
    }
}

@Composable
private fun Avatar() {
    Box(
        modifier = Modifier
            .size(56.dp)
            .clip(CircleShape)
            .background(KidColors.Surface)
            .padding(3.dp)
            .clip(CircleShape),
    ) {
        Image(
            painter = painterResource(R.drawable.bird_owl),
            contentDescription = stringResource(R.string.cd_avatar),
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize(),
        )
    }
}

@Composable
private fun ContinueCard(item: ContinueItem, onClick: () -> Unit) {
    val worldTitle = stringResource(item.worldTitle)
    val label = stringResource(if (item.resume) R.string.home_continue else R.string.home_up_next)
    KidCard(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .semantics(mergeDescendants = true) { contentDescription = "$label, $worldTitle, ${item.setTitle}" },
        color = KidColors.Primary,
        lipColor = KidColors.PrimaryLip,
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 176.dp)
                .padding(start = 22.dp, top = 20.dp, bottom = 20.dp, end = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(label, style = KidType.Small, color = KidColors.OnPrimary)
                Text(worldTitle, style = KidType.Title, color = KidColors.OnPrimary, maxLines = 1, overflow = TextOverflow.Ellipsis)
                Text(item.setTitle, style = KidType.Label, color = KidColors.OnPrimary, maxLines = 2, overflow = TextOverflow.Ellipsis)
                Spacer(Modifier.height(10.dp))
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(14.dp)) {
                    Box(
                        modifier = Modifier
                            .size(56.dp)
                            .background(KidColors.Secondary, CircleShape),
                        contentAlignment = Alignment.Center,
                    ) {
                        Icon(Icons.Rounded.PlayArrow, contentDescription = null, tint = KidColors.OnSecondary, modifier = Modifier.size(30.dp))
                    }
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalAlignment = Alignment.CenterVertically) {
                        repeat(item.cardCount) { index ->
                            Box(
                                Modifier
                                    .width(if (index == item.cardIndex) 22.dp else 9.dp)
                                    .height(9.dp)
                                    .background(
                                        if (index <= item.cardIndex) KidColors.OnPrimary else KidColors.OnPrimary.copy(alpha = 0.45f),
                                        KidShapes.Pill,
                                    ),
                            )
                        }
                    }
                }
            }
            Box(
                modifier = Modifier
                    .size(128.dp)
                    .background(KidColors.Surface, CircleShape),
                contentAlignment = Alignment.Center,
            ) {
                // Scene covers fill the circle; word banners (days, colours) are fitted inside it.
                val coverPainter = painterResource(item.cover)
                val coverScale = coverPainter.pictureScale(1f)
                Image(
                    painter = coverPainter,
                    contentDescription = null,
                    contentScale = coverScale,
                    modifier = Modifier
                        .size(112.dp)
                        .clip(CircleShape)
                        .then(if (coverScale == ContentScale.Fit) Modifier.padding(12.dp) else Modifier),
                )
            }
        }
    }
}

@Composable
private fun WorldCard(tile: WorldTile, onClick: () -> Unit) {
    val palette = tile.id.palette()
    val title = stringResource(tile.title)
    val progressText = if (tile.setsDone == 0) {
        stringResource(R.string.world_new)
    } else {
        stringResource(R.string.world_sets_done, tile.setsDone, tile.setCount)
    }
    KidCard(
        onClick = onClick,
        modifier = Modifier.semantics(mergeDescendants = true) { contentDescription = "$title, $progressText" },
        color = palette.container,
        lipColor = palette.lip,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 148.dp)
                .padding(horizontal = 16.dp, vertical = 14.dp),
            verticalArrangement = Arrangement.SpaceBetween,
        ) {
            if (tile.glyph != null) {
                Text(
                    text = tile.glyph,
                    style = KidType.Display.copy(fontFamily = AndikaFamily, fontSize = KidType.Display.fontSize * 1.15f),
                    color = palette.accent,
                    maxLines = 1,
                )
            } else if (tile.art != null) {
                Image(painterResource(tile.art), contentDescription = null, modifier = Modifier.size(60.dp))
            }
            Spacer(Modifier.height(8.dp))
            Text(title, style = KidType.Label, color = palette.ink, maxLines = 1, overflow = TextOverflow.Ellipsis)
            Row(
                modifier = Modifier.padding(top = 6.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                if (tile.setsDone > 0) {
                    Box(
                        Modifier
                            .width(64.dp)
                            .height(8.dp)
                            .background(KidColors.Surface, KidShapes.Pill),
                    ) {
                        Box(
                            Modifier
                                .fillMaxWidth(tile.setsDone.toFloat() / tile.setCount.coerceAtLeast(1))
                                .height(8.dp)
                                .background(palette.accent, KidShapes.Pill),
                        )
                    }
                    Text(stringResource(R.string.world_sets_short, tile.setsDone, tile.setCount), style = KidType.Small, color = palette.ink)
                } else {
                    Text(
                        text = progressText,
                        style = KidType.Small,
                        color = palette.ink,
                        modifier = Modifier
                            .background(KidColors.Surface, KidShapes.Pill)
                            .padding(horizontal = 10.dp, vertical = 2.dp),
                    )
                }
            }
        }
    }
}

@Composable
private fun HomeDock(
    newStickers: Int,
    onOpenPlay: () -> Unit,
    onOpenListen: () -> Unit,
    onOpenStickers: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(KidColors.Surface, KidShapes.Sheet)
            .navigationBarsPadding()
            .padding(horizontal = 20.dp, vertical = 14.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        DockTile(
            label = stringResource(R.string.nav_play),
            container = KidColors.TertiaryContainer,
            content = KidColors.OnTertiaryContainer,
            onClick = onOpenPlay,
            modifier = Modifier.weight(1f),
        ) {
            Image(painterResource(R.drawable.home_play), contentDescription = null, modifier = Modifier.size(40.dp))
        }
        DockTile(
            label = stringResource(R.string.nav_listen),
            container = WorldPalettes.Listen.container,
            content = WorldPalettes.Listen.ink,
            onClick = onOpenListen,
            modifier = Modifier.weight(1f),
        ) {
            Icon(Icons.Rounded.MusicNote, contentDescription = null, tint = WorldPalettes.Listen.accent, modifier = Modifier.size(36.dp))
        }
        val stickersLabel = stringResource(R.string.nav_stickers)
        val badge = if (newStickers > 0) pluralStringResource(R.plurals.new_stickers, newStickers, newStickers) else null
        DockTile(
            label = stickersLabel,
            container = WorldPalettes.Things.container,
            content = WorldPalettes.Things.ink,
            onClick = onOpenStickers,
            modifier = Modifier
                .weight(1f)
                .semantics(mergeDescendants = true) { contentDescription = listOfNotNull(stickersLabel, badge).joinToString(", ") },
        ) {
            Box {
                Icon(Icons.Rounded.Star, contentDescription = null, tint = WorldPalettes.Things.accent, modifier = Modifier.size(36.dp))
                if (newStickers > 0) {
                    Text(
                        text = newStickers.toString(),
                        style = KidType.Small,
                        color = KidColors.OnPrimary,
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(start = 24.dp)
                            .background(KidColors.Primary, CircleShape)
                            .padding(horizontal = 7.dp),
                    )
                }
            }
        }
    }
}

@Composable
private fun DockTile(
    label: String,
    container: Color,
    content: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    icon: @Composable () -> Unit,
) {
    KidCard(
        onClick = onClick,
        modifier = modifier,
        color = container,
        lipColor = content.copy(alpha = 0.16f),
        shape = KidShapes.Tile,
        contentAlignment = Alignment.Center,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 84.dp)
                .padding(vertical = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(2.dp, Alignment.CenterVertically),
        ) {
            icon()
            Text(label, style = KidType.Caption, color = content, maxLines = 1)
        }
    }
}
