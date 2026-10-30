package com.nicolascommandeur.cowkingsquest.model.visitor

import com.nicolascommandeur.cowkingsquest.model.hunt.Relic
import kotlin.random.Random

/** One roll per process launch; navigation, recreation and resume never reroll it. */
internal class CowVisitorSession(
    val lucky: Boolean = Random.nextInt(666) == 0,
    private val nextLine: () -> Int = { Random.nextInt(8) },
    celebrationUsed: Boolean = false,
    private val onCelebrationUsed: () -> Unit = {}
) {
    private var foregroundMs = 0L
    private var visitNumber = -1L
    private var tapped = false
    private var line = "Moo."
    private var celebrationAgeMs: Long? = null
    private var celebrationTapped = false
    private var celebrationUsed = celebrationUsed

    val canVisit: Boolean get() = lucky || celebrationAgeMs != null

    /** Called only by counter edits, never by loading progress or guide checkboxes. */
    fun onRelicsChanged(previous: Set<Relic>, current: Set<Relic>): Boolean {
        if (previous.containsAll(Relic.entries) || !current.containsAll(Relic.entries)) return false
        if (celebrationUsed) return false
        // Consume the milestone immediately, even if the app closes during the visit.
        celebrationUsed = true
        onCelebrationUsed()
        celebrationAgeMs = 0L
        celebrationTapped = false
        return true
    }

    fun advance(elapsedMs: Long): CowVisit? {
        require(elapsedMs >= 0)

        foregroundMs += elapsedMs

        celebrationAgeMs?.let { age ->
            val nextAge = age + elapsedMs
            celebrationAgeMs = nextAge.takeIf { it < VISIT_DURATION_MS }
            if (celebrationAgeMs != null) {
                return CowVisit(
                    nextAge,
                    if (celebrationTapped) "There is no cow level." else "Moo.",
                    celebrationTapped,
                    counterOnly = true
                )
            }
        }

        if (!lucky) return null
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
        if (celebrationAgeMs != null) celebrationTapped = true else tapped = true
        return advance(0)
    }

    companion object {
        const val FIRST_VISIT_DELAY_MS = 600L
        const val VISIT_DURATION_MS = 9_500L
        const val RETURN_INTERVAL_MS = 5 * 60_000L
    }
}
