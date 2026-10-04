package com.childrenbooks.data.progress

/**
 * Compact text encoding for progress stored in Preferences DataStore.
 * Keys never contain the separators: card ids and set keys use only [A-Za-z0-9_.:].
 */
object ProgressCodec {
    private const val EntrySeparator = ';'
    private const val KeyValueSeparator = '='
    private const val FieldSeparator = ','
    private const val CompletionSeparator = '@'

    fun encodeIntMap(map: Map<String, Int>): String =
        map.entries.joinToString(EntrySeparator.toString()) { "${it.key}$KeyValueSeparator${it.value}" }

    fun decodeIntMap(text: String?): Map<String, Int> = entries(text).mapNotNull { entry ->
        val key = entry.substringBefore(KeyValueSeparator, "")
        val value = entry.substringAfter(KeyValueSeparator, "").toIntOrNull()
        if (key.isEmpty() || value == null) null else key to value
    }.toMap()

    fun encodeLongMap(map: Map<Long, Long>): String =
        map.entries.joinToString(EntrySeparator.toString()) { "${it.key}$KeyValueSeparator${it.value}" }

    fun decodeLongMap(text: String?): Map<Long, Long> = entries(text).mapNotNull { entry ->
        val key = entry.substringBefore(KeyValueSeparator, "").toLongOrNull()
        val value = entry.substringAfter(KeyValueSeparator, "").toLongOrNull()
        if (key == null || value == null) null else key to value
    }.toMap()

    fun encodeCardStats(map: Map<String, CardStat>): String =
        map.entries.joinToString(EntrySeparator.toString()) { (id, stat) ->
            listOf(stat.firstTryDays, stat.attempts, stat.misses, stat.lastDay, stat.lastFirstTryDay)
                .joinToString(FieldSeparator.toString(), prefix = "$id$KeyValueSeparator")
        }

    fun decodeCardStats(text: String?): Map<String, CardStat> = entries(text).mapNotNull { entry ->
        val id = entry.substringBefore(KeyValueSeparator, "")
        val fields = entry.substringAfter(KeyValueSeparator, "").split(FieldSeparator).map { it.toLongOrNull() }
        if (id.isEmpty() || fields.size != 5 || fields.any { it == null }) {
            null
        } else {
            id to CardStat(
                firstTryDays = fields[0]!!.toInt(),
                attempts = fields[1]!!.toInt(),
                misses = fields[2]!!.toInt(),
                lastDay = fields[3]!!,
                lastFirstTryDay = fields[4]!!,
            )
        }
    }.toMap()

    fun encodeList(list: List<String>): String = list.joinToString(EntrySeparator.toString())

    fun decodeList(text: String?): List<String> = entries(text)

    fun encodeCompletions(list: List<Completion>): String =
        list.joinToString(EntrySeparator.toString()) { "${it.day}$CompletionSeparator${it.setKey}" }

    fun decodeCompletions(text: String?): List<Completion> = entries(text).mapNotNull { entry ->
        val day = entry.substringBefore(CompletionSeparator, "").toLongOrNull()
        val key = entry.substringAfter(CompletionSeparator, "")
        if (day == null || key.isEmpty()) null else Completion(day, key)
    }

    private fun entries(text: String?): List<String> =
        text.orEmpty().split(EntrySeparator).filter { it.isNotBlank() }
}

/** Local calendar day number (days since 1970-01-01 in the device time zone), API 23 safe. */
fun epochDay(millis: Long, offsetMillis: Long = java.util.TimeZone.getDefault().getOffset(millis).toLong()): Long =
    (millis + offsetMillis).floorDiv(86_400_000L)
