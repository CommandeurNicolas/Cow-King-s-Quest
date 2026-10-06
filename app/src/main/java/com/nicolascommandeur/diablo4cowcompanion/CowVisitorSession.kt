package com.nicolascommandeur.diablo4cowcompanion

import kotlin.random.Random

/** One roll per process launch; navigation, recreation and resume never reroll it. */
internal class CowVisitorSession(
    val lucky: Boolean = Random.nextInt(666) == 0,
    private val nextLine: () -> Int = { Random.nextInt(8) }
) {
    private var foregroundMs = 0L
    private var visitNumber = -1L
    private var tapped = false
    private var line = "Moo."

    fun advance(elapsedMs: Long): CowVisit? {
        require(elapsedMs >= 0)

        if (!lucky) return null

        foregroundMs += elapsedMs
        if (foregroundMs < FIRST_VISIT_DELAY_MS) return null

        val sinceFirst = foregroundMs - FIRST_VISIT_DELAY_MS
        val number = sinceFirst / RETURN_INTERVAL_MS
        val age = sinceFirst % RETURN_INTERVAL_MS

        if (age >= VISIT_DURATION_MS) return null

        if (number != visitNumber) {
            visitNumber = number
            tapped = false
            line = if (number == 0L) "Moo." else when (nextLine()) {
                0 -> "You saw nothing."
                1 -> "Just passing through."
                else -> "Moo."
            }
        }

        return CowVisit(
            age,
            if (tapped) "There is no cow level." else line,
            tapped
        )
    }

    fun tap(): CowVisit? {
        tapped = true
        return advance(0)
    }

    companion object {
        const val FIRST_VISIT_DELAY_MS = 600L
        const val VISIT_DURATION_MS = 9_500L
        const val RETURN_INTERVAL_MS = 5 * 60_000L
    }
}

internal data class CowVisit(val ageMs: Long, val line: String, val tapped: Boolean) {
    val entering: Boolean get() = ageMs in WALK_START_MS until ARRIVE_MS
    val leaving: Boolean get() = ageMs >= EXIT_START_MS
    val showBubble: Boolean get() = tapped || ageMs in ARRIVE_MS until EXIT_START_MS
    val frame: Int
        get() = if (entering || leaving) {
            (ageMs / 100 % 8).toInt()
        } else 0

    // Reveal the head, then hold it above the navigation edge for two seconds.
    val peekFraction: Float get() = (ageMs / 650f).coerceIn(0f, 1f)
    val enterFraction: Float
        get() =
            ((ageMs - WALK_START_MS).toFloat() / (ARRIVE_MS - WALK_START_MS)).coerceIn(0f, 1f)
    val exitFraction: Float
        get() =
            ((ageMs - EXIT_START_MS).toFloat() / (CowVisitorSession.VISIT_DURATION_MS - EXIT_START_MS))
                .coerceIn(0f, 1f)

    companion object {
        const val WALK_START_MS = 2_650L
        const val ARRIVE_MS = 4_650L
        const val EXIT_START_MS = 7_450L
    }
}

/** Storage callbacks keep the launch cycle testable without an Android process. */
internal class CowVisitorLaunchCounter(
    private val loadCount: () -> Int,
    private val saveCount: (Int) -> Unit,
    private val rollLucky: () -> Boolean = { Random.nextInt(666) == 0 }
) {
    // Read, increment and roll only once, even if the activity is recreated.
    val session: CowVisitorSession by lazy {
        val count = loadCount().coerceIn(0, 665) + 1
        val guaranteed = count == 666
        saveCount(if (guaranteed) 0 else count)
        CowVisitorSession(lucky = guaranteed || rollLucky())
    }
}
