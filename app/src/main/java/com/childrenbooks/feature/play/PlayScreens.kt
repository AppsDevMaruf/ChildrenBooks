package com.childrenbooks.feature.play

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.KeyboardArrowLeft
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.childrenbooks.R
import com.childrenbooks.core.designsystem.KidColors
import com.childrenbooks.core.designsystem.KidShapes
import com.childrenbooks.core.designsystem.KidType
import com.childrenbooks.core.designsystem.WorldPalette
import com.childrenbooks.core.designsystem.WorldPalettes
import com.childrenbooks.core.designsystem.components.KidCard
import com.childrenbooks.core.designsystem.components.KidHomeButton
import com.childrenbooks.core.designsystem.components.KidIconButton
import com.childrenbooks.core.designsystem.components.KidTopBar
import com.childrenbooks.feature.play.games.GameHostScreen

/** One mini-game tile in the Play hub. [id] is the route id understood by [GameHostScreen]. */
@Immutable
data class PlayGame(
    val id: String,
    @StringRes val title: Int,
    @StringRes val subtitle: Int,
    @DrawableRes val image: Int,
    val palette: WorldPalette,
)

@Immutable
data class PlaySection(@StringRes val title: Int, val games: List<PlayGame>)

object PlayCatalog {
    val sections: List<PlaySection> = listOf(
        PlaySection(
            R.string.play_section_letters,
            listOf(
                PlayGame("letter", R.string.game_letter_match, R.string.game_letter_match_sub, R.drawable.game_letter_match, WorldPalettes.Letters),
                PlayGame("object-letter", R.string.game_object_letter, R.string.game_object_letter_sub, R.drawable.apple, WorldPalettes.Food),
                PlayGame("trace", R.string.game_trace_letter, R.string.game_trace_letter_sub, R.drawable.game_trace_letter, WorldPalettes.Letters),
                PlayGame("drag", R.string.game_drag_drop, R.string.game_drag_drop_sub, R.drawable.game_puzzle_time, WorldPalettes.Things),
            ),
        ),
        PlaySection(
            R.string.play_section_look,
            listOf(
                PlayGame("number", R.string.game_number_match, R.string.game_number_match_sub, R.drawable.game_number_match, WorldPalettes.Numbers),
                PlayGame("color", R.string.game_color_match, R.string.game_color_match_sub, R.drawable.game_color_fun, WorldPalettes.Colours),
                PlayGame("find", R.string.game_find_object, R.string.game_find_object_sub, R.drawable.game_pop_balloons, WorldPalettes.Nature),
                PlayGame("quiz", R.string.game_quiz_mode, R.string.game_quiz_mode_sub, R.drawable.game_quiz_mode, WorldPalettes.Time),
            ),
        ),
        PlaySection(
            R.string.play_section_words,
            listOf(
                PlayGame("picture-match", R.string.game_picture_match, R.string.game_picture_match_sub, R.drawable.alphabet_example_apple, WorldPalettes.Food),
                PlayGame("guess-word", R.string.game_guess_word, R.string.game_guess_word_sub, R.drawable.alphabet_example_bee, WorldPalettes.Nature),
                PlayGame("word-lock", R.string.game_word_lock, R.string.game_word_lock_sub, R.drawable.alphabet_example_lock, WorldPalettes.Things),
                PlayGame("word-puzzle", R.string.game_word_puzzle, R.string.game_word_puzzle_sub, R.drawable.game_word_puzzle, WorldPalettes.Letters),
            ),
        ),
    )

    val games: List<PlayGame> = sections.flatMap { it.games }

    fun game(id: String): PlayGame? = games.firstOrNull { it.id == id }
}

@Composable
fun PlayHubRoute(onHome: () -> Unit, onOpenGame: (String) -> Unit) {
    LazyVerticalGrid(
        columns = GridCells.Adaptive(minSize = 156.dp),
        modifier = Modifier
            .fillMaxSize()
            .background(KidColors.Background)
            .statusBarsPadding()
            .navigationBarsPadding(),
        contentPadding = PaddingValues(start = 20.dp, end = 20.dp, bottom = 24.dp),
        horizontalArrangement = Arrangement.spacedBy(14.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        item(span = { GridItemSpan(maxLineSpan) }) {
            KidTopBar(horizontalPadding = 0.dp, leading = { KidHomeButton(onClick = onHome) })
        }
        item(span = { GridItemSpan(maxLineSpan) }) {
            Column {
                Text(stringResource(R.string.nav_play), style = KidType.Title, color = KidColors.Ink)
                Text(stringResource(R.string.play_subtitle), style = KidType.Caption, color = KidColors.InkVariant)
            }
        }
        PlayCatalog.sections.forEach { section ->
            item(span = { GridItemSpan(maxLineSpan) }, key = "section-${section.title}") {
                Text(
                    text = stringResource(section.title),
                    style = KidType.Headline,
                    color = KidColors.Ink,
                    modifier = Modifier
                        .padding(top = 12.dp)
                        .semantics { heading() },
                )
            }
            items(section.games, key = { it.id }) { game ->
                GameTile(game = game, onClick = { onOpenGame(game.id) })
            }
        }
    }
}

@Composable
private fun GameTile(game: PlayGame, onClick: () -> Unit) {
    val title = stringResource(game.title)
    val subtitle = stringResource(game.subtitle)
    KidCard(
        onClick = onClick,
        modifier = Modifier.semantics(mergeDescendants = true) { contentDescription = "$title. $subtitle" },
        color = game.palette.container,
        lipColor = game.palette.lip,
        shape = KidShapes.Tile,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1.25f)
                    .clip(RoundedCornerShape(18.dp))
                    .background(KidColors.Surface),
            ) {
                Image(
                    painter = painterResource(game.image),
                    contentDescription = null,
                    contentScale = ContentScale.Fit,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(8.dp),
                )
            }
            Text(
                text = title,
                style = KidType.Label,
                color = game.palette.ink,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.padding(top = 10.dp),
            )
            Text(
                text = subtitle,
                style = KidType.Small,
                color = game.palette.ink,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
            )
        }
    }
}

@Composable
fun GameRoute(gameId: String, onBack: () -> Unit) {
    val game = PlayCatalog.game(gameId)
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(KidColors.Background)
            .statusBarsPadding()
            .navigationBarsPadding(),
    ) {
        KidTopBar(
            leading = {
                KidIconButton(
                    icon = Icons.AutoMirrored.Rounded.KeyboardArrowLeft,
                    contentDescription = stringResource(R.string.cd_back),
                    onClick = onBack,
                )
            },
            center = {
                Text(
                    text = game?.let { stringResource(it.title) } ?: stringResource(R.string.nav_play),
                    style = KidType.Headline,
                    color = KidColors.Ink,
                    textAlign = TextAlign.Center,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            },
        )
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
        ) {
            GameHostScreen(gameId)
        }
    }
}
