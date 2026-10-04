package com.childrenbooks.data.content

import androidx.annotation.DrawableRes
import androidx.annotation.RawRes
import androidx.annotation.StringRes

/** What the narrator plays for a card or prompt. */
sealed interface AudioClip {
    /** A recorded clip from res/raw. */
    data class Recorded(@param:RawRes val resId: Int) : AudioClip

    /** Text read by the device's text-to-speech voice. */
    data class Spoken(val text: String) : AudioClip

    /** Clips played one after another, e.g. the letter sound, then "A is for Apple". */
    data class Sequence(val clips: List<AudioClip>) : AudioClip
}

enum class WorldId(val key: String) {
    Letters("letters"),
    Numbers("numbers"),
    Time("time"),
    Colours("colours"),
    Nature("nature"),
    Food("food"),
    Things("things"),
    ;

    companion object {
        fun fromKey(key: String): WorldId? = entries.firstOrNull { it.key == key }
    }
}

/** One thing to learn. Each card type has its own renderer in the Lesson Player. */
sealed interface Card {
    val id: String
    val word: String

    @get:DrawableRes
    val image: Int
    val audio: AudioClip
}

data class LetterCard(
    override val id: String,
    val upper: String,
    val lower: String,
    override val word: String,
    val phrase: String,
    @param:DrawableRes override val image: Int,
    override val audio: AudioClip,
) : Card

data class CountCard(
    override val id: String,
    val numeral: String,
    override val word: String,
    @param:DrawableRes override val image: Int,
    override val audio: AudioClip,
) : Card

data class PictureCard(
    override val id: String,
    override val word: String,
    @param:DrawableRes override val image: Int,
    override val audio: AudioClip,
) : Card

/** A short visit: about five cards, then practice, then a reward. */
data class LessonSet(
    val worldId: WorldId,
    val index: Int,
    val title: String,
    val cards: List<Card>,
) {
    val key: String get() = setKey(worldId, index)

    @get:DrawableRes
    val cover: Int get() = cards.first().image
}

fun setKey(worldId: WorldId, index: Int): String = "${worldId.key}:$index"

data class World(
    val id: WorldId,
    @param:StringRes val title: Int,
    @param:DrawableRes val art: Int?,
    val glyph: String?,
    val sets: List<LessonSet>,
) {
    val cardCount: Int get() = sets.sumOf { it.cards.size }
}

/** A sticker the child can win by finishing a set for the first time. */
data class Sticker(
    val id: String,
    @param:DrawableRes val image: Int,
    val name: String,
)
