package com.childrenbooks.core.audio

import android.content.Context
import android.content.res.Resources
import androidx.annotation.RawRes
import com.childrenbooks.R
import com.childrenbooks.data.local.LearningCatalog
import java.util.zip.CRC32

/**
 * Finds recorded baby-voice clips for lines the app would otherwise read with text-to-speech.
 *
 * Each clip lives in res/raw as "bv_" + the CRC32 of its exact text in 8 hex digits
 * (see tools/baby_voice/manifest.py, which must use the same rule).
 * Lines that contain a word, like "Find the Apple", play the clip for the words before it and then
 * the word's own clip. A line with no clip returns null, and the narrator falls back to text-to-speech.
 */
class BabyVoice(context: Context) {
    private val appContext = context.applicationContext
    private val resources: Resources = appContext.resources
    private val cache = HashMap<String, Int>()

    private val wordTemplates = listOf(R.string.prompt_find, R.string.feedback_hint, R.string.feedback_reveal, R.string.prompt_starts_with)
        .map { resources.getString(it) }

    /** "Great job, %1$s!" with the child's name: the name can't be recorded, so play "Great job!". */
    private val namedPraise = resources.getString(R.string.reward_great_job_name)
    private val praise = resources.getString(R.string.reward_great_job)

    /** The clips to play for [text], in order, or null when any part has no recording. */
    fun clipsFor(text: String): List<Int>? {
        val line = text.trim()
        clipId(line)?.let { return listOf(it) }
        for (template in wordTemplates) {
            val word = fill(template, line) ?: continue
            val prefix = clipId(template.substringBefore("%1\$s").trim()) ?: continue
            val wordClip = clipId(word) ?: continue
            return listOf(prefix, wordClip)
        }
        if (fill(namedPraise, line) != null) clipId(praise)?.let { return listOf(it) }
        return null
    }

    /** The value that turns [template] (one "%1$s") into [line], or null when it doesn't match. */
    private fun fill(template: String, line: String): String? {
        val before = template.substringBefore("%1\$s", missingDelimiterValue = "")
        val after = template.substringAfter("%1\$s", missingDelimiterValue = "")
        if (before.isEmpty() && after.isEmpty()) return null
        if (!line.startsWith(before) || !line.endsWith(after) || line.length <= before.length + after.length) return null
        return line.substring(before.length, line.length - after.length).trim().ifEmpty { null }
    }

    /**
     * Recordings the app already had: days, months, seasons and colours in the baby voice, and the
     * number sounds, so "This is the three" ends with the Numbers cards' "three".
     */
    private val existing: Map<String, Int> =
        (LearningCatalog.weeks + LearningCatalog.months + LearningCatalog.seasons + LearningCatalog.colours)
            .associate { it.title to it.sound } +
            LearningCatalog.numbers.associate { resources.getString(it.word) to it.numberSound }

    @RawRes
    private fun clipId(text: String): Int? {
        existing[text]?.let { return it }
        val name = fileName(text)
        val id = cache.getOrPut(name) { resources.getIdentifier(name, "raw", appContext.packageName) }
        return id.takeIf { it != 0 }
    }

    companion object {
        /** True when res/raw has a baby-voice clip for exactly [text]. */
        fun hasClip(resources: Resources, text: String): Boolean =
            resources.getIdentifier(fileName(text), "raw", resources.getResourcePackageName(R.raw.keep_baby_voice)) != 0

        fun fileName(text: String): String {
            val crc = CRC32().apply { update(text.toByteArray(Charsets.UTF_8)) }.value
            return "bv_%08x".format(crc)
        }
    }
}
