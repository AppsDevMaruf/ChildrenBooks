package com.childrenbooks.data.progress

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.MutablePreferences
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import java.io.IOException

/**
 * Everything the app remembers, kept only on this device (nothing is uploaded).
 * Progress drives Continue, set stars, stickers and the grown-ups report.
 */
class ProgressRepository(
    private val dataStore: DataStore<Preferences>,
    private val clock: () -> Long = System::currentTimeMillis,
) {
    private val safeData: Flow<Preferences> = dataStore.data.catch { error ->
        if (error is IOException) emit(emptyPreferences()) else throw error
    }

    val progress: Flow<Progress> = safeData.map { it.toProgress() }.distinctUntilChanged()

    val settings: Flow<AppSettings> = safeData.map { it.toSettings() }.distinctUntilChanged()

    fun today(): Long = epochDay(clock())

    suspend fun savePosition(setKey: String, cardIndex: Int) {
        dataStore.edit { prefs ->
            prefs[Keys.LastSet] = setKey
            prefs[Keys.LastCard] = cardIndex
        }
    }

    suspend fun recordAnswer(cardId: String, firstTry: Boolean, misses: Int) {
        val day = today()
        dataStore.edit { prefs ->
            val stats = ProgressCodec.decodeCardStats(prefs[Keys.CardStats]).toMutableMap()
            stats[cardId] = (stats[cardId] ?: CardStat()).record(firstTry, misses, day)
            prefs[Keys.CardStats] = ProgressCodec.encodeCardStats(stats)
        }
    }

    /**
     * Records a finished set. Stars always add up; the first completion of a set also
     * wins the next sticker from [stickerPool]. Effort counts: zero stars still finishes the set.
     */
    suspend fun completeSet(setKey: String, stars: Int, stickerPool: List<String>): CompletionResult {
        val day = today()
        var result = CompletionResult(starsEarned = stars, totalStars = 0, stickerId = null)
        dataStore.edit { prefs ->
            val setStars = ProgressCodec.decodeIntMap(prefs[Keys.SetStars]).toMutableMap()
            val firstTime = !setStars.containsKey(setKey)
            setStars[setKey] = maxOf(setStars[setKey] ?: 0, stars)
            prefs[Keys.SetStars] = ProgressCodec.encodeIntMap(setStars)

            val total = (prefs[Keys.Stars] ?: 0) + stars
            prefs[Keys.Stars] = total

            val stickers = ProgressCodec.decodeList(prefs[Keys.Stickers])
            val newSticker = if (firstTime) stickerPool.firstOrNull { it !in stickers } else null
            if (newSticker != null) prefs[Keys.Stickers] = ProgressCodec.encodeList(stickers + newSticker)

            val completions = ProgressCodec.decodeCompletions(prefs[Keys.Completions]) + Completion(day, setKey)
            prefs[Keys.Completions] = ProgressCodec.encodeCompletions(completions.takeLast(MaxCompletions))

            result = CompletionResult(starsEarned = stars, totalStars = total, stickerId = newSticker)
        }
        return result
    }

    suspend fun addActiveSeconds(seconds: Long) {
        if (seconds <= 0) return
        val day = today()
        dataStore.edit { prefs ->
            val daily = ProgressCodec.decodeLongMap(prefs[Keys.DailySeconds]).toMutableMap()
            daily[day] = (daily[day] ?: 0L) + seconds.coerceAtMost(MaxSessionSeconds)
            val recent = daily.filterKeys { it > day - KeptDays }
            prefs[Keys.DailySeconds] = ProgressCodec.encodeLongMap(recent)
        }
    }

    suspend fun updateSettings(transform: (AppSettings) -> AppSettings) {
        dataStore.edit { prefs -> prefs.writeSettings(transform(prefs.toSettings())) }
    }

    suspend fun resetProgress() {
        dataStore.edit { prefs ->
            listOf(Keys.SetStars, Keys.CardStats, Keys.LastSet, Keys.Stickers, Keys.DailySeconds, Keys.Completions)
                .forEach { prefs.remove(it) }
            prefs.remove(Keys.Stars)
            prefs.remove(Keys.LastCard)
            prefs.remove(Keys.StickersSeen)
        }
    }

    private fun Preferences.toProgress() = Progress(
        stars = this[Keys.Stars] ?: 0,
        setStars = ProgressCodec.decodeIntMap(this[Keys.SetStars]),
        cardStats = ProgressCodec.decodeCardStats(this[Keys.CardStats]),
        lastSetKey = this[Keys.LastSet],
        lastCardIndex = this[Keys.LastCard] ?: 0,
        stickers = ProgressCodec.decodeList(this[Keys.Stickers]),
        dailySeconds = ProgressCodec.decodeLongMap(this[Keys.DailySeconds]),
        completions = ProgressCodec.decodeCompletions(this[Keys.Completions]),
    )

    private fun Preferences.toSettings() = AppSettings(
        onboardingDone = this[Keys.OnboardingDone] ?: false,
        ageBand = this[Keys.AgeBandKey]?.let { value -> AgeBand.entries.firstOrNull { it.name == value } } ?: AgeBand.Young,
        childName = this[Keys.ChildName].orEmpty(),
        showWords = this[Keys.ShowWords] ?: true,
        bedtime = this[Keys.Bedtime] ?: false,
        stickersSeen = this[Keys.StickersSeen] ?: 0,
    )

    private fun MutablePreferences.writeSettings(settings: AppSettings) {
        this[Keys.OnboardingDone] = settings.onboardingDone
        this[Keys.AgeBandKey] = settings.ageBand.name
        this[Keys.ChildName] = settings.childName.take(MaxNameLength)
        this[Keys.ShowWords] = settings.showWords
        this[Keys.Bedtime] = settings.bedtime
        this[Keys.StickersSeen] = settings.stickersSeen
    }

    private object Keys {
        val Stars = intPreferencesKey("stars")
        val SetStars = stringPreferencesKey("set_stars")
        val CardStats = stringPreferencesKey("card_stats")
        val LastSet = stringPreferencesKey("last_set")
        val LastCard = intPreferencesKey("last_card")
        val Stickers = stringPreferencesKey("stickers")
        val DailySeconds = stringPreferencesKey("daily_seconds")
        val Completions = stringPreferencesKey("completions")
        val OnboardingDone = booleanPreferencesKey("onboarding_done")
        val AgeBandKey = stringPreferencesKey("age_band")
        val ChildName = stringPreferencesKey("child_name")
        val ShowWords = booleanPreferencesKey("show_words")
        val Bedtime = booleanPreferencesKey("bedtime")
        val StickersSeen = intPreferencesKey("stickers_seen")
    }

    private companion object {
        const val MaxCompletions = 400
        const val MaxSessionSeconds = 60L * 60L
        const val KeptDays = 120L
        const val MaxNameLength = 24
    }
}
