package com.childrenbooks.feature.practice

import com.childrenbooks.TestFixtures
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertSame
import org.junit.Assert.assertTrue
import org.junit.Test
import kotlin.random.Random

class PracticeEngineTest {
    private val cards = (1..5).map { TestFixtures.picture("c$it") }

    @Test
    fun generator_makesThreeQuestionsWithDistinctTargets() {
        val questions = QuestionGenerator.generate(cards, optionCount = 3, random = Random(7))
        assertEquals(3, questions.size)
        assertEquals(3, questions.map { it.target.id }.toSet().size)
    }

    @Test
    fun generator_optionsContainTargetOnceAndMatchAgeBand() {
        for (optionCount in listOf(3, 4)) {
            repeat(50) { seed ->
                QuestionGenerator.generate(cards, optionCount, Random(seed)).forEach { question ->
                    assertEquals(optionCount, question.options.size)
                    assertEquals(1, question.options.count { it.id == question.target.id })
                    assertEquals(optionCount, question.options.map { it.id }.toSet().size)
                }
            }
        }
    }

    @Test
    fun generator_neverPutsTheAnswerInTheSameSpotTwiceInARow() {
        repeat(200) { seed ->
            val positions = QuestionGenerator.generate(cards, 3, Random(seed)).map { q -> q.options.indexOfFirst { it.id == q.target.id } }
            positions.zipWithNext().forEach { (a, b) -> assertNotEquals("seed=$seed", a, b) }
        }
    }

    @Test
    fun generator_handlesSetsSmallerThanOptionCount() {
        val small = cards.take(2)
        val questions = QuestionGenerator.generate(small, optionCount = 4, random = Random(1))
        assertEquals(2, questions.size)
        questions.forEach { assertEquals(2, it.options.size) }
    }

    private val questions = QuestionGenerator.generate(cards, 3, Random(3))
    private fun wrongFor(index: Int) = questions[index].options.first { it.id != questions[index].target.id }.id
    private fun right(index: Int) = questions[index].target.id

    @Test
    fun firstTryAndSecondTry_earnAStar() {
        var state = PracticeReducer.reduce(PracticeState(), questions, PracticeAction.Pick(right(0)))
        assertTrue(state.solved)
        assertTrue(state.results.single().firstTry)
        state = PracticeReducer.reduce(state, questions, PracticeAction.Advance)
        state = PracticeReducer.reduce(state, questions, PracticeAction.Pick(wrongFor(1)))
        assertEquals(1, state.misses)
        state = PracticeReducer.reduce(state, questions, PracticeAction.Pick(right(1)))
        assertEquals(2, state.stars)
        assertFalse(state.results.last().firstTry)
    }

    @Test
    fun thirdMiss_revealsAnswerWithoutAStar() {
        var state = PracticeState()
        repeat(3) { state = PracticeReducer.reduce(state, questions, PracticeAction.Pick(wrongFor(0))) }
        assertTrue(state.revealed)
        assertEquals(0, state.stars)
        assertEquals(3, state.results.single().misses)
        val after = PracticeReducer.reduce(state, questions, PracticeAction.Pick(right(0)))
        assertSame("taps after the reveal are ignored", state, after)
    }

    @Test
    fun advance_waitsForAnAnswer_thenFinishesAfterLastQuestion() {
        val unanswered = PracticeState()
        assertSame(unanswered, PracticeReducer.reduce(unanswered, questions, PracticeAction.Advance))
        var state = PracticeState()
        questions.indices.forEach { index ->
            state = PracticeReducer.reduce(state, questions, PracticeAction.Pick(right(index)))
            state = PracticeReducer.reduce(state, questions, PracticeAction.Advance)
        }
        assertTrue(state.finished)
        assertEquals(3, state.stars)
        assertEquals(3, state.results.size)
    }

    @Test
    fun questionKind_followsCardType() {
        assertEquals(QuestionKind.FindPicture, questions.first().kind)
    }
}
