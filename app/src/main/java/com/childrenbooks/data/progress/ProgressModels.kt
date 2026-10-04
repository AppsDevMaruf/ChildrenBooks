package com.childrenbooks.data.progress

/** Age band chosen at first run; changes practice difficulty, not layout. */
enum class AgeBand { Young, Older }

data class AppSettings(
    val onboardingDone: Boolean = false,
    val ageBand: AgeBand = AgeBand.Young,
    val childName: String = "",
    val showWords: Boolean = true,
    val bedtime: Boolean = false,
    val stickersSeen: Int = 0,
)

enum class Mastery { New, Practising, Mastered }

/**
 * Per-card practice history. A card is mastered after a first-try correct answer
 * on two separate days.
 */
data class CardStat(
    val firstTryDays: Int = 0,
    val attempts: Int = 0,
    val misses: Int = 0,
    val lastDay: Long = 0,
    val lastFirstTryDay: Long = -1,
) {
    val mastery: Mastery
        get() = when {
            firstTryDays >= MasteryDays -> Mastery.Mastered
            attempts > 0 -> Mastery.Practising
            else -> Mastery.New
        }

    fun record(firstTry: Boolean, missCount: Int, day: Long): CardStat {
        val newFirstTryDay = firstTry && day != lastFirstTryDay
        return copy(
            firstTryDays = firstTryDays + if (newFirstTryDay) 1 else 0,
            attempts = attempts + 1,
            misses = misses + missCount,
            lastDay = day,
            lastFirstTryDay = if (firstTry) day else lastFirstTryDay,
        )
    }

    companion object {
        const val MasteryDays = 2
    }
}

data class Completion(val day: Long, val setKey: String)

data class Progress(
    val stars: Int = 0,
    val setStars: Map<String, Int> = emptyMap(),
    val cardStats: Map<String, CardStat> = emptyMap(),
    val lastSetKey: String? = null,
    val lastCardIndex: Int = 0,
    val stickers: List<String> = emptyList(),
    val dailySeconds: Map<Long, Long> = emptyMap(),
    val completions: List<Completion> = emptyList(),
) {
    fun isSetDone(key: String): Boolean = setStars.containsKey(key)

    fun mastery(cardId: String): Mastery = cardStats[cardId]?.mastery ?: Mastery.New

    /** Days in a row with at least one active minute, ending today or yesterday. */
    fun streak(today: Long): Int {
        val activeDays = dailySeconds.filterValues { it >= 60 }.keys
        var day = if (today in activeDays) today else today - 1
        var count = 0
        while (day in activeDays) {
            count++
            day--
        }
        return count
    }
}

data class CompletionResult(
    val starsEarned: Int,
    val totalStars: Int,
    val stickerId: String?,
)
