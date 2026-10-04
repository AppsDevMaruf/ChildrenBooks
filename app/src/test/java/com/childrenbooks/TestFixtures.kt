package com.childrenbooks

import com.childrenbooks.data.content.AudioClip
import com.childrenbooks.data.content.Card
import com.childrenbooks.data.content.LessonSet
import com.childrenbooks.data.content.PictureCard
import com.childrenbooks.data.content.World
import com.childrenbooks.data.content.WorldId
import com.childrenbooks.data.content.SetChunker

/** Small, Android-free content for unit tests. Image and title ids are placeholders. */
object TestFixtures {
    fun picture(id: String, word: String = id): Card = PictureCard(id, word, image = 1, audio = AudioClip.Spoken(word))

    fun world(id: WorldId, cardCount: Int, title: Int = 0): World {
        val cards = (1..cardCount).map { picture("${id.key}.c$it", "word$it") }
        val sets = SetChunker.balancedChunks(cards).mapIndexed { index, chunk ->
            LessonSet(id, index, "Set ${index + 1}", chunk)
        }
        return World(id, title, art = null, glyph = null, sets = sets)
    }
}
