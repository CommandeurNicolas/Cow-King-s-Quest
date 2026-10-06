package com.nicolascommandeur.diablo4cowcompanion

import android.content.Context
import androidx.core.content.edit

internal object CowVisitorLaunch {
    private var counter: CowVisitorLaunchCounter? = null
    val previewSession by lazy { CowVisitorSession(lucky = true) }

    @Synchronized
    fun session(context: Context): CowVisitorSession {
        val current = counter ?: run {
            val prefs = context.applicationContext.getSharedPreferences(
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
                }
            ).also { counter = it }
        }
        return current.session
    }
}
