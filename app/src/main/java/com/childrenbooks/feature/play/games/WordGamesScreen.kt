package com.childrenbooks.feature.play.games

import androidx.compose.ui.res.stringResource
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.KeyboardArrowDown
import androidx.compose.material.icons.rounded.KeyboardArrowUp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.childrenbooks.R
import com.childrenbooks.core.designsystem.KidColors
import com.childrenbooks.data.local.LearningCatalog
import com.childrenbooks.domain.model.PictureLesson
import com.childrenbooks.core.designsystem.components.KidButton
import com.childrenbooks.core.designsystem.components.KidButtonStyle
import kotlin.random.Random

private data class WordRound(
    val word: String,
    val image: Int,
)

private val wordRounds = listOf(
    WordRound("APPLE", R.drawable.alphabet_example_apple),
    WordRound("BEE", R.drawable.alphabet_example_bee),
    WordRound("CAT", R.drawable.alphabet_example_cat),
    WordRound("FISH", R.drawable.alphabet_example_fish),
    WordRound("LION", R.drawable.alphabet_example_lion),
)

@Composable
fun PictureMatchGame() {
    val lessons = remember {
        listOf(
            LearningCatalog.fruits[0],
            LearningCatalog.animals[0],
            LearningCatalog.birds[0],
            LearningCatalog.vegetables[0],
        )
    }
    var round by rememberSaveable { mutableIntStateOf(0) }
    var score by rememberSaveable { mutableIntStateOf(0) }
    var selectedTitle by rememberSaveable(round) { mutableStateOf<String?>(null) }
    val target = lessons[round % lessons.size]
    val choices = remember(round) { pictureChoices(target, lessons, round) }
    val isCorrect = selectedTitle == target.title

    GameShell(
        subtitle = stringResource(R.string.game_picture_match_hint),
        score = score,
    ) {
        Text(
            text = stringResource(R.string.game_find_title, target.title),
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.ExtraBold,
            color = KidColors.PrimaryLip,
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            choices.forEach { lesson ->
                PictureChoiceCard(
                    lesson = lesson,
                    selected = selectedTitle == lesson.title,
                    correct = selectedTitle != null && lesson.title == target.title,
                    modifier = Modifier.weight(1f),
                    onClick = {
                        if (selectedTitle == null) {
                            selectedTitle = lesson.title
                            if (lesson.title == target.title) score++
                        }
                    },
                )
            }
        }
        Text(
            text = when {
                selectedTitle == null -> stringResource(R.string.game_picture_tap)
                isCorrect -> stringResource(R.string.game_picture_correct, target.title)
                else -> stringResource(R.string.game_picture_wrong)
            },
            style = MaterialTheme.typography.titleMedium,
            textAlign = TextAlign.Center,
            fontWeight = FontWeight.ExtraBold,
            color = if (isCorrect) KidColors.Correct else KidColors.InkVariant,
        )
        if (selectedTitle != null) {
            KidButton(
                text = stringResource(R.string.game_next_picture),
                onClick = { round++ },
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
}

@Composable
fun GuessWordGame() {
    var round by rememberSaveable { mutableIntStateOf(0) }
    var score by rememberSaveable { mutableIntStateOf(0) }
    var typedWord by rememberSaveable(round) { mutableStateOf("") }
    var messageRes by rememberSaveable(round) { mutableIntStateOf(R.string.game_spell_start) }
    val current = wordRounds[round % wordRounds.size]
    val complete = typedWord == current.word
    val letterChoices = remember(round) { wordLetterChoices(current.word, round) }

    GameShell(
        subtitle = stringResource(R.string.game_spell_hint),
        score = score,
    ) {
        WordImageCard(image = current.image, label = stringResource(R.string.game_spell_question))
        LetterSlots(word = current.word, typedWord = typedWord)
        LetterChoiceGrid(
            letters = letterChoices,
            enabled = !complete,
            onLetterClick = { letter ->
                val nextIndex = typedWord.length
                if (letter == current.word[nextIndex]) {
                    typedWord += letter
                    messageRes = if (nextIndex == current.word.lastIndex) {
                        score++
                        R.string.game_spell_done
                    } else {
                        R.string.game_spell_next_letter
                    }
                } else {
                    messageRes = R.string.game_spell_wrong
                }
            },
        )
        Text(
            text = stringResource(messageRes, current.word),
            style = MaterialTheme.typography.titleMedium,
            textAlign = TextAlign.Center,
            fontWeight = FontWeight.ExtraBold,
            color = if (complete) KidColors.Correct else KidColors.InkVariant,
        )
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            KidButton(
                text = stringResource(R.string.action_clear),
                onClick = {
                    typedWord = ""
                    messageRes = R.string.game_spell_retry
                },
                style = KidButtonStyle.Neutral,
            )
            if (complete) {
                KidButton(text = stringResource(R.string.game_next_word), onClick = { round++ })
            }
        }
    }
}

@Composable
fun WordLockGame() {
    var round by rememberSaveable { mutableIntStateOf(0) }
    var score by rememberSaveable { mutableIntStateOf(0) }
    var attempt by rememberSaveable(round) { mutableStateOf("") }
    var checked by rememberSaveable(round) { mutableStateOf(false) }
    val current = wordRounds[round % wordRounds.size]
    val displayedAttempt = attempt.ifEmpty { "A".repeat(current.word.length) }
    val unlocked = displayedAttempt == current.word

    GameShell(
        subtitle = stringResource(R.string.game_lock_hint),
        score = score,
    ) {
        WordImageCard(image = current.image, label = stringResource(R.string.game_lock_question))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
        ) {
            displayedAttempt.forEachIndexed { index, letter ->
                LockWheel(
                    letter = letter,
                    onShift = { shift ->
                        attempt = displayedAttempt.replaceLetterAt(index, letter.shiftBy(shift))
                        checked = false
                    },
                )
            }
        }
        Text(
            text = when {
                !checked -> stringResource(R.string.game_lock_start)
                unlocked -> stringResource(R.string.game_lock_open, current.word)
                else -> stringResource(R.string.game_lock_closed)
            },
            style = MaterialTheme.typography.titleMedium,
            textAlign = TextAlign.Center,
            fontWeight = FontWeight.ExtraBold,
            color = if (checked && unlocked) KidColors.Correct else KidColors.InkVariant,
        )
        KidButton(
            text = stringResource(if (checked && unlocked) R.string.game_next_lock else R.string.game_check_lock),
            onClick = {
                if (checked && unlocked) {
                    round++
                } else {
                    if (unlocked) score++
                    checked = true
                }
            },
            modifier = Modifier.fillMaxWidth(),
        )
    }
}

@Composable
fun WordPuzzleGame() {
    var round by rememberSaveable { mutableIntStateOf(0) }
    var score by rememberSaveable { mutableIntStateOf(0) }
    var builtWord by rememberSaveable(round) { mutableStateOf("") }
    var checked by rememberSaveable(round) { mutableStateOf(false) }
    val current = wordRounds[round % wordRounds.size]
    val puzzleLetters = remember(round) { jumble(current.word, round) }
    val solved = builtWord == current.word
    val complete = builtWord.length == current.word.length

    GameShell(
        subtitle = stringResource(R.string.game_puzzle_hint),
        score = score,
    ) {
        WordImageCard(image = current.image, label = stringResource(R.string.game_puzzle_question))
        LetterSlots(word = current.word, typedWord = builtWord)
        LetterChoiceGrid(
            letters = puzzleLetters,
            enabled = !complete,
            onLetterClick = { letter ->
                builtWord += letter
                checked = false
            },
        )
        Text(
            text = when {
                !complete -> stringResource(R.string.game_puzzle_start)
                !checked -> stringResource(R.string.game_puzzle_check_prompt)
                solved -> stringResource(R.string.game_puzzle_solved, current.word)
                else -> stringResource(R.string.game_puzzle_wrong)
            },
            style = MaterialTheme.typography.titleMedium,
            textAlign = TextAlign.Center,
            fontWeight = FontWeight.ExtraBold,
            color = if (checked && solved) KidColors.Correct else KidColors.InkVariant,
        )
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            KidButton(
                text = stringResource(R.string.action_clear),
                onClick = {
                    builtWord = ""
                    checked = false
                },
                style = KidButtonStyle.Neutral,
            )
            if (complete) {
                KidButton(
                    text = stringResource(if (checked && solved) R.string.game_next_puzzle else R.string.action_check),
                    onClick = {
                        if (checked && solved) {
                            round++
                        } else {
                            if (solved) score++
                            checked = true
                        }
                    },
                )
            }
        }
    }
}

@Composable
private fun PictureChoiceCard(
    lesson: PictureLesson,
    selected: Boolean,
    correct: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) {
    val border = when {
        selected && correct -> KidColors.Correct
        selected -> KidColors.TryAgain
        else -> KidColors.Track
    }
    Card(
        modifier = modifier
            .height(154.dp)
            .clickable(enabled = !selected, onClick = onClick),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = androidx.compose.foundation.BorderStroke(2.dp, border),
    ) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(6.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            Image(
                painter = painterResource(lesson.image),
                contentDescription = lesson.title,
                modifier = Modifier.weight(1f).fillMaxWidth(),
                contentScale = ContentScale.Fit,
            )
            Text(
                text = lesson.title,
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.ExtraBold,
                color = KidColors.PrimaryLip,
                textAlign = TextAlign.Center,
            )
        }
    }
}

@Composable
private fun WordImageCard(image: Int, label: String) {
    Card(
        modifier = Modifier.fillMaxWidth().height(172.dp),
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(4.dp),
    ) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.ExtraBold,
                color = KidColors.PrimaryLip,
            )
            Image(
                painter = painterResource(image),
                contentDescription = null,
                modifier = Modifier.weight(1f).fillMaxWidth(),
                contentScale = ContentScale.Fit,
            )
        }
    }
}

@Composable
private fun LetterSlots(word: String, typedWord: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
    ) {
        word.indices.forEach { index ->
            val letter = typedWord.getOrNull(index)?.toString().orEmpty()
            Box(
                modifier = Modifier
                    .padding(horizontal = 3.dp)
                    .size(44.dp)
                    .background(Color.White, RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = letter,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.ExtraBold,
                    color = KidColors.Primary,
                )
            }
        }
    }
}

@Composable
private fun LetterChoiceGrid(
    letters: List<Char>,
    enabled: Boolean,
    onLetterClick: (Char) -> Unit,
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        letters.chunked(4).forEach { rowLetters ->
            Row(horizontalArrangement = Arrangement.Center) {
                rowLetters.forEach { letter ->
                    Card(
                        modifier = Modifier
                            .padding(horizontal = 4.dp)
                            .size(44.dp)
                            .clickable(enabled = enabled) { onLetterClick(letter) },
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = KidColors.PrimaryContainer),
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(
                                text = letter.toString(),
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.ExtraBold,
                                color = KidColors.Primary,
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun LockWheel(letter: Char, onShift: (Int) -> Unit) {
    Card(
        modifier = Modifier.width(52.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(3.dp),
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            IconButton(onClick = { onShift(1) }) {
                Icon(
                    imageVector = Icons.Rounded.KeyboardArrowUp,
                    contentDescription = stringResource(R.string.cd_next_letter),
                    tint = KidColors.Primary,
                )
            }
            Text(
                text = letter.toString(),
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.ExtraBold,
                color = KidColors.PrimaryLip,
            )
            IconButton(onClick = { onShift(-1) }) {
                Icon(
                    imageVector = Icons.Rounded.KeyboardArrowDown,
                    contentDescription = stringResource(R.string.cd_previous_letter),
                    tint = KidColors.Primary,
                )
            }
        }
    }
}

private fun pictureChoices(
    target: PictureLesson,
    lessons: List<PictureLesson>,
    round: Int,
): List<PictureLesson> =
    (lessons.filterNot { it.title == target.title }.take(3) + target).shuffled(Random(round + 37))

private fun wordLetterChoices(word: String, round: Int): List<Char> =
    (word.toSet() + setOf('X', 'Y', 'Z')).shuffled(Random(round + 41)).take(7)

private fun jumble(word: String, round: Int): List<Char> {
    val shifted = word.drop(1) + word.first()
    val mixed = shifted.toList().shuffled(Random(round + 97))
    return if (mixed == word.toList()) shifted.toList() else mixed
}

private fun Char.shiftBy(amount: Int): Char {
    val alphabetPosition = this.code - 'A'.code
    return ('A'.code + (alphabetPosition + amount + 26) % 26).toChar()
}

private fun String.replaceLetterAt(index: Int, replacement: Char): String =
    replaceRange(index, index + 1, replacement.toString())
