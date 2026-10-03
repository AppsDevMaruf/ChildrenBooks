package com.childrenbooks.ui.games

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
import androidx.compose.material3.Card
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
import com.childrenbooks.data.local.LearningCatalog
import com.childrenbooks.ui.components.AppPillButton
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
            .background(Color(0xFFF8FBFF))
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Column(Modifier.weight(1f)) {
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.titleMedium,
                    color = Color(0xFF526573),
                    fontWeight = FontWeight.Bold,
                )
            }
            Text(
                text = "★ $score",
                fontWeight = FontWeight.ExtraBold,
                color = Color(0xFF7B4A10),
                modifier = Modifier
                    .background(Color(0xFFFFE3B7), RoundedCornerShape(18.dp))
                    .padding(horizontal = 14.dp, vertical = 8.dp),
            )
        }
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(top = 18.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp),
            content = content,
        )
    }
}

@Composable
private fun TraceLetterGame() {
    var path by remember { mutableStateOf(Path()) }
    var points by rememberSaveable { mutableIntStateOf(0) }
    var score by rememberSaveable { mutableIntStateOf(0) }
    val completed = points > 28

    GameShell(
        subtitle = "Finger দিয়ে dotted A এর উপর লিখো",
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
                    color = Color(0x22165BBE),
                    fontSize = androidx.compose.ui.unit.TextUnit.Unspecified,
                )
                Text(
                    text = "A",
                    style = MaterialTheme.typography.displayLarge,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color(0x33165BBE),
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
                        color = Color(0xFF0875DA),
                        style = Stroke(width = 24f, cap = StrokeCap.Round),
                    )
                }
            }
        }
        Text(
            text = if (completed) "Great tracing! 🎉" else "Start at the top and trace the letter.",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.ExtraBold,
            color = if (completed) Color(0xFF2E9B42) else Color(0xFF526573),
            textAlign = TextAlign.Center,
        )
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            AppPillButton(
                text = "Clear",
                onClick = {
                    path = Path()
                    points = 0
                },
            )
            AppPillButton(
                text = "Done",
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
    var result by rememberSaveable { mutableStateOf("Drag Apple to Letter A") }
    val options = listOf(
        "Apple" to R.drawable.apple,
        "Ball" to R.drawable.b,
        "Cat" to R.drawable.cat,
    )

    GameShell("Correct object target box-e drop koro", score) {
        DropTarget(title = "Letter A", subtitle = "Starts with Apple")
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
                            result = "Correct! Apple → A 🎉"
                        } else {
                            result = "Try again. $name does not start with A."
                        }
                    },
                )
            }
        }
        Text(
            text = result,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.ExtraBold,
            color = if (dragged == "Apple") Color(0xFF2E9B42) else Color(0xFF526573),
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
        colors = CardDefaults.cardColors(containerColor = Color(0xFFEAF6FF)),
        border = androidx.compose.foundation.BorderStroke(3.dp, Color(0xFF8CCBFA)),
    ) {
        Column(
            Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            Text(title, style = MaterialTheme.typography.displayMedium, fontWeight = FontWeight.ExtraBold, color = Color(0xFF165BBE))
            Text(subtitle, color = Color(0xFF526573), fontWeight = FontWeight.Bold)
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
            Text(name, fontWeight = FontWeight.ExtraBold, color = Color(0xFF243745))
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

    GameShell("Correct answer choose koro", score) {
        ImageQuestionCard(answer.image, "What is this?")
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
        AppPillButton(
            text = "Next Quiz",
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
        choice == answer -> Color(0xFFE1F7E8)
        choice == selected -> Color(0xFFFFE5E5)
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
            color = Color(0xFF243745),
        )
    }
}

@Composable
private fun FindObjectGame() {
    var targetIndex by rememberSaveable { mutableIntStateOf(0) }
    var score by rememberSaveable { mutableIntStateOf(0) }
    var result by rememberSaveable { mutableStateOf("Tap the target object") }
    val objects = listOf(
        LearningCatalog.animals[0],
        LearningCatalog.fruits[1],
        LearningCatalog.birds[0],
        LearningCatalog.vegetables[0],
    )
    val target = objects[targetIndex % objects.size]

    GameShell("Find: ${target.title}", score) {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            objects.forEach { item ->
                Card(
                    modifier = Modifier
                        .weight(1f)
                        .height(142.dp)
                        .clickable {
                            if (item.title == target.title) {
                                score++
                                result = "Found ${item.title}! 🎉"
                                targetIndex++
                            } else {
                                result = "Oops, find ${target.title}."
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
        Text(result, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.ExtraBold, color = Color(0xFF165BBE), textAlign = TextAlign.Center)
        Text("Next target: ${target.title}", color = Color(0xFF526573), fontWeight = FontWeight.Bold)
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
            Text(title, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.ExtraBold, color = Color(0xFF165BBE))
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
