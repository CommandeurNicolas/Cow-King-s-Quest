package com.nicolascommandeur.cowkingsquest.model.hunt

data class Hunt(
    val relic: Relic,
    val name: String,
    val count: Int = 0,
    val previousCount: Int? = null,
    val obtainedRelics: Set<Relic> = emptySet()
) {
    val firstRelicObtained: Boolean get() = obtainedRelics.isNotEmpty()
    val pastFirstDrop: Boolean get() = firstRelicObtained || count >= COW_GOAL

    // Before the first drop, bulk taps stop before the separately confirmed kill.
    fun add(amount: Int, continueAfterGoal: Boolean = false): Hunt {
        val limit = if (continueAfterGoal && pastFirstDrop) Int.MAX_VALUE else COW_GOAL - 1
        return if (count >= limit || amount <= 0) this
        else setCount(
            (count.toLong() + amount).coerceAtMost(limit.toLong()).toInt(),
            continueAfterGoal
        )
    }

    fun recordFinalKill(): Hunt = if (count == COW_GOAL - 1) setCount(COW_GOAL) else this

    fun setCount(value: Int, continueAfterGoal: Boolean = false): Hunt {
        val next = value.coerceIn(0, if (continueAfterGoal) Int.MAX_VALUE else COW_GOAL)
        return if (next == count) this else copy(
            count = next,
            previousCount = count
        )
    }

    fun undo(): Hunt = previousCount?.let {
        copy(count = it, previousCount = null)
    } ?: this

    fun reset(): Hunt = copy(count = 0, obtainedRelics = emptySet(), previousCount = null)
}
