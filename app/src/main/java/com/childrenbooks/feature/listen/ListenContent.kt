package com.childrenbooks.feature.listen

import androidx.annotation.DrawableRes
import com.childrenbooks.R

/** One thing to listen to. Scripts are original, kid-safe text read by the narrator voice. */
data class ListenItem(
    val title: String,
    val subtitle: String,
    @param:DrawableRes val image: Int,
    val script: String,
)

enum class ListenShelf(val routeId: String, @param:DrawableRes val image: Int) {
    AnimalSounds("animal-sounds", R.drawable.animal_cow),
    KidsStories("kids-stories", R.drawable.animal_rabbit),
    Rhymes("rhymes", R.drawable.bird_parrot),
    ;

    companion object {
        fun fromRouteId(routeId: String): ListenShelf? = entries.firstOrNull { it.routeId == routeId }
    }
}

object ListenContent {
    fun itemsFor(shelf: ListenShelf): List<ListenItem> = when (shelf) {
        ListenShelf.AnimalSounds -> animalSounds
        ListenShelf.KidsStories -> kidsStories
        ListenShelf.Rhymes -> rhymes
    }

    val animalSounds: List<ListenItem> = listOf(
    ListenItem("Dog Sound", "Dog says woof woof", R.drawable.animal_dog, "Dog. Woof woof. The friendly dog says woof woof."),
    ListenItem("Cat Sound", "Cat says meow", R.drawable.animal_cat, "Cat. Meow meow. The soft little cat says meow."),
    ListenItem("Cow Sound", "Cow says moo", R.drawable.animal_cow, "Cow. Moo moo. The big cow says moo."),
    ListenItem("Lion Sound", "Lion says roar", R.drawable.animal_lion, "Lion. Roar roar. The brave lion says roar."),
    ListenItem("Monkey Sound", "Monkey says oo oo aa aa", R.drawable.animal_monkey, "Monkey. Oo oo aa aa. The funny monkey jumps and calls."),
    ListenItem("Horse Sound", "Horse says neigh", R.drawable.animal_horse, "Horse. Neigh neigh. The happy horse says neigh."),
    ListenItem("Sheep Sound", "Sheep says baa", R.drawable.animal_sheep, "Sheep. Baa baa. The fluffy sheep says baa."),
    ListenItem("Duck Sound", "Duck says quack", R.drawable.bird_duck, "Duck. Quack quack. The duck swims and says quack."),
    ListenItem("Bird Sound", "Bird says tweet", R.drawable.bird_parrot, "Bird. Tweet tweet. The little bird sings tweet tweet."),
    ListenItem("Elephant Sound", "Elephant trumpets", R.drawable.animal_elephant, "Elephant. Pawoo pawoo. The gentle elephant makes a trumpet sound."),
)

    val kidsStories: List<ListenItem> = listOf(
    ListenItem(
        title = "The Kind Bunny",
        subtitle = "Story 1 • sharing",
        image = R.drawable.animal_rabbit,
        script = "The kind bunny found one carrot. A hungry friend came by. Bunny smiled and shared the carrot. Both friends felt happy and full.",
    ),
    ListenItem(
        title = "The Brave Little Fish",
        subtitle = "Story 2 • courage",
        image = R.drawable.fish,
        script = "A little fish was scared to swim far. Then it saw a tiny bubble path. The fish followed slowly and found a beautiful coral garden.",
    ),
    ListenItem(
        title = "Mina's Magic Book",
        subtitle = "Story 3 • reading",
        image = R.drawable.home_slider_2,
        script = "Mina opened a blue book. The pictures became bright and friendly. She learned letters, numbers, colors, and a happy song.",
    ),
    ListenItem(
        title = "The Helpful Panda",
        subtitle = "Story 4 • helping",
        image = R.drawable.animal_panda,
        script = "Panda saw toys on the floor. He picked them up one by one. The room became clean, and everyone clapped for helpful Panda.",
    ),
    ListenItem(
        title = "The Rainbow Picnic",
        subtitle = "Story 5 • colors",
        image = R.drawable.rainbo,
        script = "The children packed red apples, yellow bananas, and green grapes. After rain, a rainbow appeared. They named every color together.",
    ),
)

    val rhymes: List<ListenItem> = listOf(
    ListenItem(
        title = "Starry Sky",
        subtitle = "Rhyme 1 • calm night",
        image = R.drawable.shape_star,
        script = "Starry sky, soft and bright. Little dreams glow through the night. Close your eyes and smile with me. Happy hearts are wild and free.",
    ),
    ListenItem(
        title = "Happy Apple",
        subtitle = "Rhyme 2 • fruit fun",
        image = R.drawable.fruit_apple,
        script = "Apple red, apple sweet. Crunchy fruit is good to eat. One small bite and one big grin. Healthy habits now begin.",
    ),
    ListenItem(
        title = "Little Bunny",
        subtitle = "Rhyme 3 • animal jump",
        image = R.drawable.animal_rabbit,
        script = "Little bunny hops around. Tiny feet tap on the ground. Hop hop here and hop hop there. Bunny dances everywhere.",
    ),
    ListenItem(
        title = "Rainbow Day",
        subtitle = "Rhyme 4 • colors",
        image = R.drawable.rainbo,
        script = "Red and yellow, green and blue. Rainbow colors smile at you. Point and name them one by one. Learning colors is such fun.",
    ),
    ListenItem(
        title = "Counting Steps",
        subtitle = "Rhyme 5 • numbers",
        image = R.drawable.number,
        script = "One step, two step, three and four. Tiny feet go to the door. Five steps, six steps, clap with me. Counting makes us bright and free.",
    ),
    ListenItem(
        title = "Sunny Morning",
        subtitle = "Rhyme 6 • wake up",
        image = R.drawable.days,
        script = "Good morning sun, hello new day. Wash your face and start to play. Read a book and sing along. Morning time is bright and strong.",
    ),
    ListenItem(
        title = "Tiny Fish",
        subtitle = "Rhyme 7 • water",
        image = R.drawable.fish,
        script = "Tiny fish in water blue. Swims around and waves to you. Bubble bubble, splash and glide. Happy fish goes side to side.",
    ),
    ListenItem(
        title = "Busy Bee",
        subtitle = "Rhyme 8 • nature",
        image = R.drawable.bee,
        script = "Busy bee goes buzz buzz buzz. Flying gently just because. Flower here and flower there. Sweet little bee is in the air.",
    ),
    ListenItem(
        title = "Sleepy Moon",
        subtitle = "Rhyme 9 • bedtime",
        image = R.drawable.months,
        script = "Sleepy moon is shining high. Soft white clouds are drifting by. Teddy waits beside the bed. Sweet dreams dance inside your head.",
    ),
    ListenItem(
        title = "Green Garden",
        subtitle = "Rhyme 10 • vegetables",
        image = R.drawable.vegetable_carrot,
        script = "Carrot orange, leafy green. Freshest garden ever seen. Water plants and watch them grow. Healthy foods help children glow.",
    ),
    ListenItem(
        title = "Friendly Dog",
        subtitle = "Rhyme 11 • animal sound",
        image = R.drawable.animal_dog,
        script = "Friendly dog says woof woof woof. Wagging tail and tiny hoof. Run and fetch and sit down too. Doggy loves to play with you.",
    ),
    ListenItem(
        title = "Book Time",
        subtitle = "Rhyme 12 • reading",
        image = R.drawable.home_slider_2,
        script = "Open book and turn the page. Stories sparkle on the stage. Words can fly and pictures sing. Reading is a magic thing.",
    ),
    ListenItem(
        title = "Shape Parade",
        subtitle = "Rhyme 13 • shapes",
        image = R.drawable.shape_circle,
        script = "Circle rolls and square stands still. Triangle climbs a tiny hill. Star shines bright and heart says hi. Shapes are dancing in the sky.",
    ),
    ListenItem(
        title = "Rainy Day",
        subtitle = "Rhyme 14 • weather",
        image = R.drawable.ranyday,
        script = "Raindrops tap on window glass. Little clouds are moving past. Boots go splash and umbrellas sway. We can smile on rainy day.",
    ),
    ListenItem(
        title = "Flower Smile",
        subtitle = "Rhyme 15 • flowers",
        image = R.drawable.flower_sunflower,
        script = "Flower smiles in morning light. Petals soft and colors bright. Bees come near and butterflies play. Garden friends say hello today.",
    ),
    ListenItem(
        title = "Little Train",
        subtitle = "Rhyme 16 • movement",
        image = R.drawable.play_and_learn,
        script = "Little train goes choo choo choo. Rolling past the hills so blue. Stop and wave, then start again. Learning rides the happy train.",
    ),
    ListenItem(
        title = "Clean Hands",
        subtitle = "Rhyme 17 • habit",
        image = R.drawable.flower_water_lily,
        script = "Wash your hands with bubbly foam. Clean and fresh before you roam. Rub and rinse and dry them too. Healthy hands are good for you.",
    ),
    ListenItem(
        title = "Kind Friends",
        subtitle = "Rhyme 18 • manners",
        image = R.drawable.animal_panda,
        script = "Kind words make a sunny day. Please and thank you light the way. Share a toy and help a friend. Happy hearts will never end.",
    ),
    ListenItem(
        title = "Bird Song",
        subtitle = "Rhyme 19 • birds",
        image = R.drawable.bird_parrot,
        script = "Little bird sings tweet tweet tweet. Perched upon a branch so neat. Flap your wings and fly up high. Music floats across the sky.",
    ),
    ListenItem(
        title = "Good Night",
        subtitle = "Rhyme 20 • sleep",
        image = R.drawable.shape_heart,
        script = "Good night toys and good night light. Good night moon so soft and bright. Close your eyes and rest your head. Dreams are waiting near your bed.",
    ),
)
}
