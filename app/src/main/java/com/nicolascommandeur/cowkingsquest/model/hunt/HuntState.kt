package com.nicolascommandeur.cowkingsquest.model.hunt

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
