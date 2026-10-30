package com.nicolascommandeur.cowkingsquest

import android.content.Context
import androidx.core.content.edit

/** One private, offline snapshot; each relic slot has a stable storage key. */
class HuntStore(context: Context) {
    private val prefs = context.getSharedPreferences("cow_hunts_v1", Context.MODE_PRIVATE)

    fun load(): HuntState {
        val strategy = CharacterStrategy.entries.firstOrNull {
            it.name == prefs.getString("strategy", null)
        } ?: CharacterStrategy.THREE_CHARACTERS

        val selected = prefs.getInt("selected", 0).coerceIn(0, Relic.entries.lastIndex)
        val singleCharacter = prefs.getInt(
            "single_character",
            selected
        ).coerceIn(0, Relic.entries.lastIndex)

        val collectedRelics = Relic.entries.filter {
            prefs.getBoolean("${it.name}_collected", false)
        }.toSet()

        return HuntState(
            hunts = Relic.entries.mapIndexed { index, relic ->
                val key = relic.name
                val count = prefs.getInt("${key}_count", 0).coerceAtLeast(0)

                Hunt(
                    relic = relic,
                    name = prefs.getString("${key}_name", null)?.takeIf { it.isNotBlank() }
                        ?: "Character ${index + 1}",
                    count = count,
                    previousCount = prefs.getInt("${key}_previous", -1).takeIf { it >= 0 },
                    obtainedRelics = if (prefs.contains("${key}_obtained_relics")) {
                        val saved =
                            prefs.getStringSet("${key}_obtained_relics", emptySet()).orEmpty()
                        Relic.entries.filter { it.name in saved }.toSet()
                    } else if (prefs.getBoolean("${key}_first_relic", relic in collectedRelics)) {
                        // Older saves only recorded a flag. Infer ownership from the saved strategy
                        // and checked relics; an unchecked pickup must not leave a stale lock.
                        if (strategy == CharacterStrategy.ONE_CHARACTER && index == singleCharacter)
                            collectedRelics else setOf(relic).intersect(collectedRelics)
                    } else emptySet()
                )
            },
            selected = selected,
            keepAwake = prefs.getBoolean("keep_awake", false),
            strategy = strategy,
            cowSource = CowSource.entries.firstOrNull {
                it.name == prefs.getString(
                    "cow_source",
                    null
                )
            }
                ?: CowSource.WORLD_COWS,
            singleCharacter = singleCharacter,
            singleTarget = Relic.entries.firstOrNull {
                it.name == prefs.getString(
                    "single_target",
                    null
                )
            }
                ?: Relic.SHARD,
            collectedRelics = collectedRelics
        )
    }

    fun save(state: HuntState) {
        prefs.edit {
            putInt("selected", state.selected)
            putBoolean("keep_awake", state.keepAwake)
            putString("strategy", state.strategy.name)
            putString("cow_source", state.cowSource.name)
            putInt("single_character", state.singleCharacter)
            putString("single_target", state.singleTarget.name)

            state.hunts.forEach { hunt ->
                val key = hunt.relic.name
                putString("${key}_name", hunt.name)
                putInt("${key}_count", hunt.count)
                putInt("${key}_previous", hunt.previousCount ?: -1)
                putBoolean("${key}_collected", hunt.relic in state.collectedRelics)
                putBoolean("${key}_first_relic", hunt.firstRelicObtained)
                putStringSet("${key}_obtained_relics", hunt.obtainedRelics.map { it.name }.toSet())
            }
        }
    }
}
