package com.nicolascommandeur.cowkingsquest.model.visitor

import kotlin.random.Random

/** Storage callbacks keep the launch cycle testable without an Android process. */
internal class CowVisitorLaunchCounter(
    private val loadCount: () -> Int,
    private val saveCount: (Int) -> Unit,
    private val rollLucky: () -> Boolean = { Random.nextInt(666) == 0 },
    private val loadCelebrationUsed: () -> Boolean = { false },
    private val saveCelebrationUsed: () -> Unit = {}
) {
    // Read, increment and roll only once, even if the activity is recreated.
    val session: CowVisitorSession by lazy {
        val count = loadCount().coerceIn(0, 665) + 1
        val guaranteed = count == 666
        saveCount(if (guaranteed) 0 else count)
        CowVisitorSession(
            lucky = guaranteed || rollLucky(),
            celebrationUsed = loadCelebrationUsed(),
            onCelebrationUsed = saveCelebrationUsed
        )
    }
}
