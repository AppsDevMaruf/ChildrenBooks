package com.childrenbooks.feature.play.games

import com.childrenbooks.core.designsystem.KidColors
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Cancel
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.EmojiEvents
import androidx.compose.material.icons.rounded.RadioButtonUnchecked
import androidx.compose.material3.Icon
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.childrenbooks.data.local.LearningCatalog
import com.childrenbooks.core.designsystem.components.KidButton
import kotlin.random.Random

private data class GameQuestion(
    val prompt: String,
    val answer: String,
    val image: Int,
    val choices: List<String>,
)

@Composable
fun MatchGameScreen(
    gameId: String,
    matchGameViewModel: MatchGameViewModel = viewModel(),
) {
    val uiState by matchGameViewModel.uiState.collectAsStateWithLifecycle()
    val round = uiState.round
    val score = uiState.score
    val selected = uiState.selectedAnswer
    val question = remember(gameId, round) { createQuestion(gameId, round) }

    if (uiState.isComplete) {
        GameComplete(
            score = score,
            onPlayAgain = matchGameViewModel::playAgain,
        )
        return
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(KidColors.Background)
            .padding(horizontal = 20.dp, vertical = 14.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text("Round ${round + 1} of $MatchGameRoundCount", color = KidColors.InkVariant, fontWeight = FontWeight.Bold)
                LinearProgressIndicator(
                    progress = { (round + 1f) / MatchGameRoundCount },
                    modifier = Modifier
                        .fillMaxWidth(0.72f)
                        .padding(top = 5.dp)
                        .height(7.dp)
                        .clip(CircleShape),
                    color = KidColors.Primary,
                    trackColor = KidColors.Divider,
                )
            }
            GameScoreChip(score)
        }
        Text(
            text = when (gameId) {
                "number" -> "Match the Number!"
                "color" -> "Match the Color!"
                "object-letter" -> "Apple → A"
                else -> "Match the Letter!"
            },
            style = MaterialTheme.typography.headlineSmall,
            color = KidColors.PrimaryLip,
            fontWeight = FontWeight.ExtraBold,
            modifier = Modifier.padding(top = 28.dp),
        )
        Text(
            text = question.prompt,
            color = KidColors.InkVariant,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = 5.dp, bottom = 14.dp),
        )
        Card(
            modifier = Modifier.size(150.dp),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            border = androidx.compose.foundation.BorderStroke(2.dp, KidColors.Primary),
            elevation = CardDefaults.cardElevation(3.dp),
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(12.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
            ) {
                Image(
                    painter = painterResource(question.image),
                    contentDescription = null,
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    contentScale = ContentScale.Fit,
                )
                Text(
                    text = question.answer.uppercase(),
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.ExtraBold,
                    color = KidColors.PrimaryLip,
                )
            }
        }
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            question.choices.forEachIndexed { index, choice ->
                AnswerChoice(
                    choice = choice,
                    index = index,
                    correctAnswer = question.answer,
                    selected = selected,
                    onSelect = { matchGameViewModel.selectAnswer(choice, question.answer) },
                )
            }
        }
        AnimatedVisibility(selected != null) {
            KidButton(
                text = if (round == MatchGameRoundCount - 1) "See result" else "Next round",
                onClick = matchGameViewModel::nextRound,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 18.dp),
            )
        }
    }
}

@Composable
private fun AnswerChoice(
    choice: String,
    index: Int,
    correctAnswer: String,
    selected: String?,
    onSelect: () -> Unit,
) {
    val isCorrect = selected != null && choice == correctAnswer
    val isWrong = selected == choice && choice != correctAnswer
    val background = when {
        isCorrect -> KidColors.CorrectContainer
        isWrong -> KidColors.TryAgainContainer
        else -> Color.White
    }
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(enabled = selected == null, onClick = onSelect),
        shape = RoundedCornerShape(17.dp),
        colors = CardDefaults.cardColors(containerColor = background),
        elevation = CardDefaults.cardElevation(2.dp),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = ('A'.code + index).toChar().toString(),
                color = KidColors.Primary,
                fontWeight = FontWeight.ExtraBold,
                modifier = Modifier
                    .size(32.dp)
                    .background(KidColors.PrimaryContainer, RoundedCornerShape(9.dp))
                    .padding(7.dp),
            )
            Text(
                text = choice,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = KidColors.Ink,
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 16.dp),
            )
            Icon(
                imageVector = when {
                    isCorrect -> Icons.Rounded.CheckCircle
                    isWrong -> Icons.Rounded.Cancel
                    else -> Icons.Rounded.RadioButtonUnchecked
                },
                contentDescription = null,
                tint = when {
                    isCorrect -> KidColors.Correct
                    isWrong -> KidColors.TryAgain
                    else -> KidColors.Outline
                },
                modifier = Modifier.size(28.dp),
            )
        }
    }
}

@Composable
private fun GameComplete(score: Int, onPlayAgain: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(KidColors.Background)
            .padding(28.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Icon(Icons.Rounded.EmojiEvents, contentDescription = null, tint = KidColors.Star, modifier = Modifier.size(112.dp))
        Text(
            text = "Great job!",
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.ExtraBold,
            color = KidColors.PrimaryLip,
        )
        Text(
            text = "You scored $score out of $MatchGameRoundCount",
            style = MaterialTheme.typography.titleLarge,
            color = KidColors.InkVariant,
            modifier = Modifier.padding(top = 8.dp, bottom = 24.dp),
        )
        KidButton(text = "Play again", onClick = onPlayAgain)
    }
}

private fun createQuestion(gameId: String, round: Int): GameQuestion {
    return when (gameId) {
        "number" -> {
            val lessons = LearningCatalog.numbers
            val current = lessons[round % lessons.size]
            val choices = choiceIndexes(round, lessons.size).map { lessons[it].number }
            GameQuestion(
                prompt = "Which number matches the picture?",
                answer = current.number,
                image = current.countImage,
                choices = choices,
            )
        }
        "color" -> {
            val lessons = LearningCatalog.colours
            val current = lessons[round % lessons.size]
            val choices = choiceIndexes(round, lessons.size).map { lessons[it].title }
            GameQuestion(
                prompt = "Choose the name of this color.",
                answer = current.title,
                image = current.image,
                choices = choices,
            )
        }
        "object-letter" -> {
            val lessons = LearningCatalog.alphabet
            val current = lessons[round % lessons.size]
            val choices = choiceIndexes(round, lessons.size).map { lessons[it].letter }
            GameQuestion(
                prompt = "Which letter starts this object?",
                answer = current.letter,
                image = current.exampleImage,
                choices = choices,
            )
        }
        else -> {
            val lessons = LearningCatalog.alphabet
            val current = lessons[round % lessons.size]
            val choices = choiceIndexes(round, lessons.size).map { lessons[it].letter.lowercase() }
            GameQuestion(
                prompt = "Match the uppercase letter to its lowercase pair.",
                answer = current.letter.lowercase(),
                image = current.exampleImage,
                choices = choices,
            )
        }
    }
}

private fun choiceIndexes(correct: Int, size: Int): List<Int> {
    val answer = correct % size
    val distractors = (0 until size)
        .filterNot { it == answer }
        .shuffled(Random(answer))
        .take(2)
    return (distractors + answer).shuffled(Random(answer + 31))
}
