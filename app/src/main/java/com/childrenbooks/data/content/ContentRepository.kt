package com.childrenbooks.data.content

import android.content.res.Resources
import com.childrenbooks.R
import com.childrenbooks.data.local.LearningCatalog
import com.childrenbooks.domain.model.LessonItem
import com.childrenbooks.domain.model.PictureLesson

/**
 * Turns the static [LearningCatalog] into seven worlds of short lesson sets.
 * The catalog stays the single source of content; this class only shapes it.
 */
class ContentRepository(private val resources: Resources) {

    val worlds: List<World> by lazy { buildWorlds() }

    val stickers: List<Sticker> by lazy { StickerCatalog.all }

    private val setsByKey: Map<String, LessonSet> by lazy {
        worlds.flatMap { it.sets }.associateBy { it.key }
    }

    private val cardLocations: Map<String, Pair<LessonSet, Int>> by lazy {
        buildMap {
            worlds.forEach { world ->
                world.sets.forEach { set ->
                    set.cards.forEachIndexed { index, card -> put(card.id, set to index) }
                }
            }
        }
    }

    fun world(id: WorldId): World = worlds.first { it.id == id }

    fun set(worldId: WorldId, index: Int): LessonSet? = world(worldId).sets.getOrNull(index)

    fun setByKey(key: String): LessonSet? = setsByKey[key]

    fun locateCard(cardId: String): Pair<LessonSet, Int>? = cardLocations[cardId]

    fun sticker(id: String): Sticker? = stickers.firstOrNull { it.id == id }

    private fun buildWorlds(): List<World> = listOf(
        World(WorldId.Letters, R.string.world_letters, art = null, glyph = "Aa", sets = letterSets()),
        World(WorldId.Numbers, R.string.world_numbers, art = null, glyph = "123", sets = numberSets()),
        World(
            WorldId.Time, R.string.world_time, art = R.drawable.home_days, glyph = null,
            sets = groupedSets(
                WorldId.Time,
                listOf(
                    "days" to LearningCatalog.weeks.map { it.toCard(WorldId.Time, "days") },
                    "months" to LearningCatalog.months.map { it.toCard(WorldId.Time, "months") },
                    "seasons" to LearningCatalog.seasons.map { it.toCard(WorldId.Time, "seasons") },
                ),
            ),
        ),
        World(
            WorldId.Colours, R.string.world_colours, art = R.drawable.home_rainbow, glyph = null,
            sets = groupedSets(
                WorldId.Colours,
                listOf("colours" to LearningCatalog.colours.map { it.toCard(WorldId.Colours, "colours") }),
            ),
        ),
        World(
            WorldId.Nature, R.string.world_nature, art = R.drawable.home_animals, glyph = null,
            sets = groupedSets(
                WorldId.Nature,
                listOf(
                    "animals" to (LearningCatalog.animals + LearningCatalog.fishes).map { it.toCard(WorldId.Nature, "animals") },
                    "birds" to LearningCatalog.birds.map { it.toCard(WorldId.Nature, "birds") },
                    "flowers" to LearningCatalog.flowers.map { it.toCard(WorldId.Nature, "flowers") },
                ),
            ),
        ),
        World(
            WorldId.Food, R.string.world_food, art = R.drawable.home_foods, glyph = null,
            sets = groupedSets(
                WorldId.Food,
                listOf(
                    "fruits" to LearningCatalog.fruits.map { it.toCard(WorldId.Food, "fruits") },
                    "vegetables" to LearningCatalog.vegetables.map { it.toCard(WorldId.Food, "vegetables") },
                    "foods" to LearningCatalog.foods.map { it.toCard(WorldId.Food, "foods") },
                ),
            ),
        ),
        World(
            WorldId.Things, R.string.world_things, art = R.drawable.home_shapes, glyph = null,
            sets = groupedSets(
                WorldId.Things,
                listOf(
                    "furniture" to LearningCatalog.furniture.map { it.toCard(WorldId.Things, "furniture") },
                    "shapes" to LearningCatalog.shapes.map { it.toCard(WorldId.Things, "shapes") },
                ),
            ),
        ),
    )

    private fun letterSets(): List<LessonSet> {
        val cards = LearningCatalog.alphabet.map { lesson ->
            val phrase = resources.getString(lesson.description).trim()
            LetterCard(
                id = cardId(WorldId.Letters, "letters", lesson.letter),
                upper = lesson.letter,
                lower = lesson.letter.lowercase(),
                word = phrase.substringAfter(" is for ", phrase).trim(),
                phrase = phrase,
                image = lesson.exampleImage,
                audio = AudioClip.Sequence(listOf(AudioClip.Recorded(lesson.letterSound), AudioClip.Recorded(lesson.wordSound))),
            )
        }
        return SetChunker.balancedChunks(cards).mapIndexed { index, chunk ->
            LessonSet(WorldId.Letters, index, rangeTitle(chunk.first().upper, chunk.last().upper), chunk)
        }
    }

    private fun numberSets(): List<LessonSet> {
        val cards = LearningCatalog.numbers.map { lesson ->
            CountCard(
                id = cardId(WorldId.Numbers, "numbers", lesson.number),
                numeral = lesson.number,
                word = resources.getString(lesson.word),
                image = lesson.countImage,
                audio = AudioClip.Sequence(listOf(AudioClip.Recorded(lesson.numberSound), AudioClip.Recorded(lesson.wordSound))),
            )
        }
        return SetChunker.balancedChunks(cards).mapIndexed { index, chunk ->
            LessonSet(WorldId.Numbers, index, rangeTitle(chunk.first().numeral, chunk.last().numeral), chunk)
        }
    }

    private fun groupedSets(worldId: WorldId, groups: List<Pair<String, List<Card>>>): List<LessonSet> {
        var index = 0
        return groups.flatMap { (_, cards) ->
            SetChunker.balancedChunks(cards).map { chunk ->
                LessonSet(worldId, index++, rangeTitle(chunk.first().word, chunk.last().word), chunk)
            }
        }
    }

    private fun LessonItem.toCard(worldId: WorldId, group: String): Card = PictureCard(
        id = cardId(worldId, group, title),
        word = title,
        image = image,
        audio = AudioClip.Recorded(sound),
    )

    private fun PictureLesson.toCard(worldId: WorldId, group: String): Card = PictureCard(
        id = cardId(worldId, group, title),
        word = title,
        image = image,
        audio = AudioClip.Spoken(title),
    )

    private fun rangeTitle(first: String, last: String): String =
        if (first == last) first else resources.getString(R.string.set_range, first, last)

    private fun cardId(worldId: WorldId, group: String, name: String): String =
        "${worldId.key}.$group.${name.replace(Regex("[^A-Za-z0-9]+"), "_")}"
}

/** Stickers in the order they are won. */
object StickerCatalog {
    val all: List<Sticker> = listOf(
        Sticker("lion", R.drawable.animal_lion, "Lion"),
        Sticker("panda", R.drawable.animal_panda, "Panda"),
        Sticker("monkey", R.drawable.animal_monkey, "Monkey"),
        Sticker("elephant", R.drawable.animal_elephant, "Elephant"),
        Sticker("giraffe", R.drawable.animal_giraffe, "Giraffe"),
        Sticker("penguin", R.drawable.bird_penguin, "Penguin"),
        Sticker("fox", R.drawable.animal_fox, "Fox"),
        Sticker("owl", R.drawable.bird_owl, "Owl"),
        Sticker("parrot", R.drawable.bird_parrot, "Parrot"),
        Sticker("tiger", R.drawable.animal_tiger, "Tiger"),
        Sticker("zebra", R.drawable.animal_zebra, "Zebra"),
        Sticker("rabbit", R.drawable.animal_rabbit, "Rabbit"),
        Sticker("bear", R.drawable.animal_bear, "Bear"),
        Sticker("kangaroo", R.drawable.animal_kangaroo, "Kangaroo"),
        Sticker("flamingo", R.drawable.bird_flamingo, "Flamingo"),
        Sticker("peacock", R.drawable.bird_peacock, "Peacock"),
        Sticker("deer", R.drawable.animal_deer, "Deer"),
        Sticker("horse", R.drawable.animal_horse, "Horse"),
        Sticker("cow", R.drawable.animal_cow, "Cow"),
        Sticker("duck", R.drawable.bird_duck, "Duck"),
        Sticker("cat", R.drawable.animal_cat, "Cat"),
        Sticker("dog", R.drawable.animal_dog, "Dog"),
        Sticker("toucan", R.drawable.bird_toucan, "Toucan"),
        Sticker("swan", R.drawable.bird_swan, "Swan"),
    )
}
