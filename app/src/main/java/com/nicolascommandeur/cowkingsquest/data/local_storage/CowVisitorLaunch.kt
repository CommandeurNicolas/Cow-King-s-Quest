package com.nicolascommandeur.cowkingsquest.data.local_storage

import android.content.Context
import androidx.core.content.edit
import com.nicolascommandeur.cowkingsquest.model.hunt.Relic
import com.nicolascommandeur.cowkingsquest.model.visitor.CowVisitorLaunchCounter
import com.nicolascommandeur.cowkingsquest.model.visitor.CowVisitorSession

internal object CowVisitorLaunch {
    private var counter: CowVisitorLaunchCounter? = null
    val previewSession by lazy { CowVisitorSession(lucky = true) }

    @Synchronized
    fun session(context: Context): CowVisitorSession {
        val current = counter ?: run {
            val appContext = context.applicationContext
            val prefs = appContext.getSharedPreferences(
                "cow_visitor_v1",
                Context.MODE_PRIVATE
            )

            CowVisitorLaunchCounter(
                loadCount = {
                    prefs.getInt(
                        "launch_count",
                        0
                    )
                },
                saveCount = { count ->
                    prefs.edit {
                        putInt(
                            "launch_count",
                            count
                        )
                    }
                },
                loadCelebrationUsed = {
                    if (!prefs.contains("collection_celebrated")) {
                        // Existing completed collections must not become replayable on upgrade.
                        val alreadyComplete = HuntStore(appContext).load()
                            .collectedRelics.containsAll(Relic.entries)
                        prefs.edit { putBoolean("collection_celebrated", alreadyComplete) }
                    }
                    prefs.getBoolean("collection_celebrated", false)
                },
                saveCelebrationUsed = {
                    prefs.edit(commit = true) { putBoolean("collection_celebrated", true) }
                }
            ).also { counter = it }
        }
        return current.session
    }
}
