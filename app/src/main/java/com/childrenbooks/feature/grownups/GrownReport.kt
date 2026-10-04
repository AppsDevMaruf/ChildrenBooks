package com.childrenbooks.feature.grownups

import androidx.annotation.StringRes
import com.childrenbooks.data.content.World
import com.childrenbooks.data.content.WorldId
import com.childrenbooks.data.progress.CardStat
import com.childrenbooks.data.progress.Mastery
import com.childrenbooks.data.progress.Progress

enum class ReportRange(val days: Int) { Week(7), Month(30) }

data class DayBar(val day: Long, val minutes: Int, val isToday: Boolean)

data class WorldMastery(
    val worldId: WorldId,
    @StringRes val title: Int,
    val mastered: Int,
    val practising: Int,
    val total: Int,
)

/** A card the child keeps missing, with where to find it so a grown-up can open that lesson. */
data class PracticeWord(
    val cardId: String,
    val word: String,
    val worldId: WorldId,
    val setIndex: Int,
    val cardIndex: Int,
)

data class GrownReport(
    val range: ReportRange = ReportRange.Week,
    val bars: List<DayBar> = emptyList(),
    val totalMinutes: Int = 0,
    val setsFinished: Int = 0,
    val streak: Int = 0,
    val totalStars: Int = 0,
    val stickers: Int = 0,
    val masteredWords: Int = 0,
    val worlds: List<WorldMastery> = emptyList(),
    val needsPractice: List<PracticeWord> = emptyList(),
)

/** Pure report maths, kept free of Android types so it is unit-tested. */
object ReportBuilder {
    fun build(
        progress: Progress,
        worlds: List<World>,
        today: Long,
        range: ReportRange,
        maxPractice: Int = 6,
    ): GrownReport {
        val firstDay = today - range.days + 1
        val days = firstDay..today
        val bars = days.map { day ->
            DayBar(day = day, minutes = ((progress.dailySeconds[day] ?: 0L) / 60L).toInt(), isToday = day == today)
        }
        val totalSeconds = days.sumOf { progress.dailySeconds[it] ?: 0L }

        val worldMastery = worlds.map { world ->
            val cards = world.sets.flatMap { it.cards }
            WorldMastery(
                worldId = world.id,
                title = world.title,
                mastered = cards.count { progress.mastery(it.id) == Mastery.Mastered },
                practising = cards.count { progress.mastery(it.id) == Mastery.Practising },
                total = cards.size,
            )
        }

        val needsPractice = worlds.asSequence()
            .flatMap { world -> world.sets.asSequence() }
            .flatMap { set -> set.cards.asSequence().mapIndexed { index, card -> Triple(set, index, card) } }
            .mapNotNull { (set, index, card) ->
                val stat = progress.cardStats[card.id] ?: return@mapNotNull null
                if (stat.mastery == Mastery.Mastered || stat.misses == 0) return@mapNotNull null
                stat to PracticeWord(card.id, card.word, set.worldId, set.index, index)
            }
            .distinctBy { (_, word) -> word.cardId }
            .sortedWith(
                compareByDescending<Pair<CardStat, PracticeWord>> { (stat, _) -> stat.missRate }
                    .thenByDescending { (stat, _) -> stat.lastDay },
            )
            .take(maxPractice)
            .map { (_, word) -> word }
            .toList()

        return GrownReport(
            range = range,
            bars = bars,
            totalMinutes = (totalSeconds / 60L).toInt(),
            setsFinished = progress.completions.count { it.day in days },
            streak = progress.streak(today),
            totalStars = progress.stars,
            stickers = progress.stickers.size,
            masteredWords = worldMastery.sumOf { it.mastered },
            worlds = worldMastery,
            needsPractice = needsPractice,
        )
    }

    private val CardStat.missRate: Float get() = misses.toFloat() / attempts.coerceAtLeast(1)
}
