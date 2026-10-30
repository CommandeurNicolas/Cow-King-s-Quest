package com.nicolascommandeur.cowkingsquest

import com.nicolascommandeur.cowkingsquest.model.hunt.Relic
import com.nicolascommandeur.cowkingsquest.model.visitor.CowVisitorLaunchCounter
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertSame
import org.junit.Assert.assertTrue
import org.junit.Test

class CowVisitorLaunchCounterTest {
    @Test
    fun collectionMilestoneSurvivesNewLaunchesIncludingAnInterruptedVisit() {
        var count = 0
        var used = false
        var writes = 0

        fun launch() = CowVisitorLaunchCounter(
            loadCount = { count },
            saveCount = { count = it },
            rollLucky = { false },
            loadCelebrationUsed = { used },
            saveCelebrationUsed = {
                used = true
                writes++
            }
        ).session

        val all = Relic.entries.toSet()
        assertTrue(launch().onRelicsChanged(all - Relic.FRAGMENT, all))
        assertTrue(used)

        // Simulate process death before the first visit even finishes.
        val restarted = launch()
        assertFalse(restarted.onRelicsChanged(all - Relic.FRAGMENT, all))
        assertNull(restarted.advance(0))
        assertFalse(restarted.onRelicsChanged(emptySet(), all))
        assertEquals(1, writes)
    }

    @Test
    fun storedCountGuaranteesEvery666thLaunchAndRestartsTheCycle() {
        var storedCount = 0
        var randomRolls = 0
        // Each new instance represents a new process reading the saved count.
        repeat(1_333) { index ->
            val launch = index + 1
            val session = CowVisitorLaunchCounter(
                loadCount = { storedCount },
                saveCount = { storedCount = it },
                rollLucky = { randomRolls++; false }
            ).session

            assertEquals("Launch $launch", launch % 666 == 0, session.lucky)
            assertEquals(launch % 666, storedCount)
        }
        assertEquals(1_331, randomRolls)
    }

    @Test
    fun activityRecreationNeitherIncrementsNorRerolls() {
        var storedCount = 664
        var writes = 0
        var rolls = 0
        val counter = CowVisitorLaunchCounter(
            loadCount = { storedCount },
            saveCount = { storedCount = it; writes++ },
            rollLucky = { rolls++; false }
        )
        val session = counter.session

        repeat(10) { assertSame(session, counter.session) }
        assertFalse(session.lucky)
        assertEquals(665, storedCount)
        assertEquals(1, writes)
        assertEquals(1, rolls)
    }

    @Test
    fun randomVisitDoesNotResetTheGuaranteedLaunchCounter() {
        var storedCount = 664
        val randomVisit = CowVisitorLaunchCounter(
            loadCount = { storedCount },
            saveCount = { storedCount = it },
            rollLucky = { true }
        ).session

        assertTrue(randomVisit.lucky)
        assertEquals(665, storedCount)

        val guaranteedVisit = CowVisitorLaunchCounter(
            loadCount = { storedCount },
            saveCount = { storedCount = it },
            rollLucky = { error("A guaranteed visit must not depend on randomness") }
        ).session

        assertTrue(guaranteedVisit.lucky)
        assertEquals(0, storedCount)
    }
}
