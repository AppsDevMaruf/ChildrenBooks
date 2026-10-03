package com.childrenbooks.ui.listen

import android.speech.tts.TextToSpeech
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.compose.ui.unit.dp
import com.childrenbooks.R
import com.childrenbooks.ui.components.ChevronRightIcon
import java.util.Locale

private data class ListenFeature(
    val title: String,
    val subtitle: String,
    val image: Int,
    val colors: List<Color>,
    val script: String,
)

enum class ListenCategory(
    val routeId: String,
    val title: String,
    val subtitle: String,
    val image: Int,
    val colors: List<Color>,
) {
    AnimalSounds(
        routeId = "animal-sounds",
        title = "Animal Sounds",
        subtitle = "10 sounds • tap and hear",
        image = R.drawable.animal_dog,
        colors = listOf(Color(0xFF19A8F7), Color(0xFF126FE8)),
    ),
    KidsStories(
        routeId = "kids-stories",
        title = "Kids Stories",
        subtitle = "5 short stories",
        image = R.drawable.home_slider_2,
        colors = listOf(Color(0xFFFF78A7), Color(0xFFE94D8D)),
    ),
    Rhymes(
        routeId = "rhymes",
        title = "Rhymes",
        subtitle = "20 nursery rhymes",
        image = R.drawable.play_and_learn,
        colors = listOf(Color(0xFF5FDA8A), Color(0xFF21B864)),
    );

    companion object {
        fun fromRouteId(routeId: String): ListenCategory? = entries.firstOrNull { it.routeId == routeId }
    }
}

private val animalSoundFeatures = listOf(
    ListenFeature("Dog Sound", "Dog says woof woof", R.drawable.animal_dog, listOf(Color(0xFF19A8F7), Color(0xFF126FE8)), "Dog. Woof woof. The friendly dog says woof woof."),
    ListenFeature("Cat Sound", "Cat says meow", R.drawable.animal_cat, listOf(Color(0xFFFF78A7), Color(0xFFE94D8D)), "Cat. Meow meow. The soft little cat says meow."),
    ListenFeature("Cow Sound", "Cow says moo", R.drawable.animal_cow, listOf(Color(0xFF7ED957), Color(0xFF19B86B)), "Cow. Moo moo. The big cow says moo."),
    ListenFeature("Lion Sound", "Lion says roar", R.drawable.animal_lion, listOf(Color(0xFFFFB547), Color(0xFFFF7A00)), "Lion. Roar roar. The brave lion says roar."),
    ListenFeature("Monkey Sound", "Monkey says oo oo aa aa", R.drawable.animal_monkey, listOf(Color(0xFFFFD65E), Color(0xFFFF9F1C)), "Monkey. Oo oo aa aa. The funny monkey jumps and calls."),
    ListenFeature("Horse Sound", "Horse says neigh", R.drawable.animal_horse, listOf(Color(0xFFC78B55), Color(0xFF8E5626)), "Horse. Neigh neigh. The happy horse says neigh."),
    ListenFeature("Sheep Sound", "Sheep says baa", R.drawable.animal_sheep, listOf(Color(0xFFB9E8FF), Color(0xFF58BEE8)), "Sheep. Baa baa. The fluffy sheep says baa."),
    ListenFeature("Duck Sound", "Duck says quack", R.drawable.bird_duck, listOf(Color(0xFFFFE36E), Color(0xFFFFB300)), "Duck. Quack quack. The duck swims and says quack."),
    ListenFeature("Bird Sound", "Bird says tweet", R.drawable.bird_parrot, listOf(Color(0xFF7B61FF), Color(0xFF23C6FF)), "Bird. Tweet tweet. The little bird sings tweet tweet."),
    ListenFeature("Elephant Sound", "Elephant trumpets", R.drawable.animal_elephant, listOf(Color(0xFF9CA6FF), Color(0xFF5968D8)), "Elephant. Pawoo pawoo. The gentle elephant makes a trumpet sound."),
)

private val kidsStoryFeatures = listOf(
    ListenFeature(
        title = "The Kind Bunny",
        subtitle = "Story 1 • sharing",
        image = R.drawable.animal_rabbit,
        colors = listOf(Color(0xFFFF9AC7), Color(0xFFE85BA1)),
        script = "The kind bunny found one carrot. A hungry friend came by. Bunny smiled and shared the carrot. Both friends felt happy and full.",
    ),
    ListenFeature(
        title = "The Brave Little Fish",
        subtitle = "Story 2 • courage",
        image = R.drawable.fish,
        colors = listOf(Color(0xFF3DDCFF), Color(0xFF0C7ACF)),
        script = "A little fish was scared to swim far. Then it saw a tiny bubble path. The fish followed slowly and found a beautiful coral garden.",
    ),
    ListenFeature(
        title = "Mina's Magic Book",
        subtitle = "Story 3 • reading",
        image = R.drawable.home_slider_2,
        colors = listOf(Color(0xFF9B6BFF), Color(0xFF5F39DE)),
        script = "Mina opened a blue book. The pictures became bright and friendly. She learned letters, numbers, colors, and a happy song.",
    ),
    ListenFeature(
        title = "The Helpful Panda",
        subtitle = "Story 4 • helping",
        image = R.drawable.animal_panda,
        colors = listOf(Color(0xFF5FDA8A), Color(0xFF21B864)),
        script = "Panda saw toys on the floor. He picked them up one by one. The room became clean, and everyone clapped for helpful Panda.",
    ),
    ListenFeature(
        title = "The Rainbow Picnic",
        subtitle = "Story 5 • colors",
        image = R.drawable.rainbo,
        colors = listOf(Color(0xFFFFB547), Color(0xFFFF5E8D)),
        script = "The children packed red apples, yellow bananas, and green grapes. After rain, a rainbow appeared. They named every color together.",
    ),
)

private val listenFeatures = listOf(
    ListenFeature(
        title = "Starry Sky",
        subtitle = "Rhyme 1 • calm night",
        image = R.drawable.shape_star,
        colors = listOf(Color(0xFF19A8F7), Color(0xFF126FE8)),
        script = "Starry sky, soft and bright. Little dreams glow through the night. Close your eyes and smile with me. Happy hearts are wild and free.",
    ),
    ListenFeature(
        title = "Happy Apple",
        subtitle = "Rhyme 2 • fruit fun",
        image = R.drawable.fruit_apple,
        colors = listOf(Color(0xFFFF6F7F), Color(0xFFE83A59)),
        script = "Apple red, apple sweet. Crunchy fruit is good to eat. One small bite and one big grin. Healthy habits now begin.",
    ),
    ListenFeature(
        title = "Little Bunny",
        subtitle = "Rhyme 3 • animal jump",
        image = R.drawable.animal_rabbit,
        colors = listOf(Color(0xFFFF78A7), Color(0xFFE94D8D)),
        script = "Little bunny hops around. Tiny feet tap on the ground. Hop hop here and hop hop there. Bunny dances everywhere.",
    ),
    ListenFeature(
        title = "Rainbow Day",
        subtitle = "Rhyme 4 • colors",
        image = R.drawable.rainbo,
        colors = listOf(Color(0xFF7B61FF), Color(0xFF23C6FF)),
        script = "Red and yellow, green and blue. Rainbow colors smile at you. Point and name them one by one. Learning colors is such fun.",
    ),
    ListenFeature(
        title = "Counting Steps",
        subtitle = "Rhyme 5 • numbers",
        image = R.drawable.number,
        colors = listOf(Color(0xFFFFB547), Color(0xFFFF7A00)),
        script = "One step, two step, three and four. Tiny feet go to the door. Five steps, six steps, clap with me. Counting makes us bright and free.",
    ),
    ListenFeature(
        title = "Sunny Morning",
        subtitle = "Rhyme 6 • wake up",
        image = R.drawable.days,
        colors = listOf(Color(0xFFFFD65E), Color(0xFFFF9F1C)),
        script = "Good morning sun, hello new day. Wash your face and start to play. Read a book and sing along. Morning time is bright and strong.",
    ),
    ListenFeature(
        title = "Tiny Fish",
        subtitle = "Rhyme 7 • water",
        image = R.drawable.fish,
        colors = listOf(Color(0xFF3DDCFF), Color(0xFF0C7ACF)),
        script = "Tiny fish in water blue. Swims around and waves to you. Bubble bubble, splash and glide. Happy fish goes side to side.",
    ),
    ListenFeature(
        title = "Busy Bee",
        subtitle = "Rhyme 8 • nature",
        image = R.drawable.bee,
        colors = listOf(Color(0xFFFFE36E), Color(0xFFFFB300)),
        script = "Busy bee goes buzz buzz buzz. Flying gently just because. Flower here and flower there. Sweet little bee is in the air.",
    ),
    ListenFeature(
        title = "Sleepy Moon",
        subtitle = "Rhyme 9 • bedtime",
        image = R.drawable.months,
        colors = listOf(Color(0xFF8F7CFF), Color(0xFF5846D6)),
        script = "Sleepy moon is shining high. Soft white clouds are drifting by. Teddy waits beside the bed. Sweet dreams dance inside your head.",
    ),
    ListenFeature(
        title = "Green Garden",
        subtitle = "Rhyme 10 • vegetables",
        image = R.drawable.vegetable_carrot,
        colors = listOf(Color(0xFF5FDA8A), Color(0xFF21B864)),
        script = "Carrot orange, leafy green. Freshest garden ever seen. Water plants and watch them grow. Healthy foods help children glow.",
    ),
    ListenFeature(
        title = "Friendly Dog",
        subtitle = "Rhyme 11 • animal sound",
        image = R.drawable.animal_dog,
        colors = listOf(Color(0xFF19A8F7), Color(0xFF126FE8)),
        script = "Friendly dog says woof woof woof. Wagging tail and tiny hoof. Run and fetch and sit down too. Doggy loves to play with you.",
    ),
    ListenFeature(
        title = "Book Time",
        subtitle = "Rhyme 12 • reading",
        image = R.drawable.home_slider_2,
        colors = listOf(Color(0xFF9B6BFF), Color(0xFF5F39DE)),
        script = "Open book and turn the page. Stories sparkle on the stage. Words can fly and pictures sing. Reading is a magic thing.",
    ),
    ListenFeature(
        title = "Shape Parade",
        subtitle = "Rhyme 13 • shapes",
        image = R.drawable.shape_circle,
        colors = listOf(Color(0xFFFF8AC4), Color(0xFFC74BFF)),
        script = "Circle rolls and square stands still. Triangle climbs a tiny hill. Star shines bright and heart says hi. Shapes are dancing in the sky.",
    ),
    ListenFeature(
        title = "Rainy Day",
        subtitle = "Rhyme 14 • weather",
        image = R.drawable.ranyday,
        colors = listOf(Color(0xFF5EC8FF), Color(0xFF3D78E8)),
        script = "Raindrops tap on window glass. Little clouds are moving past. Boots go splash and umbrellas sway. We can smile on rainy day.",
    ),
    ListenFeature(
        title = "Flower Smile",
        subtitle = "Rhyme 15 • flowers",
        image = R.drawable.flower_sunflower,
        colors = listOf(Color(0xFFFFC047), Color(0xFFFF7A7A)),
        script = "Flower smiles in morning light. Petals soft and colors bright. Bees come near and butterflies play. Garden friends say hello today.",
    ),
    ListenFeature(
        title = "Little Train",
        subtitle = "Rhyme 16 • movement",
        image = R.drawable.play_and_learn,
        colors = listOf(Color(0xFF5FDA8A), Color(0xFF21B864)),
        script = "Little train goes choo choo choo. Rolling past the hills so blue. Stop and wave, then start again. Learning rides the happy train.",
    ),
    ListenFeature(
        title = "Clean Hands",
        subtitle = "Rhyme 17 • habit",
        image = R.drawable.flower_water_lily,
        colors = listOf(Color(0xFF7FE9E0), Color(0xFF16A7A8)),
        script = "Wash your hands with bubbly foam. Clean and fresh before you roam. Rub and rinse and dry them too. Healthy hands are good for you.",
    ),
    ListenFeature(
        title = "Kind Friends",
        subtitle = "Rhyme 18 • manners",
        image = R.drawable.animal_panda,
        colors = listOf(Color(0xFFFFA6D1), Color(0xFFE85BA1)),
        script = "Kind words make a sunny day. Please and thank you light the way. Share a toy and help a friend. Happy hearts will never end.",
    ),
    ListenFeature(
        title = "Bird Song",
        subtitle = "Rhyme 19 • birds",
        image = R.drawable.bird_parrot,
        colors = listOf(Color(0xFF7ED957), Color(0xFF19B86B)),
        script = "Little bird sings tweet tweet tweet. Perched upon a branch so neat. Flap your wings and fly up high. Music floats across the sky.",
    ),
    ListenFeature(
        title = "Good Night",
        subtitle = "Rhyme 20 • sleep",
        image = R.drawable.shape_heart,
        colors = listOf(Color(0xFF5FDA8A), Color(0xFF21B864)),
        script = "Good night toys and good night light. Good night moon so soft and bright. Close your eyes and rest your head. Dreams are waiting near your bed.",
    ),
)

@Composable
fun ListenScreen(onOpenCategory: (ListenCategory) -> Unit) {
    LazyColumn(
        modifier = listenScreenModifier(),
        contentPadding = PaddingValues(18.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        item {
            ListenHero()
        }
        item {
            ListenSectionTitle("Choose a section")
        }
        items(ListenCategory.entries, key = { it.routeId }) { category ->
            ListenCategoryCard(category = category, onClick = { onOpenCategory(category) })
        }
        item { Spacer(Modifier.height(88.dp)) }
    }
}

@Composable
fun ListenCategoryScreen(category: ListenCategory) {
    val speak = rememberListenSpeaker()
    val features = remember(category) { listenFeaturesFor(category) }

    LazyColumn(
        modifier = listenScreenModifier(),
        contentPadding = PaddingValues(18.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        item {
            ListenCategoryHero(category)
        }
        items(features, key = { it.title }) { feature ->
            ListenFeatureCard(feature = feature, onPlay = { speak(feature.script) })
        }
        item { Spacer(Modifier.height(88.dp)) }
    }
}

private fun listenFeaturesFor(category: ListenCategory): List<ListenFeature> = when (category) {
    ListenCategory.AnimalSounds -> animalSoundFeatures
    ListenCategory.KidsStories -> kidsStoryFeatures
    ListenCategory.Rhymes -> listenFeatures
}

private fun listenScreenModifier(): Modifier = Modifier
    .fillMaxSize()
    .background(
        Brush.verticalGradient(
            listOf(Color(0xFFFFFBF3), Color(0xFFFFFFFF), Color(0xFFFFF7E8)),
        ),
    )

@Composable
private fun ListenSectionTitle(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleLarge,
        fontWeight = FontWeight.ExtraBold,
        color = Color(0xFF091340),
        modifier = Modifier.padding(top = 4.dp),
    )
}

@Composable
private fun ListenHero() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(150.dp)
            .clip(RoundedCornerShape(30.dp))
            .background(
                Brush.linearGradient(
                    listOf(Color(0xFFFFA31A), Color(0xFFFFC047)),
                ),
            ),
    ) {
        Text(
            text = "Listen & Enjoy",
            color = Color.White,
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.ExtraBold,
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(start = 22.dp, top = 24.dp),
        )
        Text(
            text = "Stories, sounds & songs\nfor little learners",
            color = Color.White.copy(alpha = 0.9f),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(start = 22.dp, top = 70.dp),
        )
        Text(
            text = "♪",
            color = Color.White.copy(alpha = 0.35f),
            style = MaterialTheme.typography.displayLarge,
            fontWeight = FontWeight.ExtraBold,
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .padding(end = 34.dp),
        )
    }
}

@Composable
private fun ListenCategoryHero(category: ListenCategory) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(150.dp)
            .clip(RoundedCornerShape(30.dp))
            .background(Brush.linearGradient(category.colors)),
    ) {
        Text(
            text = category.title,
            color = Color.White,
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.ExtraBold,
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(start = 22.dp, top = 26.dp),
        )
        Text(
            text = category.subtitle,
            color = Color.White.copy(alpha = 0.9f),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(start = 22.dp, top = 78.dp),
        )
        Image(
            painter = painterResource(category.image),
            contentDescription = category.title,
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .padding(end = 18.dp)
                .size(116.dp),
            contentScale = ContentScale.Fit,
        )
    }
}

@Composable
private fun ListenCategoryCard(category: ListenCategory, onClick: () -> Unit) {
    val shape = RoundedCornerShape(28.dp)
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(150.dp)
            .clip(shape)
            .clickable(onClick = onClick),
        shape = shape,
        elevation = CardDefaults.cardElevation(8.dp),
        colors = CardDefaults.cardColors(containerColor = category.colors.first()),
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Brush.linearGradient(category.colors)),
        ) {
            Column(
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .padding(start = 20.dp, end = 150.dp),
            ) {
                Text(
                    text = category.title,
                    color = Color.White,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.ExtraBold,
                )
                Text(
                    text = category.subtitle,
                    color = Color.White.copy(alpha = 0.92f),
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(top = 8.dp),
                )
            }
            Image(
                painter = painterResource(category.image),
                contentDescription = category.title,
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .padding(end = 58.dp)
                    .size(110.dp),
                contentScale = ContentScale.Fit,
            )
            Box(
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .padding(end = 16.dp)
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(Color.White),
                contentAlignment = Alignment.Center,
            ) {
                ChevronRightIcon(color = category.colors.last(), size = 24.dp)
            }
        }
    }
}

@Composable
private fun ListenFeatureCard(feature: ListenFeature, onPlay: () -> Unit) {
    val shape = RoundedCornerShape(28.dp)
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(148.dp)
            .clip(shape)
            .clickable(onClick = onPlay),
        shape = shape,
        elevation = CardDefaults.cardElevation(8.dp),
        colors = CardDefaults.cardColors(containerColor = feature.colors.first()),
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Brush.linearGradient(feature.colors)),
        ) {
            Column(
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .padding(start = 20.dp, end = 150.dp),
            ) {
                Text(
                    text = feature.title,
                    color = Color.White,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.ExtraBold,
                )
                Text(
                    text = feature.subtitle,
                    color = Color.White.copy(alpha = 0.92f),
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(top = 8.dp),
                )
            }
            Image(
                painter = painterResource(feature.image),
                contentDescription = feature.title,
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .padding(end = 42.dp)
                    .size(116.dp)
                    .clip(RoundedCornerShape(22.dp)),
                contentScale = ContentScale.Fit,
            )
            Box(
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .padding(end = 16.dp)
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(Color.White),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = "▶",
                    color = feature.colors.last(),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.ExtraBold,
                )
            }
        }
    }
}

@Composable
private fun rememberListenSpeaker(): (String) -> Unit {
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
                engine?.setPitch(1.45f)
                engine?.setSpeechRate(0.9f)
            } else {
                engine?.shutdown()
                tts = null
            }
        }
        tts = engine
        onDispose {
            disposed = true
            engine?.stop()
            engine?.shutdown()
            tts = null
        }
    }

    return { text ->
        tts?.speak(text, TextToSpeech.QUEUE_FLUSH, null, "listen-${text.hashCode()}")
    }
}
