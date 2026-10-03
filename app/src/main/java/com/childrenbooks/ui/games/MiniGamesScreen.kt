package com.childrenbooks.ui.games

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.childrenbooks.R
import com.childrenbooks.ui.components.ChevronRightIcon

private data class MiniGame(
    val id: String,
    val title: String,
    val subtitle: String,
    val image: Int,
    val colors: List<Color>,
)

private val games = listOf(
    MiniGame("memory", "Memory Match", "Match & learn with fun", R.drawable.game_memory_match, listOf(Color(0xFFDDF0FF), Color(0xFF9FD3FF))),
    MiniGame("drag", "Puzzle Time", "Solve puzzles & improve skills", R.drawable.game_puzzle_time, listOf(Color(0xFFFFF1C8), Color(0xFFFFD874))),
    MiniGame("color", "Color Fun", "Learn colors in a creative way", R.drawable.game_color_fun, listOf(Color(0xFFF0E8FF), Color(0xFFD9C5FF))),
    MiniGame("find", "Pop the Balloons", "Pop, learn & have fun!", R.drawable.game_pop_balloons, listOf(Color(0xFFD8FAF2), Color(0xFF9CE6D7))),
    MiniGame("trace", "Trace Letter", "Finger দিয়ে লিখা practice", R.drawable.game_trace_letter, listOf(Color(0xFFE6F5FF), Color(0xFFBDE8FF))),
    MiniGame("letter", "Letter Match", "Uppercase to lowercase", R.drawable.game_letter_match, listOf(Color(0xFFEAF7E5), Color(0xFFBDEAAE))),
    MiniGame("object-letter", "Apple → A", "Match object with letter", R.drawable.apple, listOf(Color(0xFFFFE8E8), Color(0xFFFFB6B6))),
    MiniGame("quiz", "Quiz Mode", "Answer and collect stars", R.drawable.game_quiz_mode, listOf(Color(0xFFFFEEDF), Color(0xFFFFC998))),
    MiniGame("number", "Number Match", "Count and choose number", R.drawable.game_number_match, listOf(Color(0xFFFFF4D7), Color(0xFFFFDF87))),
    MiniGame("picture-match", "Picture Match", "Match every picture with its word", R.drawable.alphabet_example_apple, listOf(Color(0xFFE4F4FF), Color(0xFFB5E0FF))),
    MiniGame("guess-word", "Guess the Word", "Look, listen and spell the word", R.drawable.alphabet_example_bee, listOf(Color(0xFFFFF3D9), Color(0xFFFFD891))),
    MiniGame("word-lock", "Word Lock", "Turn letter wheels to unlock a word", R.drawable.alphabet_example_lock, listOf(Color(0xFFECE3FF), Color(0xFFD0B9FF))),
    MiniGame("word-puzzle", "Word Puzzle", "Put the mixed-up letters in order", R.drawable.game_word_puzzle, listOf(Color(0xFFE5F8ED), Color(0xFFB9EACB))),
)

@Composable
fun MiniGamesScreen(onOpenGame: (String) -> Unit) {
    LazyColumn(
        modifier = Modifier.background(Color(0xFFF8FBFF)),
        contentPadding = PaddingValues(18.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        item {
            FeaturedGame(onClick = { onOpenGame("letter") })
        }
        item {
            Text(
                text = "Fun Activities",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.ExtraBold,
                color = Color(0xFF172B3A),
                modifier = Modifier.padding(top = 4.dp),
            )
        }
        items(games) { game ->
            GameCard(game = game, onClick = { onOpenGame(game.id) })
        }
        item { Spacer(Modifier.height(88.dp)) }
    }
}

@Composable
private fun FeaturedGame(onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(190.dp)
            .clip(RoundedCornerShape(26.dp))
            .background(
                Brush.linearGradient(listOf(Color(0xFF4A84A1), Color(0xFF183F57))),
            )
            .clickable(onClick = onClick),
    ) {
        Image(
            painter = painterResource(R.drawable.play_and_learn),
            contentDescription = null,
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .height(175.dp)
                .fillMaxWidth(0.56f)
                .padding(8.dp),
            contentScale = ContentScale.Fit,
        )
        Column(
            modifier = Modifier
                .align(Alignment.CenterStart)
                .fillMaxWidth(0.58f)
                .padding(20.dp),
        ) {
            Text(
                text = "GAME OF THE DAY",
                color = Color(0xFFFFD36A),
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.ExtraBold,
            )
            Text(
                text = "Alphabet Safari",
                color = Color.White,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.ExtraBold,
            )
            Text(
                text = "Find all the hidden letters",
                color = Color.White.copy(alpha = 0.82f),
                style = MaterialTheme.typography.bodySmall,
            )
        }
    }
}

@Composable
private fun GameCard(game: MiniGame, onClick: () -> Unit) {
    val shape = RoundedCornerShape(26.dp)
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(132.dp)
            .clip(shape)
            .clickable(onClick = onClick),
        shape = shape,
        colors = CardDefaults.cardColors(containerColor = game.colors.first()),
        elevation = CardDefaults.cardElevation(6.dp),
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Brush.linearGradient(game.colors))
                .padding(horizontal = 18.dp, vertical = 14.dp),
        ) {
            Column(
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .fillMaxWidth(0.58f),
            ) {
                Text(
                    text = game.title,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color(0xFF10245A),
                )
                Text(
                    text = game.subtitle,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color(0xFF10245A).copy(alpha = 0.82f),
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(top = 6.dp),
                )
            }
            Image(
                painter = painterResource(game.image),
                contentDescription = game.title,
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .padding(end = 54.dp)
                    .size(92.dp),
                contentScale = ContentScale.Fit,
            )
            Box(
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(Color.White),
                contentAlignment = Alignment.Center,
            ) {
                ChevronRightIcon(color = Color(0xFF165BBE), size = 22.dp)
            }
        }
    }
}
