package com.nicolascommandeur.diablo4cowcompanion

const val COW_GOAL = 666
const val ZONE_REMINDER_COUNT = 600

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

enum class CharacterStrategy(val title: String) {
    THREE_CHARACTERS("Three characters"),
    ONE_CHARACTER("One character")
}

enum class CowSource(val title: String) {
    WORLD_COWS("World cows"),
    BELL_OF_THE_BOVINE("Bell of the Bovine")
}

data class HuntState(
    val hunts: List<Hunt> = Relic.entries.mapIndexed { index, relic ->
        Hunt(
            relic,
            "Character ${index + 1}"
        )
    },
    val selected: Int = 0,
    val keepAwake: Boolean = false,
    val strategy: CharacterStrategy = CharacterStrategy.THREE_CHARACTERS,
    val cowSource: CowSource = CowSource.WORLD_COWS,
    val singleCharacter: Int = 0,
    val singleTarget: Relic = Relic.SHARD,
    val collectedRelics: Set<Relic> = emptySet()
) {
    val activeIndex: Int get() = if (strategy == CharacterStrategy.ONE_CHARACTER) singleCharacter else selected
    val activeHunt: Hunt get() = hunts[activeIndex]
    val targetRelic: Relic get() = if (strategy == CharacterStrategy.ONE_CHARACTER) singleTarget else activeHunt.relic
    val isRngPhase: Boolean get() = strategy == CharacterStrategy.ONE_CHARACTER && activeHunt.pastFirstDrop

    fun updateActive(next: Hunt): HuntState = copy(hunts = hunts.mapIndexed { index, old ->
        if (index == activeIndex) next else old
    })

    fun markRelic(relic: Relic, collected: Boolean): HuntState {
        val next = if (collected) collectedRelics + relic else collectedRelics - relic
        return copy(
            // A shared checkbox can be changed while a different character is selected.
            hunts = hunts.mapIndexed { index, hunt ->
                when {
                    !collected -> hunt.copy(obtainedRelics = hunt.obtainedRelics - relic)
                    relic !in collectedRelics && index == activeIndex ->
                        hunt.copy(obtainedRelics = hunt.obtainedRelics + relic)

                    else -> hunt
                }
            },
            collectedRelics = next,
            singleTarget = if (collected && singleTarget == relic)
                Relic.entries.firstOrNull { it !in next } ?: singleTarget
            else singleTarget
        )
    }
}
