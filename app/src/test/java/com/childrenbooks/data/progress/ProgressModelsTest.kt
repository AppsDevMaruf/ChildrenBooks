package com.childrenbooks.data.progress

import org.junit.Assert.assertEquals
import org.junit.Test

class ProgressModelsTest {
    @Test
    fun cardIsMastered_afterFirstTryOnTwoDifferentDays() {
        var stat = CardStat()
        assertEquals(Mastery.New, stat.mastery)
        stat = stat.record(firstTry = true, missCount = 0, day = 10)
        assertEquals(Mastery.Practising, stat.mastery)
        stat = stat.record(firstTry = true, missCount = 0, day = 10)
        assertEquals("same day must not count twice", Mastery.Practising, stat.mastery)
        stat = stat.record(firstTry = true, missCount = 0, day = 11)
        assertEquals(Mastery.Mastered, stat.mastery)
        assertEquals(3, stat.attempts)
    }

    @Test
    fun missesAccumulate_andDoNotCountAsFirstTry() {
        val stat = CardStat().record(firstTry = false, missCount = 2, day = 5).record(firstTry = false, missCount = 1, day = 6)
        assertEquals(3, stat.misses)
        assertEquals(0, stat.firstTryDays)
        assertEquals(Mastery.Practising, stat.mastery)
    }

    @Test
    fun streak_countsActiveDaysEndingTodayOrYesterday() {
        val active = mapOf(8L to 120L, 9L to 300L, 10L to 61L)
        assertEquals(3, Progress(dailySeconds = active).streak(today = 10))
        assertEquals("today not played yet keeps yesterday's streak", 3, Progress(dailySeconds = active).streak(today = 11))
        assertEquals(0, Progress(dailySeconds = active).streak(today = 12))
    }

    @Test
    fun streak_ignoresDaysUnderOneMinute() {
        val active = mapOf(9L to 30L, 10L to 600L)
        assertEquals(1, Progress(dailySeconds = active).streak(today = 10))
    }
}
