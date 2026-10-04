package com.childrenbooks.data.content

/**
 * Splits content into short, evenly sized sets so no lesson becomes an endless deck.
 * 26 letters become 5, 5, 4, 4, 4, 4; 7 days become 4 and 3; 20 pictures become 4 sets of 5.
 */
object SetChunker {
    const val TargetSetSize = 5

    fun <T> balancedChunks(items: List<T>, target: Int = TargetSetSize): List<List<T>> {
        require(target > 0) { "Target set size must be positive" }
        if (items.isEmpty()) return emptyList()
        val setCount = (items.size + target - 1) / target
        val baseSize = items.size / setCount
        val larger = items.size % setCount
        val result = ArrayList<List<T>>(setCount)
        var start = 0
        repeat(setCount) { index ->
            val size = baseSize + if (index < larger) 1 else 0
            result += items.subList(start, start + size)
            start += size
        }
        return result
    }
}
