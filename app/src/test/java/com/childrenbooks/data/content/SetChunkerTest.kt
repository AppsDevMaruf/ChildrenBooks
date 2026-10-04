package com.childrenbooks.data.content

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class SetChunkerTest {
    private fun sizes(count: Int, target: Int = 5) = SetChunker.balancedChunks((1..count).toList(), target).map { it.size }

    @Test
    fun letters_splitIntoBalancedSets() {
        assertEquals(listOf(5, 5, 4, 4, 4, 4), sizes(26))
    }

    @Test
    fun numbersAndDays_splitIntoBalancedSets() {
        assertEquals(listOf(5, 4, 4, 4, 4), sizes(21))
        assertEquals(listOf(4, 3), sizes(7))
        assertEquals(listOf(5, 5, 5, 5), sizes(20))
    }

    @Test
    fun keepsOrderAndEveryItem() {
        val items = (1..13).toList()
        assertEquals(items, SetChunker.balancedChunks(items).flatten())
    }

    @Test
    fun noSetIsLargerThanTargetOrDiffersByMoreThanOne() {
        for (count in 1..60) {
            val sizes = sizes(count)
            assertTrue("count=$count sizes=$sizes", sizes.all { it in 1..5 })
            assertTrue("count=$count sizes=$sizes", sizes.max() - sizes.min() <= 1)
        }
    }

    @Test
    fun emptyInput_givesNoSets() {
        assertEquals(emptyList<List<Int>>(), SetChunker.balancedChunks(emptyList<Int>()))
    }
}
