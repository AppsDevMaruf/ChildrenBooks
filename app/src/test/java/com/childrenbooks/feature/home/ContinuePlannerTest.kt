package com.childrenbooks.feature.home

import com.childrenbooks.TestFixtures
import com.childrenbooks.data.content.WorldId
import com.childrenbooks.data.progress.Progress
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ContinuePlannerTest {
    private val letters = TestFixtures.world(WorldId.Letters, cardCount = 10)
    private val numbers = TestFixtures.world(WorldId.Numbers, cardCount = 10)
    private val worlds = listOf(letters, numbers)
    private val sets = worlds.flatMap { it.sets }.associateBy { it.key }

    private fun plan(progress: Progress) = ContinuePlanner.next(progress, worlds, sets::get)!!

    @Test
    fun newChild_startsAtFirstSetOfFirstWorld() {
        val target = plan(Progress())
        assertEquals(letters.sets[0].key, target.set.key)
        assertEquals(0, target.cardIndex)
        assertFalse(target.resume)
    }

    @Test
    fun unfinishedLastSet_resumesAtSavedCard() {
        val target = plan(Progress(lastSetKey = numbers.sets[1].key, lastCardIndex = 3))
        assertEquals(numbers.sets[1].key, target.set.key)
        assertEquals(3, target.cardIndex)
        assertTrue(target.resume)
    }

    @Test
    fun finishedLastSet_movesToNextSetInSameWorld() {
        val done = numbers.sets[0].key
        val target = plan(Progress(lastSetKey = done, setStars = mapOf(done to 3)))
        assertEquals(numbers.sets[1].key, target.set.key)
    }

    @Test
    fun finishedWorld_movesToNextWorldWithWork() {
        val allLetters = letters.sets.associate { it.key to 3 }
        val target = plan(Progress(lastSetKey = letters.sets.last().key, setStars = allLetters))
        assertEquals(numbers.sets[0].key, target.set.key)
    }

    @Test
    fun savedCardIndexOutOfRange_isClamped() {
        val target = plan(Progress(lastSetKey = letters.sets[0].key, lastCardIndex = 99))
        assertEquals(letters.sets[0].cards.lastIndex, target.cardIndex)
    }
}
