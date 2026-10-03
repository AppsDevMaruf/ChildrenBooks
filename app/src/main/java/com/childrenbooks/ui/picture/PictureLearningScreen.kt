package com.childrenbooks.ui.picture

import android.speech.tts.TextToSpeech
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.childrenbooks.data.local.LearningCatalog
import com.childrenbooks.domain.model.PictureLesson
import com.childrenbooks.ui.components.CircleTextButton
import com.childrenbooks.ui.components.PagerControls
import com.childrenbooks.ui.components.ResizableText
import com.childrenbooks.ui.components.SoundIconButton
import com.childrenbooks.ui.components.StarProgressBar
import com.childrenbooks.ui.state.SelectionViewModel
import java.util.Locale

private data class PictureCardStyle(
    val background: Color,
    val border: Color,
    val text: Color,
    val landscape: Color,
)

private val animalCardStyles = listOf(
    PictureCardStyle(Color(0xFFEAF8DA), Color(0xFF98CF54), Color(0xFF2E8A20), Color(0xFFBEE891)),
    PictureCardStyle(Color(0xFFE2F4FF), Color(0xFF7CC7F3), Color(0xFF1266C4), Color(0xFF82CCFF)),
    PictureCardStyle(Color(0xFFFFF0CC), Color(0xFFFFC96B), Color(0xFFF07100), Color(0xFFFFC45C)),
    PictureCardStyle(Color(0xFFF1E4FF), Color(0xFFB78EEB), Color(0xFF7434C4), Color(0xFFC7A7F6)),
)

@Composable
fun AnimalsLearningScreen(onBack: () -> Unit) {
    PictureLearningScreen(
        title = "Animals",
        items = LearningCatalog.animals,
        titleColor = Color(0xFF165BBE),
        accentColor = Color(0xFF2678F2),
        styles = animalCardStyles,
        onBack = onBack,
    )
}

@Composable
fun FishesLearningScreen(onBack: () -> Unit) {
    PictureLearningScreen(
        title = "Fishes",
        items = LearningCatalog.fishes,
        titleColor = Color(0xFF0C7ACF),
        accentColor = Color(0xFF0C7ACF),
        styles = fishCardStyles,
        onBack = onBack,
    )
}
@Composable
fun FoodsLearningScreen(onBack: () -> Unit) {
    PictureLearningScreen(
        title = "Foods",
        items = LearningCatalog.foods,
        titleColor = Color(0xFFFF7A00),
        accentColor = Color(0xFFFF8A00),
        styles = foodCardStyles,
        onBack = onBack,
    )
}
@Composable
fun FlowersLearningScreen(onBack: () -> Unit) {
    PictureLearningScreen(
        title = "Flowers",
        items = LearningCatalog.flowers,
        titleColor = Color(0xFFE8328C),
        accentColor = Color(0xFFE8328C),
        styles = flowerCardStyles,
        onBack = onBack,
    )
}
@Composable
fun BirdsLearningScreen(onBack: () -> Unit) {
    PictureLearningScreen(
        title = "Birds",
        items = LearningCatalog.birds,
        titleColor = Color(0xFF0C7ACF),
        accentColor = Color(0xFF0C7ACF),
        styles = birdCardStyles,
        onBack = onBack,
    )
}
@Composable
fun FurnitureLearningScreen(onBack: () -> Unit) {
    PictureLearningScreen(
        title = "Furniture",
        items = LearningCatalog.furniture,
        titleColor = Color(0xFF9B5A1A),
        accentColor = Color(0xFFC97826),
        styles = furnitureCardStyles,
        onBack = onBack,
    )
}
@Composable
fun FruitsLearningScreen(onBack: () -> Unit) {
    PictureLearningScreen(
        title = "Fruits",
        items = LearningCatalog.fruits,
        titleColor = Color(0xFFE84A3A),
        accentColor = Color(0xFFFF7043),
        styles = fruitCardStyles,
        onBack = onBack,
    )
}
@Composable
fun VegetablesLearningScreen(onBack: () -> Unit) {
    PictureLearningScreen(
        title = "Vegetables",
        items = LearningCatalog.vegetables,
        titleColor = Color(0xFF2E9B42),
        accentColor = Color(0xFF35B64B),
        styles = vegetableCardStyles,
        onBack = onBack,
    )
}
@Composable
fun ShapesLearningScreen(onBack: () -> Unit) {
    PictureLearningScreen(
        title = "Shapes",
        items = LearningCatalog.shapes,
        titleColor = Color(0xFF7A35D8),
        accentColor = Color(0xFF7A35D8),
        styles = shapeCardStyles,
        onBack = onBack,
    )
}
private val foodCardStyles = listOf(
    PictureCardStyle(Color(0xFFFFF0CC), Color(0xFFFFC45D), Color(0xFFFF7A00), Color(0xFFFFD375)),
    PictureCardStyle(Color(0xFFFFE2E2), Color(0xFFFF8F8F), Color(0xFFE64242), Color(0xFFFFA0A0)),
    PictureCardStyle(Color(0xFFE7F7D9), Color(0xFF8FD765), Color(0xFF3B9B24), Color(0xFFA6E57F)),
    PictureCardStyle(Color(0xFFFFE8F0), Color(0xFFFF98BE), Color(0xFFE73578), Color(0xFFFFB3D0)),
)
private val fishCardStyles = listOf(
    PictureCardStyle(Color(0xFFE4F8FF), Color(0xFF7CC7F3), Color(0xFF0C7ACF), Color(0xFF9EE2FF)),
    PictureCardStyle(Color(0xFFEAF0FF), Color(0xFF8FB8FF), Color(0xFF266FE0), Color(0xFFA2CCFF)),
    PictureCardStyle(Color(0xFFDFF9F5), Color(0xFF7FDAD1), Color(0xFF168A9A), Color(0xFF94E8DF)),
    PictureCardStyle(Color(0xFFF1E4FF), Color(0xFFB78EEB), Color(0xFF7434C4), Color(0xFFC7A7F6)),
)
private val flowerCardStyles = listOf(
    PictureCardStyle(Color(0xFFFFE7F2), Color(0xFFFF91C3), Color(0xFFE8328C), Color(0xFFFFB8D7)),
    PictureCardStyle(Color(0xFFFFF3CC), Color(0xFFFFD65E), Color(0xFFED9700), Color(0xFFFFDF75)),
    PictureCardStyle(Color(0xFFEDE4FF), Color(0xFFC39AFF), Color(0xFF7C3ED8), Color(0xFFD4B7FF)),
    PictureCardStyle(Color(0xFFE5F7E5), Color(0xFF8FDB8D), Color(0xFF2E9B42), Color(0xFFA9E9AA)),
)
private val birdCardStyles = listOf(
    PictureCardStyle(Color(0xFFE5F6FF), Color(0xFF7FCBFA), Color(0xFF0C7ACF), Color(0xFF96DBFF)),
    PictureCardStyle(Color(0xFFFFEBD9), Color(0xFFFFB277), Color(0xFFE86D18), Color(0xFFFFC18E)),
    PictureCardStyle(Color(0xFFE9F2FF), Color(0xFF88B9FF), Color(0xFF266FE0), Color(0xFFA2CCFF)),
    PictureCardStyle(Color(0xFFF0E6FF), Color(0xFFB998F2), Color(0xFF7542D4), Color(0xFFCDB6F8)),
)
private val furnitureCardStyles = listOf(
    PictureCardStyle(Color(0xFFFFEBD9), Color(0xFFD7A36A), Color(0xFF9B5A1A), Color(0xFFFFC996)),
    PictureCardStyle(Color(0xFFEAF7E5), Color(0xFF9ED487), Color(0xFF4E8D33), Color(0xFFBEE7A8)),
    PictureCardStyle(Color(0xFFE5F2FF), Color(0xFF89BFF2), Color(0xFF2E73BD), Color(0xFFA8D0F5)),
    PictureCardStyle(Color(0xFFFFE4EC), Color(0xFFFF9AB6), Color(0xFFD64C72), Color(0xFFFFB6CA)),
)
private val fruitCardStyles = listOf(
    PictureCardStyle(Color(0xFFFFE2E2), Color(0xFFFF8F8F), Color(0xFFE64242), Color(0xFFFFA0A0)),
    PictureCardStyle(Color(0xFFFFF3CC), Color(0xFFFFD65E), Color(0xFFED9700), Color(0xFFFFDF75)),
    PictureCardStyle(Color(0xFFE7F7D9), Color(0xFF8FD765), Color(0xFF3B9B24), Color(0xFFA6E57F)),
    PictureCardStyle(Color(0xFFF0E6FF), Color(0xFFB998F2), Color(0xFF7542D4), Color(0xFFCDB6F8)),
)

private val vegetableCardStyles = listOf(
    PictureCardStyle(Color(0xFFE7F7D9), Color(0xFF8FD765), Color(0xFF2E9B42), Color(0xFFA6E57F)),
    PictureCardStyle(Color(0xFFFFE8D6), Color(0xFFFFB06A), Color(0xFFE86D18), Color(0xFFFFC18E)),
    PictureCardStyle(Color(0xFFE5F7E5), Color(0xFF8FDB8D), Color(0xFF2E9B42), Color(0xFFA9E9AA)),
    PictureCardStyle(Color(0xFFFFE4EA), Color(0xFFFF94A9), Color(0xFFD73552), Color(0xFFFFB2C0)),
)

private val shapeCardStyles = listOf(
    PictureCardStyle(Color(0xFFFFE4EA), Color(0xFFFF94A9), Color(0xFFD73552), Color(0xFFFFB2C0)),
    PictureCardStyle(Color(0xFFE5F2FF), Color(0xFF89BFF2), Color(0xFF2E73BD), Color(0xFFA8D0F5)),
    PictureCardStyle(Color(0xFFE9F9D8), Color(0xFFA8DF69), Color(0xFF559C23), Color(0xFFBFEA89)),
    PictureCardStyle(Color(0xFFF0E6FF), Color(0xFFB998F2), Color(0xFF7542D4), Color(0xFFCDB6F8)),
)

@Composable
private fun PictureLearningScreen(
    title: String,
    items: List<PictureLesson>,
    titleColor: Color,
    accentColor: Color,
    styles: List<PictureCardStyle>,
    onBack: () -> Unit,
    selectionViewModel: SelectionViewModel = viewModel(),
) {
    require(items.isNotEmpty()) { "Picture learning requires at least one item" }
    val speak = rememberTextSpeaker()
    val storedSelectedIndex by selectionViewModel.selectedIndex.collectAsStateWithLifecycle()
    val selectedIndex = storedSelectedIndex.coerceIn(items.indices)
    val currentItem = items[selectedIndex]

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(Color(0xFFEFFAFF), Color(0xFFFFFFFF), Color(0xFFF8FCFF)),
                ),
            )
            .padding(horizontal = 18.dp, vertical = 18.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        AnimalsHeader(
            title = title,
            selectedIndex = selectedIndex,
            total = items.size,
            titleColor = titleColor,
            accentColor = accentColor,
            onBack = onBack,
            onSound = { speak(currentItem.title) },
        )
        LazyColumn(
            modifier = Modifier.weight(1f),
            contentPadding = PaddingValues(top = 16.dp, bottom = 20.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            itemsIndexed(items, key = { _, item -> item.title }) { index, item ->
                AnimalLearningCard(
                    item = item,
                    style = styles[index % styles.size],
                    selected = selectedIndex == index,
                    onClick = {
                        selectionViewModel.select(index, items.lastIndex)
                        speak(item.title)
                    },
                )
            }
        }
        AnimalsBottomControls(
            previousEnabled = selectedIndex > 0,
            nextEnabled = selectedIndex < items.lastIndex,
            onPrevious = {
                val previousIndex = (selectedIndex - 1).coerceAtLeast(0)
                selectionViewModel.select(previousIndex, items.lastIndex)
                speak(items[previousIndex].title)
            },
            onNext = {
                val nextIndex = (selectedIndex + 1).coerceAtMost(items.lastIndex)
                selectionViewModel.select(nextIndex, items.lastIndex)
                speak(items[nextIndex].title)
            },
            accentColor = accentColor,
        )
    }
}

@Composable
private fun AnimalsHeader(
    title: String,
    selectedIndex: Int,
    total: Int,
    titleColor: Color,
    accentColor: Color,
    onBack: () -> Unit,
    onSound: () -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.Top,
    ) {
        CircleTextButton(
            label = "‹",
            size = 44.dp,
            chevronSize = 24.dp,
            onClick = onBack,
        )
        Column(
            modifier = Modifier.weight(1f),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                ResizableText(
                    text = title,
                    style = MaterialTheme.typography.displayMedium,
                    fontWeight = FontWeight.ExtraBold,
                    color = titleColor,
                    textAlign = TextAlign.Center,
                    minTextSize = 24.sp,
                    maxTextSize = 48.sp,
                )
            }
        }
        SoundIconButton(
            contentColor = accentColor,
            size = 44.dp,
            iconSize = 24.dp,
            onClick = onSound,
        )
    }
    StarProgressBar(
        progress = (selectedIndex + 1f) / total,
        label = "${selectedIndex + 1}/$total",
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 16.dp),
        fillColor = accentColor,
        labelColor = accentColor,
    )
}

@Composable
private fun AnimalLearningCard(
    item: PictureLesson,
    style: PictureCardStyle,
    selected: Boolean,
    onClick: () -> Unit,
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(190.dp)
            .clip(RoundedCornerShape(26.dp)),
        shape = RoundedCornerShape(26.dp),
        colors = CardDefaults.cardColors(containerColor = style.background),
        elevation = CardDefaults.cardElevation(if (selected) 8.dp else 3.dp),
        border = androidx.compose.foundation.BorderStroke(2.dp, style.border),
        onClick = onClick,
    ) {
        Row(Modifier.fillMaxSize()) {
            Image(
                painter = painterResource(item.image),
                contentDescription = item.title,
                modifier = Modifier
                    .weight(1.24f)
                    .fillMaxSize()
                    .clip(RoundedCornerShape(topStart = 24.dp, bottomStart = 24.dp)),
                contentScale = ContentScale.Crop,
            )
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            listOf(style.background, Color.White.copy(alpha = 0.45f), style.landscape.copy(alpha = 0.6f)),
                        ),
                    ),
            ) {
                Text(
                    text = "♧",
                    color = Color.White.copy(alpha = 0.55f),
                    style = MaterialTheme.typography.headlineSmall,
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(16.dp),
                )
                ResizableText(
                    text = item.title,
                    modifier = Modifier
                        .align(Alignment.Center)
                        .padding(horizontal = 12.dp)
                        .padding(bottom = 42.dp),
                    style = MaterialTheme.typography.displaySmall,
                    fontWeight = FontWeight.ExtraBold,
                    color = style.text,
                    textAlign = TextAlign.Center,
                    minTextSize = 24.sp,
                    maxTextSize = 38.sp,
                )
                SoundIconButton(
                    contentColor = style.text,
                    modifier = Modifier
                        .align(Alignment.Center)
                        .padding(top = 82.dp),
                    size = 44.dp,
                    iconSize = 24.dp,
                    onClick = onClick,
                )
            }
        }
    }
}

@Composable
private fun AnimalsBottomControls(
    previousEnabled: Boolean,
    nextEnabled: Boolean,
    onPrevious: () -> Unit,
    onNext: () -> Unit,
    accentColor: Color,
) {
    PagerControls(
        previousEnabled = previousEnabled,
        nextEnabled = nextEnabled,
        onPrevious = onPrevious,
        onNext = onNext,
        previousContentColor = accentColor,
        nextContainerColor = accentColor,
        disabledContentColor = Color(0xFF8FA9C8),
        topPadding = 8.dp,
    )
}

@Composable
private fun rememberTextSpeaker(): (String) -> Unit {
    val context = LocalContext.current
    var tts by remember { mutableStateOf<TextToSpeech?>(null) }
    DisposableEffect(context) {
        var disposed = false
        var engine: TextToSpeech? = null
        engine = TextToSpeech(context.applicationContext) { status ->
            if (disposed) {
                engine?.shutdown()
            } else if (status == TextToSpeech.SUCCESS) {
                engine?.language = Locale.US
                engine?.setPitch(1.55f)
                engine?.setSpeechRate(0.88f)
            } else {
                engine?.shutdown()
                tts = null
            }
        }
        tts = engine
        onDispose {
            disposed = true
            engine.stop()
            engine.shutdown()
            tts = null
        }
    }
    return { text ->
        tts?.speak(text, TextToSpeech.QUEUE_FLUSH, null, "remote-item-$text")
    }
}
