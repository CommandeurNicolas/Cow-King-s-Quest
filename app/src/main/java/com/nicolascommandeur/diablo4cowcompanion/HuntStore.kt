package com.nicolascommandeur.diablo4cowcompanion

import android.content.Context
import androidx.core.content.edit

/** One private, offline snapshot; each relic slot has a stable storage key. */
class HuntStore(context: Context) {
    private val prefs = context.getSharedPreferences("cow_hunts_v1", Context.MODE_PRIVATE)

    fun load(): HuntState = HuntState(
        hunts = Relic.entries.mapIndexed { index, relic ->
            val key = relic.name
            val count = prefs.getInt("${key}_count", 0).coerceIn(0, COW_GOAL)
            Hunt(
                relic = relic,
                name = prefs.getString("${key}_name", null)?.takeIf { it.isNotBlank() }
                    ?: "Character ${index + 1}",
                count = count,
                collected = prefs.getBoolean("${key}_collected", false) && count == COW_GOAL,
                previousCount = prefs.getInt("${key}_previous", -1).takeIf { it in 0..COW_GOAL }
            )
        },
        selected = prefs.getInt("selected", 0).coerceIn(0, Relic.entries.lastIndex),
        keepAwake = prefs.getBoolean("keep_awake", false)
    )

    fun save(state: HuntState) {
        prefs.edit {
            putInt("selected", state.selected)
            putBoolean("keep_awake", state.keepAwake)
            state.hunts.forEach { hunt ->
                val key = hunt.relic.name
                putString("${key}_name", hunt.name)
                putInt("${key}_count", hunt.count)
                putInt("${key}_previous", hunt.previousCount ?: -1)
                putBoolean("${key}_collected", hunt.collected)
            }
        }
    }
}
