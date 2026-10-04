package com.childrenbooks.feature.grownups

import com.childrenbooks.TestFixtures
import com.childrenbooks.data.content.WorldId
import com.childrenbooks.data.progress.CardStat
import com.childrenbooks.data.progress.Completion
import com.childrenbooks.data.progress.Progress
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ReportBuilderTest {
    private val letters = TestFixtures.world(WorldId.Letters, cardCount = 10)
    private val food = TestFixtures.world(WorldId.Food, cardCount = 6)
    private val worlds = listOf(letters, food)
    private val today = 100L

    @Test
    fun week_hasSevenBarsEndingToday_andSumsOnlyThatRange() {
        val progress = Progress(dailySeconds = mapOf(94L to 600L, 100L to 125L, 93L to 9_999L))
        val report = ReportBuilder.build(progress, worlds, today, ReportRange.Week)
        assertEquals(7, report.bars.size)
        assertEquals(94L, report.bars.first().day)
        assertTrue(report.bars.last().isToday)
        assertEquals(10, report.bars.first().minutes)
        assertEquals(2, report.bars.last().minutes)
        assertEquals("day 93 is outside the week", (600 + 125) / 60, report.totalMinutes)
    }

    @Test
    fun month_hasThirtyBars_andCountsSetsInRange() {
        val progress = Progress(completions = listOf(Completion(70L, "letters:0"), Completion(71L, "letters:1"), Completion(100L, "food:0")))
        val month = ReportBuilder.build(progress, worlds, today, ReportRange.Month)
        assertEquals(30, month.bars.size)
        assertEquals(2, month.setsFinished)
        assertEquals(1, ReportBuilder.build(progress, worlds, today, ReportRange.Week).setsFinished)
    }

    @Test
    fun masteryIsCountedPerWorld() {
        val mastered = CardStat(firstTryDays = 2, attempts = 2)
        val practising = CardStat(attempts = 1, misses = 1)
        val progress = Progress(
            cardStats = mapOf(
                letters.sets[0].cards[0].id to mastered,
                letters.sets[0].cards[1].id to mastered,
                food.sets[0].cards[0].id to practising,
            ),
        )
        val report = ReportBuilder.build(progress, worlds, today, ReportRange.Week)
        val lettersRow = report.worlds.first { it.worldId == WorldId.Letters }
        assertEquals(2, lettersRow.mastered)
        assertEquals(10, lettersRow.total)
        assertEquals(1, report.worlds.first { it.worldId == WorldId.Food }.practising)
        assertEquals(2, report.masteredWords)
    }

    @Test
    fun needsPractice_listsMissedUnmasteredCards_worstFirst_withLocation() {
        val worst = food.sets[1].cards[0]
        val mild = letters.sets[0].cards[2]
        val progress = Progress(
            cardStats = mapOf(
                mild.id to CardStat(attempts = 4, misses = 1, lastDay = 99),
                worst.id to CardStat(attempts = 2, misses = 4, lastDay = 98),
                letters.sets[0].cards[0].id to CardStat(firstTryDays = 2, attempts = 5, misses = 3),
                letters.sets[1].cards[0].id to CardStat(attempts = 3, misses = 0),
            ),
        )
        val words = ReportBuilder.build(progress, worlds, today, ReportRange.Week).needsPractice
        assertEquals(listOf(worst.id, mild.id), words.map { it.cardId })
        assertEquals(WorldId.Food, words.first().worldId)
        assertEquals(1, words.first().setIndex)
        assertEquals(0, words.first().cardIndex)
    }

    @Test
    fun needsPractice_isCapped() {
        val stats = letters.sets.flatMap { it.cards }.associate { it.id to CardStat(attempts = 1, misses = 1) }
        val report = ReportBuilder.build(Progress(cardStats = stats), worlds, today, ReportRange.Week, maxPractice = 4)
        assertEquals(4, report.needsPractice.size)
    }
}
