package com.nicolascommandeur.cowkingsquest.data.local_storage

import android.content.Context
import androidx.core.content.edit
import com.nicolascommandeur.cowkingsquest.data.guide.guideStepIds

class GuideStore(context: Context) {
    // Separate from cow_hunts_v1: checking or resetting the guide never changes counters.
    private val prefs = context.getSharedPreferences("cow_guide_v1", Context.MODE_PRIVATE)
    fun load(): Set<String> = prefs.getStringSet("completed_steps", emptySet())
        .orEmpty().intersect(guideStepIds)

    fun save(completed: Set<String>) {
        prefs.edit { putStringSet("completed_steps", completed.intersect(guideStepIds)) }
    }
}
