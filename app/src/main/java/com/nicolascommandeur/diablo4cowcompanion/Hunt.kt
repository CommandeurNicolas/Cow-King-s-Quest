package com.nicolascommandeur.diablo4cowcompanion

const val COW_GOAL = 666

enum class Relic(val title: String, val shortName: String, val zones: List<String>) {
    SHARD(
        "Bloody Wooden Shard",
        "Shard",
        listOf("Hawezar", "Kehjistan"),
    ),
    TOME(
        "Musty Tome",
        "Tome",
        listOf("Scosglen", "Fractured Peaks"),
    ),
    FRAGMENT(
        "Intricate Metallic Fragment",
        "Fragment",
        listOf("Dry Steppes"),
    );

    val zoneLabel: String get() = zones.joinToString(" / ")
    val zoneInstruction: String get() = zones.joinToString(" or ")
}

data class Hunt(
    val relic: Relic,
    val name: String,
    val count: Int = 0,
    val collected: Boolean = false,
    val previousCount: Int? = null
) {
    // Bulk taps never consume the final kill: it must be recorded separately.
    fun add(amount: Int): Hunt =
        if (count >= COW_GOAL - 1 || amount <= 0) this
        else {
            setCount((count.toLong() + amount).coerceAtMost((COW_GOAL - 1).toLong()).toInt())
        }

    fun recordFinalKill(): Hunt = if (count == COW_GOAL - 1) setCount(COW_GOAL) else this

    fun setCount(value: Int): Hunt {
        val next = value.coerceIn(0, COW_GOAL)
        return if (next == count) this else copy(
            count = next,
            previousCount = count,
            collected = collected && next == COW_GOAL
        )
    }

    fun undo(): Hunt = previousCount?.let {
        copy(count = it, previousCount = null, collected = collected && it == COW_GOAL)
    } ?: this

    fun markCollected(value: Boolean): Hunt = copy(collected = value && count == COW_GOAL)
    fun reset(): Hunt = copy(count = 0, collected = false, previousCount = null)
}

data class HuntState(
    val hunts: List<Hunt> = Relic.entries.mapIndexed { index, relic ->
        Hunt(
            relic,
            "Character ${index + 1}"
        )
    },
    val selected: Int = 0,
    val keepAwake: Boolean = false
)
