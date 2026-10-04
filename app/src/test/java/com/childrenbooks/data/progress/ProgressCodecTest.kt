package com.childrenbooks.data.progress

import org.junit.Assert.assertEquals
import org.junit.Test

class ProgressCodecTest {
    @Test
    fun intMap_roundTrips() {
        val map = mapOf("letters:0" to 3, "numbers:4" to 1)
        assertEquals(map, ProgressCodec.decodeIntMap(ProgressCodec.encodeIntMap(map)))
    }

    @Test
    fun longMap_roundTrips() {
        val map = mapOf(20_000L to 600L, 20_001L to 45L)
        assertEquals(map, ProgressCodec.decodeLongMap(ProgressCodec.encodeLongMap(map)))
    }

    @Test
    fun cardStats_roundTrip() {
        val map = mapOf(
            "letters.letters.a" to CardStat(firstTryDays = 2, attempts = 5, misses = 3, lastDay = 20_010L, lastFirstTryDay = 20_009L),
            "food.fruits.apple" to CardStat(attempts = 1, misses = 2, lastDay = 20_011L),
        )
        assertEquals(map, ProgressCodec.decodeCardStats(ProgressCodec.encodeCardStats(map)))
    }

    @Test
    fun listsAndCompletions_roundTrip() {
        val stickers = listOf("lion", "panda", "owl")
        assertEquals(stickers, ProgressCodec.decodeList(ProgressCodec.encodeList(stickers)))
        val completions = listOf(Completion(20_000L, "letters:0"), Completion(20_002L, "time:1"))
        assertEquals(completions, ProgressCodec.decodeCompletions(ProgressCodec.encodeCompletions(completions)))
    }

    @Test
    fun missingOrCorruptText_decodesToEmpty() {
        assertEquals(emptyMap<String, Int>(), ProgressCodec.decodeIntMap(null))
        assertEquals(emptyMap<String, Int>(), ProgressCodec.decodeIntMap(""))
        assertEquals(emptyMap<String, Int>(), ProgressCodec.decodeIntMap("garbage"))
        assertEquals(emptyList<String>(), ProgressCodec.decodeList(null))
    }

    @Test
    fun epochDay_countsWholeLocalDays() {
        val day = 86_400_000L
        assertEquals(0L, epochDay(0L, offsetMillis = 0L))
        assertEquals(1L, epochDay(day, offsetMillis = 0L))
        assertEquals(0L, epochDay(day - 1, offsetMillis = 0L))
        // 23:30 UTC on day 0 is already day 1 in UTC+6 (Dhaka).
        assertEquals(1L, epochDay(day - 30 * 60_000L, offsetMillis = 6 * 3_600_000L))
        // Before 1970 still floors, it does not round towards zero.
        assertEquals(-1L, epochDay(-1L, offsetMillis = 0L))
    }
}
