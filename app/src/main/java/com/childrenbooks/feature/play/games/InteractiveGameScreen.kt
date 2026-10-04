package com.childrenbooks.feature.play.games

import androidx.compose.ui.res.stringResource
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.childrenbooks.R
import com.childrenbooks.core.designsystem.KidColors
import com.childrenbooks.core.designsystem.KidShapes
import com.childrenbooks.core.designsystem.KidType
import com.childrenbooks.data.local.LearningCatalog
import com.childrenbooks.core.designsystem.components.KidButton
import kotlin.math.roundToInt

@Composable
fun GameHostScreen(gameId: String) {
    when (gameId) {
        "trace" -> TraceLetterGame()
        "drag" -> DragDropGame()
        "quiz" -> QuizModeGame()
        "find" -> FindObjectGame()
        "picture-match" -> PictureMatchGame()
        "guess-word" -> GuessWordGame()
        "word-lock" -> WordLockGame()
        "word-puzzle" -> WordPuzzleGame()
        else -> MatchGameScreen(gameId)
    }
}

@Composable
internal fun GameShell(
    subtitle: String,
    score: Int,
    content: @Composable ColumnScope.() -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(KidColors.Background)
            .padding(horizontal = 20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = subtitle,
                style = KidType.Label,
                color = KidColors.InkVariant,
                modifier = Modifier.weight(1f),
            )
            GameScoreChip(score)
        }
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(top = 18.dp, bottom = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp),
            content = content,
        )
    }
}

/** Points for this game round. A Material star icon, never a text glyph. */
@Composable
internal fun GameScoreChip(score: Int, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .background(KidColors.SecondaryContainer, KidShapes.Pill)
            .padding(horizontal = 12.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        Icon(Icons.Rounded.Star, contentDescription = null, tint = KidColors.StarStroke, modifier = Modifier.size(20.dp))
        Text(text = score.toString(), style = KidType.Label, color = KidColors.OnSecondaryContainer)
    }
}

@Composable
private fun TraceLetterGame() {
    var path by remember { mutableStateOf(Path()) }
    var points by rememberSaveable { mutableIntStateOf(0) }
    var score by rememberSaveable { mutableIntStateOf(0) }
    val completed = points > 28

    GameShell(
        subtitle = stringResource(R.string.game_trace_hint),
        score = score,
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(430.dp),
            shape = RoundedCornerShape(32.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(5.dp),
        ) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(
                    text = "A",
                    style = MaterialTheme.typography.displayLarge,
                    fontWeight = FontWeight.ExtraBold,
                    color = KidColors.Primary.copy(alpha = 0.13f),
                    fontSize = androidx.compose.ui.unit.TextUnit.Unspecified,
                )
                Text(
                    text = "A",
                    style = MaterialTheme.typography.displayLarge,
                    fontWeight = FontWeight.ExtraBold,
                    color = KidColors.Primary.copy(alpha = 0.2f),
                    modifier = Modifier.padding(bottom = 4.dp),
                )
                Canvas(
                    modifier = Modifier
                        .fillMaxSize()
                        .pointerInput(Unit) {
                            detectDragGestures(
                                onDragStart = { offset ->
                                    path = Path().apply {
                                        addPath(path)
                                        moveTo(offset.x, offset.y)
                                    }
                                },
                                onDrag = { change, _ ->
                                    path = Path().apply {
                                        addPath(path)
                                        lineTo(change.position.x, change.position.y)
                                    }
                                    points++
                                },
                            )
                        },
                ) {
                    drawPath(
                        path = path,
                        color = KidColors.Primary,
                        style = Stroke(width = 24f, cap = StrokeCap.Round),
                    )
                }
            }
        }
        Text(
            text = stringResource(if (completed) R.string.game_trace_done else R.string.game_trace_start),
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.ExtraBold,
            color = if (completed) KidColors.Correct else KidColors.InkVariant,
            textAlign = TextAlign.Center,
        )
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            KidButton(
                text = stringResource(R.string.action_clear),
                onClick = {
                    path = Path()
                    points = 0
                },
            )
            KidButton(
                text = stringResource(R.string.action_done),
                onClick = {
                    if (completed) score++
                    path = Path()
                    points = 0
                },
            )
        }
    }
}

@Composable
private fun DragDropGame() {
    var score by rememberSaveable { mutableIntStateOf(0) }
    var dragged by rememberSaveable { mutableStateOf<String?>(null) }
    var resultRes by rememberSaveable { mutableIntStateOf(R.string.game_drag_start) }
    var resultArg by rememberSaveable { mutableStateOf("") }
    val options = listOf(
        "Apple" to R.drawable.apple,
        "Ball" to R.drawable.b,
        "Cat" to R.drawable.cat,
    )

    GameShell(stringResource(R.string.game_drag_hint), score) {
        DropTarget(title = stringResource(R.string.game_drag_target), subtitle = stringResource(R.string.game_drag_target_sub))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            options.forEach { (name, image) ->
                DraggableObjectCard(
                    name = name,
                    image = image,
                    modifier = Modifier.weight(1f),
                    onDropped = {
                        dragged = name
                        if (name == "Apple") {
                            score++
                            resultRes = R.string.game_drag_correct
                        } else {
                            resultRes = R.string.game_drag_wrong
                            resultArg = name
                        }
                    },
                )
            }
        }
        Text(
            text = stringResource(resultRes, resultArg),
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.ExtraBold,
            color = if (dragged == "Apple") KidColors.Correct else KidColors.InkVariant,
            textAlign = TextAlign.Center,
        )
    }
}

@Composable
private fun DropTarget(title: String, subtitle: String) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(170.dp),
        shape = RoundedCornerShape(30.dp),
        colors = CardDefaults.cardColors(containerColor = KidColors.PrimaryContainer),
        border = androidx.compose.foundation.BorderStroke(3.dp, KidColors.Primary),
    ) {
        Column(
            Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            Text(title, style = MaterialTheme.typography.displayMedium, fontWeight = FontWeight.ExtraBold, color = KidColors.Primary)
            Text(subtitle, color = KidColors.InkVariant, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
private fun DraggableObjectCard(
    name: String,
    image: Int,
    modifier: Modifier,
    onDropped: () -> Unit,
) {
    var offsetX by remember { mutableFloatStateOf(0f) }
    var offsetY by remember { mutableFloatStateOf(0f) }
    Card(
        modifier = modifier
            .offset { IntOffset(offsetX.roundToInt(), offsetY.roundToInt()) }
            .height(150.dp)
            .pointerInput(name) {
                detectDragGestures(
                    onDrag = { _, dragAmount ->
                        offsetX += dragAmount.x
                        offsetY += dragAmount.y
                    },
                    onDragEnd = {
                        onDropped()
                        offsetX = 0f
                        offsetY = 0f
                    },
                )
            },
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(5.dp),
    ) {
        Column(
            Modifier
                .fillMaxSize()
                .padding(10.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Image(
                painter = painterResource(image),
                contentDescription = name,
                modifier = Modifier.weight(1f),
                contentScale = ContentScale.Fit,
            )
            Text(name, fontWeight = FontWeight.ExtraBold, color = KidColors.Ink)
        }
    }
}

@Composable
private fun QuizModeGame() {
    var round by rememberSaveable { mutableIntStateOf(0) }
    var score by rememberSaveable { mutableIntStateOf(0) }
    var selected by rememberSaveable(round) { mutableStateOf<String?>(null) }
    val foods = LearningCatalog.fruits
    val answer = foods[round % foods.size]
    val choices = remember(round) {
        listOf(answer.title, foods[(round + 3) % foods.size].title, foods[(round + 7) % foods.size].title).shuffled()
    }

    GameShell(stringResource(R.string.game_quiz_hint), score) {
        ImageQuestionCard(answer.image, stringResource(R.string.game_quiz_question))
        choices.forEach { choice ->
            QuizChoice(
                choice = choice,
                selected = selected,
                answer = answer.title,
                onClick = {
                    if (selected == null) {
                        selected = choice
                        if (choice == answer.title) score++
                    }
                },
            )
        }
        KidButton(
            text = stringResource(R.string.game_quiz_next),
            enabled = selected != null,
            onClick = {
                selected = null
                round++
            },
        )
    }
}

@Composable
private fun QuizChoice(choice: String, selected: String?, answer: String, onClick: () -> Unit) {
    val color = when {
        selected == null -> Color.White
        choice == answer -> KidColors.CorrectContainer
        choice == selected -> KidColors.TryAgainContainer
        else -> Color.White
    }
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(enabled = selected == null, onClick = onClick),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = color),
        elevation = CardDefaults.cardElevation(2.dp),
    ) {
        Text(
            text = choice,
            modifier = Modifier.padding(18.dp),
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.ExtraBold,
            color = KidColors.Ink,
        )
    }
}

@Composable
private fun FindObjectGame() {
    var targetIndex by rememberSaveable { mutableIntStateOf(0) }
    var score by rememberSaveable { mutableIntStateOf(0) }
    var resultRes by rememberSaveable { mutableIntStateOf(R.string.game_find_start) }
    var resultArg by rememberSaveable { mutableStateOf("") }
    val objects = listOf(
        LearningCatalog.animals[0],
        LearningCatalog.fruits[1],
        LearningCatalog.birds[0],
        LearningCatalog.vegetables[0],
    )
    val target = objects[targetIndex % objects.size]

    GameShell(stringResource(R.string.game_find_title, target.title), score) {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            objects.forEach { item ->
                Card(
                    modifier = Modifier
                        .weight(1f)
                        .height(142.dp)
                        .clickable {
                            if (item.title == target.title) {
                                score++
                                resultRes = R.string.game_find_found
                                resultArg = item.title
                                targetIndex++
                            } else {
                                resultRes = R.string.game_find_oops
                                resultArg = target.title
                            }
                        },
                    shape = RoundedCornerShape(22.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(4.dp),
                ) {
                    Image(
                        painter = painterResource(item.image),
                        contentDescription = item.title,
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(8.dp),
                        contentScale = ContentScale.Fit,
                    )
                }
            }
        }
        Text(stringResource(resultRes, resultArg), style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.ExtraBold, color = KidColors.Primary, textAlign = TextAlign.Center)
        Text(stringResource(R.string.game_find_next, target.title), color = KidColors.InkVariant, fontWeight = FontWeight.Bold)
    }
}

@Composable
private fun ImageQuestionCard(image: Int, title: String) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(250.dp),
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(5.dp),
    ) {
        Column(
            Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(title, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.ExtraBold, color = KidColors.Primary)
            Image(
                painter = painterResource(image),
                contentDescription = title,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentScale = ContentScale.Fit,
            )
        }
    }
}
