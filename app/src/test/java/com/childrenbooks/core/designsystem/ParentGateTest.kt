package com.childrenbooks.core.designsystem

import com.childrenbooks.core.designsystem.components.NumberWords
import com.childrenbooks.core.designsystem.components.ParentGateQuestion
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import kotlin.random.Random

class ParentGateTest {
    @Test
    fun numberWords_coverTeensTensAndCompounds() {
        assertEquals("zero", NumberWords.toWords(0))
        assertEquals("thirteen", NumberWords.toWords(13))
        assertEquals("forty", NumberWords.toWords(40))
        assertEquals("forty-seven", NumberWords.toWords(47))
        assertEquals("ninety-nine", NumberWords.toWords(99))
    }

    @Test(expected = IllegalArgumentException::class)
    fun numberWords_rejectOutOfRange() {
        NumberWords.toWords(100)
    }

    @Test
    fun question_isATwoDigitSumWithThreeDistinctOptions() {
        repeat(500) { seed ->
            val question = ParentGateQuestion.generate(Random(seed))
            assertTrue(question.first in 21..69)
            assertTrue(question.second in 11..29)
            assertTrue("answer must be spellable", question.answer in 32..98)
            assertEquals(3, question.options.toSet().size)
            assertTrue(question.answer in question.options)
        }
    }
}
