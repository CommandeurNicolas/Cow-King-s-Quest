package com.nicolascommandeur.cowkingsquest.model.visitor

internal data class CowVisit(
    val ageMs: Long,
    val line: String,
    val tapped: Boolean,
    val counterOnly: Boolean = false
) {
    fun isVisible(isCounterScreen: Boolean): Boolean = !counterOnly || isCounterScreen

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
